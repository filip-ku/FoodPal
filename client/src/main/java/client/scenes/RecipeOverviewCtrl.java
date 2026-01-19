package client.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.utils.FavoritesManager;
import client.utils.RecipeFormatter;
import com.google.inject.Inject;

import client.ws.WebSocketService;
import client.utils.ServerUtils;
import client.utils.ConfigUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import commons.ws.RecipeChangedEvent;
import commons.ws.RecipeContentChangedEvent;
import commons.ws.RecipeListEvent;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.springframework.messaging.simp.stomp.StompSession;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Set;
import java.util.stream.Collectors;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final WebSocketService webSocketService;
    private final FavoritesManager favoritesManager;

    private ObservableList<Recipe> data;
    private ObservableList<Recipe> allRecipes; // Unfiltered list of all recipes

    private StompSession.Subscription recipeContentSubscription;

    @FXML
    private ResourceBundle resources;

    @FXML
    private TableView<Recipe> tableRecipes;
    @FXML
    private TableColumn<Recipe, String> colRecipes;

    @FXML
    private TableView<RecipeIngredient> tableIngredients;
    @FXML
    private TableColumn<RecipeIngredient, String> colIngredients;
    @FXML
    private TableColumn<RecipeIngredient, String> colAmount;

    @FXML
    private TableView<RecipeStep> tablePreparation;
    @FXML
    private TableColumn<RecipeStep, String> colPreparation;
    @FXML
    private Button favButton;
    @FXML
    private CheckBox filterFavorites;

    /**
     * Language indicator showing current choice and opening the language menu.
     */
    @FXML
    private MenuButton languageMenu;

    @FXML
    private Label recipeName;
    @FXML
    private TextField recipeEditBox;
    @FXML
    private Button recipeEditButton;
    private boolean editingName = false;

    @FXML
    private Button addRecipeStep;
    @FXML
    private Button removeStepButton;
    @FXML
    private Button editStepsButton;

    @FXML
    private Button recipeIngredientAdd;
    @FXML
    private Button recipeIngredientDelete;
    @FXML
    private Button recipeIngredientEditButton;

    @FXML
    private Button downloadRecipeButton;

    @FXML
    private TextField searchField;

    @FXML
    private HBox scaleHBox;
    @FXML
    private TextField scaleFactorField;

    @FXML
    private Label estimatedKcalLabel;

    @FXML
    private Label servingsLabel;

    @FXML
    private CheckBox filterEnglish;
    @FXML
    private CheckBox filterDutch;
    @FXML
    private CheckBox filterSpanish;
    @FXML
    private CheckBox filterFrench;

    /**
     * Constructs a {@code RecipeOverviewCtrl}.
     *
     * <p>Instances are created via Guice injection. The controller receives a
     * reference to {@link ServerUtils} for server communication and a
     * {@link MainCtrl} for navigation.</p>
     *
     * @param server  injected {@link ServerUtils}
     * @param mainCtrl injected {@link MainCtrl}
     * @param webSocketService injected {@link WebSocketService}
     * @param favoritesManager injected {@link FavoritesManager}
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl,
                              WebSocketService webSocketService, FavoritesManager favoritesManager){
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.webSocketService = webSocketService;
        this.favoritesManager = favoritesManager;
    }

    /**
     * Called by the JavaFX framework after the FXML elements have been injected.
     *
     * <p>Initialises UI bindings and listeners, then shows the default
     * "main menu" view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        showMainMenu();
        setupLanguageMenu();
        loadRecipeLanguageFilter();

        colRecipes.setCellFactory(column -> new TableCell<Recipe, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Recipe recipe = getTableView().getItems().get(getIndex());
                    setText(item);

                    if (favoritesManager.isFavorite(recipe.getId())) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #DAA520;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        colRecipes.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));

        colIngredients.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getIngredient().getName())
        );

        colAmount.setCellValueFactory(cell -> {
            var ri = cell.getValue();
            return loadAmountsForRecipeIngredient(ri);
        });

        colPreparation.setCellValueFactory(cell ->
                new SimpleStringProperty(formatStepForDisplay(cell.getValue())));

        tableRecipes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {

                    // Cancel any active title editing when switching recipes
                    if (editingName) {
                        cancelTitleEditing();
                    }
                    
                    if (newSel != null) {
                        if (newSel.getIngredients() != null) {
                            tableIngredients.setItems(
                                    FXCollections.observableArrayList(newSel.getIngredients()));
                        } else {
                            tableIngredients.getItems().clear();
                        }

                        loadRecipeOverviewUI(newSel);
                        loadStepsForRecipe(newSel);
                        subscribeToRecipeContent(newSel.getId());
                        reloadSelectedRecipeDetails(newSel);
                    }
                });

        searchField.textProperty().addListener((obs, old, query) -> {
            filterRecipes(query);
        });

        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
            }
        });

        recipeIngredientEditButton.setDisable(true);
        editStepsButton.setDisable(true);

        tableIngredients.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    recipeIngredientEditButton.setDisable(newSel == null);
                });
        tablePreparation.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    editStepsButton.setDisable(newSel == null);
                });

        scaleFactorField.textProperty().addListener((obs, oldVal, newVal) -> {
            tableIngredients.refresh();
            refresh();
        });

        setupWebSocketSubscriptions();
    }

    /**
     * Loads the amount column of the ingredients table with the correct values
     * Also normalizes units when the user scales the recipe
     * (1000g becomes 1kg for example)
     *
     * @param ri RecipeIngredient to load amounts for
     * @return SimpleStringProperty containing the amount to display
     */
    private SimpleStringProperty loadAmountsForRecipeIngredient(RecipeIngredient ri) {
        double factor = 1.0;

        try {
            factor = Double.parseDouble(scaleFactorField.getText());
        } catch (NumberFormatException e) {
            factor = 1.0;
        }

        if (factor <= 0) {
            factor = 1.0;
        }

        boolean hasFormal = ri.getAmount() != null &&
                ri.getAmount() != 0 &&
                ri.getUnit() != null;

        if (hasFormal) {
            double scaledAmount = factor * ri.getAmount();
            String displayUnit = ri.getUnit();

            if (scaledAmount >= 3) {
                switch (displayUnit) {
                    case "g" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "kg";
                            if (scaledAmount >= 1000) {
                                scaledAmount /= 1000;
                                displayUnit = "ton";
                            }
                        }
                    }
                    case "kg" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "ton";
                        }
                    }
                    case "mL" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "L";
                            if (scaledAmount >= 1000) {
                                scaledAmount /= 1000;
                                displayUnit = "kL";
                            }
                        }
                    }
                    case "L" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "kL";
                        }
                    }
                    case "tsp" -> {
                        scaledAmount /= 3;
                        displayUnit = "tbsp";
                        if (scaledAmount >= 16) {
                            scaledAmount /= 16;
                            displayUnit = "cup";
                        }
                    }
                    case "tbsp" -> {
                        if (scaledAmount >= 16) {
                            scaledAmount /= 16;
                            displayUnit = "cup";
                        }
                    }
                    default -> {
                        break;
                    }
                }
            }
            return new SimpleStringProperty(scaledAmount + " " + displayUnit);
        } else {
            String informalAmount = ri.getInformalAmount();

            if (factor > 1.0) {
                return new SimpleStringProperty(informalAmount + " (x" + factor + ")");
            }
            return new SimpleStringProperty(informalAmount);
        }
    }

    /**
     * Configures the language indicator/dropdown with the available options.
     * Loads the saved UI language from config if available.
     */
    private void setupLanguageMenu() {
        languageMenu.getItems().clear();

        for (LanguageOption option : supportedLanguages) {
            MenuItem item = new MenuItem(option.name);
            item.setGraphic(createFlagGraphic(option.iconPath, 16));
            item.setOnAction(e -> setCurrentLanguage(option, true));
            languageMenu.getItems().add(item);
        }

        // Load saved UI language from config (defaults to "en" if not set)
        String savedLanguageCode = ConfigUtils.getUILanguage();
        LanguageOption savedLanguage = supportedLanguages.stream()
                .filter(opt -> opt.code.equals(savedLanguageCode))
                .findFirst()
                .orElse(null);

        if (savedLanguage != null) {
            setCurrentLanguage(savedLanguage, false);
        } else if (!supportedLanguages.isEmpty()) {
            // Fallback to first language if saved language code is invalid
            setCurrentLanguage(supportedLanguages.get(0), false);
        }
    }

    /**
     * Updates the indicator text and flag to the chosen language.
     * Persists the selection to the config file and optionally reloads the scene.
     *
     * @param option selected language option
     * @param shouldReload whether to reload the
     *                     scene (true for user-initiated changes, false for initial setup)
     */
    private void setCurrentLanguage(LanguageOption option, boolean shouldReload) {
        this.currentLanguage = option;
        languageMenu.setText(option.name);
        languageMenu.setGraphic(createFlagGraphic(option.iconPath, 16));
        // Persist UI language choice
        ConfigUtils.setUILanguage(option.code);
        // Reload the scene with the new language (only if user changed it)
        if (shouldReload) {
            mainCtrl.reloadRecipeOverview();
        }
    }

    /**
     * Navigates to the “Add Recipe” screen.
     */
    public void addRecipe() {
        mainCtrl.showAddRecipe();
    }

    /**
     * clones an existing recipe (making a copy)
     */
    @FXML
    public void cloneRecipe() {

        Recipe original = tableRecipes.getSelectionModel().getSelectedItem();
        if(original == null){
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        Recipe clone;
        String copySuffix = " " + resources.getString("recipeOverview.label.copySuffix");
        if(original.getServings() != null){
            clone = new Recipe(original.getTitle() + copySuffix, original.getServings());
        }
        else{
            clone = new Recipe(original.getTitle() + copySuffix);
        }

        // Preserve the language from the original recipe
        clone.setLanguage(original.getLanguage());

        for(RecipeIngredient ri : original.getIngredients()){
            RecipeIngredient riClone =
                    new RecipeIngredient(clone, ri.getIngredient(), ri.getPosition());

            riClone.setAmount(ri.getAmount());
            riClone.setUnit(ri.getUnit());
            riClone.setInformalAmount(ri.getInformalAmount());
            riClone.setNote(ri.getNote());

            clone.addRecipeIngredient(riClone);
        }

        for (RecipeStep step : original.getSteps()){
            RecipeStep stepClone = new RecipeStep(clone, step.getPosition(), step.getInstruction());

            clone.addStep(stepClone);
        }

        try{
            server.addRecipe(clone);
        }
        catch (WebApplicationException e){
            mainCtrl.showError(e.getMessage());
            return;
        }

        refresh();
    }

    /**
     * Gets the currently selected recipe.
     *
     * @return the selected recipe, or null if none is selected
     */
    public Recipe getSelectedRecipe() {
        return tableRecipes.getSelectionModel().getSelectedItem();
    }

    /**
     * Selects the given recipe in the table by matching ID.
     *
     * @param recipe the recipe to select
     */
    public void selectRecipe(Recipe recipe) {
        if (recipe == null || recipe.getId() == null) return;

        // Find the recipe in the current data by ID
        for (Recipe r : data) {
            if (r.getId().equals(recipe.getId())) {
                tableRecipes.getSelectionModel().select(r);
                loadStepsForRecipe(r);
                break;
            }
        }
    }

    /**
     * Refreshes the recipe list from the server and updates the table view.
     */
    public void refresh() {
        try {
            Long selectedId;
            Recipe selectedBefore = tableRecipes.getSelectionModel().getSelectedItem();
            if (selectedBefore != null) {
                selectedId = selectedBefore.getId();
            } else {
                selectedId = null;
            }

            var recipes = server.getRecipes();

            Set<Long> currentRecipeIds = new HashSet<>();
            for (Recipe recipe : recipes) {
                currentRecipeIds.add(recipe.getId());
                // Mark favorites
                recipe.setFavorite(favoritesManager.isFavorite(recipe.getId()));
            }

            // Check for deleted favorites and warn user
            Set<Long> favoriteIds = new HashSet<>(favoritesManager.getFavoriteIds());
            favoriteIds.removeAll(currentRecipeIds);
            if (!favoriteIds.isEmpty()) {
                favoritesManager.cleanupDeletedRecipes(favoriteIds);
                mainCtrl.showError(resources.getString("recipeOverview.warning.favoritesDeleted")
                        .replace("{0}", String.valueOf(favoriteIds.size())));
            }

            allRecipes = FXCollections.observableList(recipes);
            applyLanguageFilter(); // Apply current filter state

            if (selectedId != null) {
                data.stream()
                        .filter(r -> selectedId.equals(r.getId()))
                        .findFirst()
                        .ifPresent(r -> tableRecipes.getSelectionModel().select(r));
            }

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                recipeName.setText(selected.getTitle());
                reloadSelectedRecipeDetails(selected);
            }
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Filters the currently loaded recipes based on the given search query.
     * <p>The following recipe fields are searched:</p>
     * <ul>
     *     <li>Recipe title</li>
     *     <li>Ingredient names</li>
     *     <li>Preparation step instructions</li>
     * </ul>
     * @param query the search string entered by the user
     */
    private void filterRecipes(String query) {
        if (data == null) {
            return;
        }

        if (query == null || query.isBlank()) {
            tableRecipes.setItems(data);
            return;
        }

        List<Recipe> filtered = data.stream()
                .filter(recipe -> matchesSearch(recipe, query))
                .collect(Collectors.toList());

        tableRecipes.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Determines whether a recipe matches the given search query.
     * The query is evaluated using AND semantics.
     * @param recipe the recipe to test
     * @param query the search query entered by the user
     * @return true if the recipe matches the query, false otherwise.
     */
    private boolean matchesSearch(Recipe recipe, String query) {
        if (recipe == null) {
            return false;
        }

        if (query == null || query.isBlank()) {
            return true;
        }

        String[] tokens = query.toLowerCase().trim().split("\\s+");

        StringBuilder searchable = new StringBuilder();

        // Recipe title
        if (recipe.getTitle() != null) {
            searchable.append(recipe.getTitle()).append(" ");
        }

        // Ingredient names
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                if (ri != null && ri.getIngredient() != null
                        && ri.getIngredient().getName() != null) {
                    searchable.append(ri.getIngredient().getName()).append(" ");
                }
            }
        }

        // Preparation steps
        if (recipe.getSteps() != null) {
            for (RecipeStep step : recipe.getSteps()) {
                if (step != null && step.getInstruction() != null) {
                    searchable.append(step.getInstruction()).append(" ");
                }
            }
        }

        String searchableText = searchable.toString().toLowerCase();

        // AND semantics: all tokens must match
        for (String token : tokens) {
            if (!searchableText.contains(token)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Filters recipes based on selected language checkboxes.
     * If no languages are selected, all recipes are shown.
     * Persists the filter selection to the config file.
     */
    @FXML
    public void applyLanguageFilter() {
        if (allRecipes == null) {
            return; // Recipes not loaded yet
        }

        // Check if filter checkboxes are initialized
        if (filterEnglish == null || filterDutch == null
                || filterSpanish == null || filterFrench == null) {
            return; // UI not fully initialized yet
        }

        // Collect selected language codes
        java.util.Set<String> selectedLanguages = new java.util.HashSet<>();
        if (filterEnglish.isSelected()) {
            selectedLanguages.add("en");
        }
        if (filterDutch.isSelected()) {
            selectedLanguages.add("nl");
        }
        if (filterSpanish.isSelected()) {
            selectedLanguages.add("es");
        }
        if (filterFrench.isSelected()) {
            selectedLanguages.add("fr");
        }

        boolean filterByFavorites = filterFavorites != null && filterFavorites.isSelected();
        // Persist recipe language filter selection
        ConfigUtils.setRecipeLanguageFilter(new ArrayList<>(selectedLanguages));

        // Filter recipes by selected languages
        List<Recipe> filtered = allRecipes.stream()
                .filter(recipe -> {
                    // Apply language filter
                    boolean languageMatch = selectedLanguages.isEmpty() ||
                            (recipe.getLanguage() != null &&
                                    selectedLanguages.contains(recipe.getLanguage()));

                    // Apply favorites filter
                    boolean favoriteMatch = !filterByFavorites ||
                            favoritesManager.isFavorite(recipe.getId());

                    return languageMatch && favoriteMatch;
                })
                .collect(Collectors.toList());
        data = FXCollections.observableList(filtered);
        tableRecipes.setItems(data);
        filterRecipes(searchField.getText());
    }

    /**
     * Loads the saved recipe language filter from config and applies it to the UI.
     * Also applies the filter to the recipe list if recipes are already loaded.
     */
    private void loadRecipeLanguageFilter() {
        // Check if filter checkboxes are initialized
        if (filterEnglish == null || filterDutch == null
                || filterSpanish == null || filterFrench == null) {
            return; // UI not fully initialized yet
        }

        List<String> savedFilter = ConfigUtils.getRecipeLanguageFilter();

        // Set checkboxes based on saved filter
        filterEnglish.setSelected(savedFilter.contains("en"));
        filterDutch.setSelected(savedFilter.contains("nl"));
        filterSpanish.setSelected(savedFilter.contains("es"));
        filterFrench.setSelected(savedFilter.contains("fr"));

        // Apply the filter if recipes are already loaded
        if (allRecipes != null) {
            applyLanguageFilter();
        }
    }

    /**
     * Loads and displays all recipe steps for the specified recipe.
     *
     * @param recipe the recipe whose steps should be shown
     */
    public void loadStepsForRecipe(Recipe recipe) {
        try {
            var steps = server.getStepsForRecipe(recipe.getId());
            ObservableList<RecipeStep> stepData = FXCollections.observableList(steps);
            tablePreparation.setItems(stepData);
            tablePreparation.refresh();
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Cancels the current title editing session without saving changes.
     * Closes the edit box and restores the label display.
     */
    private void cancelTitleEditing() {
        if (!editingName) {
            return;
        }
        
        editingName = false;
        recipeEditButton.setText(resources.getString("button.edit"));

        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            recipeName.setText(selected.getTitle());
        }
        
        recipeEditBox.setDisable(true);
        recipeEditBox.setVisible(false);
        recipeName.setVisible(true);
    }

    /**
     * Handles the Edit/Save button click for a recipe name.
     *
     * <p>When not in edit mode, switches to an editable {@link TextField}.
     * When already editing, validates the input and updates the model.</p>
     */
    public void editNameClicked() {
        if (!editingName) {
            editingName = true;
            recipeEditButton.setText(resources.getString("button.save"));

            recipeEditBox.setText(recipeName.getText());
            recipeEditBox.setDisable(false);
            recipeEditBox.setVisible(true);

            recipeName.setVisible(false);
        } else {
            String newName = recipeEditBox.getText();

            if (newName == null || newName.trim().isEmpty()) {
                mainCtrl.showError(resources.getString("recipeOverview.error.nameEmpty"));
                return;
            }
            recipeName.setText(newName);

            editingName = false;
            recipeEditButton.setText(resources.getString("button.edit"));

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setTitle(newName);
            }

            try {
                server.updateRecipe(selected);
            } catch (WebApplicationException e) {
                mainCtrl.showExceptionErrorPopUp(e);
                return;
            }

            tableRecipes.refresh();

            recipeEditBox.setDisable(true);
            recipeEditBox.setVisible(false);

            recipeName.setVisible(true);
        }
    }

    /**
     * Deletes the currently selected recipe after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the recipe
     * from the observable list and updates the table view.</p>
     */
    public void deleteRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.noRecipeSelected"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(resources.getString("recipeOverview.dialog.confirmDelete"));
        confirm.setHeaderText(resources.getString("recipeOverview.dialog.deleteRecipe"));
        confirm.setContentText(resources.getString("recipeOverview.dialog.deleteRecipeConfirm"));

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            server.deleteRecipe(selected);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

        showMainMenu();

        data.remove(selected);
    }

    /**
     * Opens RecipeStep scene if a valid recipe and corresponding step is selected
     */
    @FXML
    private void editSteps() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        RecipeStep selectedStep = tablePreparation.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectStepToEdit"));
            return;
        }
        mainCtrl.showEditRecipeStep(selectedRecipe, selectedStep);
    }

    /**
     * Deletes the currently selected ingredient after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the ingredient
     * from the observable list and updates the table view.</p>
     */
    @FXML
    public void deleteIngredient() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        RecipeIngredient selectedRecipeIngredient =
                tableIngredients.getSelectionModel().getSelectedItem();
        if (selectedRecipeIngredient == null) {
            mainCtrl.showError(resources
                    .getString("recipeOverview.error.selectIngredientToDelete"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(resources.getString("recipeOverview.dialog.confirmDelete"));
        confirm.setHeaderText(resources.getString("recipeOverview.dialog.deleteIngredient"));
        confirm.setContentText(resources
                .getString("recipeOverview.dialog.deleteIngredientConfirm"));

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            Recipe updatedRecipe =
                    server.deleteRecipeIngredient(selectedRecipe, selectedRecipeIngredient);
            int index = data.indexOf(selectedRecipe);
            if (index != -1) {
                data.set(index, updatedRecipe);
            }
            selectRecipe(updatedRecipe);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }

        tableIngredients.getItems().remove(selectedRecipeIngredient);
    }

    /**
     * Subscribes to global recipe list and title change topics so the UI can refresh automatically.
     * Retries are not implemented here; failures are logged to stderr.
     */
    private void setupWebSocketSubscriptions() {
        try {
            webSocketService.subscribeRecipeList(this::handleRecipeListEvent);
            webSocketService.subscribeRecipeChanged(this::handleRecipeChangedEvent);
        } catch (RuntimeException e) {
            System.err.println("WebSocket subscription failed: " + e.getMessage());
        }
    }

    /**
     * Handles list-level events (create/delete) by refreshing the full list.
     *
     * @param event event describing a list change
     */
    private void handleRecipeListEvent(RecipeListEvent event) {
        Platform.runLater(this::refresh);
    }

    /**
     * Handles title changes by refreshing the list (and reselecting).
     *
     * @param event event describing a recipe title change
     */
    private void handleRecipeChangedEvent(RecipeChangedEvent event) {
        Platform.runLater(this::refresh);
    }

    /**
     * Subscribes to content changes for the specified recipe, cancelling any previous subscription.
     *
     * @param recipeId the recipe to follow for ingredient/step updates
     */
    private void subscribeToRecipeContent(Long recipeId) {
        if (recipeContentSubscription != null) {
            recipeContentSubscription.unsubscribe();
            recipeContentSubscription = null;
        }
        if (recipeId == null) {
            return;
        }
        try {
            recipeContentSubscription = webSocketService.subscribeRecipeContent(
                    recipeId,
                    this::handleRecipeContentChangedEvent
            );
        } catch (RuntimeException e) {
            System.err.println("WebSocket content subscription failed: " + e.getMessage());
        }
    }

    /**
     * Reloads the currently selected recipe's details when its content changes.
     *
     * @param event event indicating which recipe changed
     */
    private void handleRecipeContentChangedEvent(RecipeContentChangedEvent event) {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getId() != null
                && selected.getId().equals(event.recipeId())) {
            Platform.runLater(() -> reloadSelectedRecipeDetails(selected));
        }
    }

    /**
     * Reloads ingredients and steps for the given recipe and updates the tables.
     *
     * @param recipe currently selected recipe
     */
    private void reloadSelectedRecipeDetails(Recipe recipe) {
        if (recipe == null) {
            return;
        }
        try {
            var ingredients = server.getRecipeIngredients(recipe);
            tableIngredients.getItems().clear();
            tableIngredients.setItems(FXCollections.observableArrayList(ingredients));
            loadStepsForRecipe(recipe);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Makes every component except the label invisible
     * Sets the label text to "Welcome to FoodPal!"
     */
    public void showMainMenu() {
        recipeEditButton.setVisible(false);
        recipeName.setText(resources.getString("recipeOverview.welcome"));
        tableIngredients.setVisible(false);
        tablePreparation.setVisible(false);
        recipeIngredientAdd.setVisible(false);
        recipeIngredientDelete.setVisible(false);
        downloadRecipeButton.setVisible(false);
        editStepsButton.setVisible(false);
        removeStepButton.setVisible(false);
        addRecipeStep.setVisible(false);
        recipeIngredientEditButton.setVisible(false);
        favButton.setVisible(false);

        scaleHBox.setVisible(false);
        estimatedKcalLabel.setVisible(false);
        servingsLabel.setVisible(false);
    }

    /**
     * Makes every component that the user should
     * be able to interact with when a recipe is selected visible
     *
     * @param newSel the recipe that is selected
     */
    public void loadRecipeOverviewUI(Recipe newSel) {
        recipeName.setText(newSel.getTitle());

        tableIngredients.setVisible(true);
        tablePreparation.setVisible(true);

        recipeEditButton.setVisible(true);
        recipeName.setVisible(true);
        updateFavoriteButton(newSel);

        recipeIngredientAdd.setVisible(true);
        recipeIngredientDelete.setVisible(true);

        downloadRecipeButton.setVisible(true);
        editStepsButton.setVisible(true);
        removeStepButton.setVisible(true);
        addRecipeStep.setVisible(true);
        recipeIngredientEditButton.setVisible(true);
        favButton.setVisible(true);
        scaleHBox.setVisible(true);
        estimatedKcalLabel.setVisible(true);

        double totalKcal = 0.0;

        for (RecipeIngredient ri : newSel.getIngredients()) {
            totalKcal += ri.getIngredient().getCalories();
        }

        estimatedKcalLabel.setText(resources.getString("recipeOverview.label.estimatedKcalValue")
                .replace("{0}", String.valueOf(totalKcal)));

        servingsLabel.setVisible(true);

        double factor = 1.0;

        try {
            factor = Double.parseDouble(scaleFactorField.getText());
        } catch (NumberFormatException e) {
            factor = 1.0;
        }

        if (factor <= 0.0) {
            factor = 1.0;
        }

        servingsLabel.setText(resources.getString("recipeOverview.label.servingsValue")
                .replace("{0}", String.valueOf(newSel.getServings().doubleValue() * factor)));
    }

    /**
     * Opens the Ingredient Overview scene
     */
    public void showIngredients() {
        mainCtrl.showIngredientsOverview();
    }

    /**
     * Opens the ingredient chooser for the currently selected recipe.
     * Shows an error if no recipe is selected.
     */
    @FXML
    private void openAddRecipeIngredient() {
        var selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }
        mainCtrl.showChooseRecipeIngredient(selected);
    }

    /**
     * Opens the “Add Recipe Step” screen for the selected recipe.
     * Shows an error if no recipe is selected.
     */
    @FXML
    private void openAddRecipeStep() {
        var selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }
        mainCtrl.showAddRecipeStep(selected);
    }

    /**
     * Returns a nicely formatted string representation of a {@link RecipeStep}.
     * Example: "1. Chop onions" or just "Chop onions" if position is invalid.
     *
     * @param step the recipe step to format
     * @return formatted string for display
     */
    private String formatStepForDisplay(RecipeStep step) {
        if (step == null) {
            return "";
        }

        String number = "";
        if (step.getPosition() >= 0) {
            number = String.valueOf(step.getPosition());
        }

        String text = "";
        if (step.getInstruction() != null) {
            text = step.getInstruction();
        }

        if (!number.isEmpty()) {
            return (number + ". " + text).trim();
        }
        return text.trim();
    }

    /**
     * Deletes the selected step, then renumbers the remaining steps (1..n),
     * then reloads the steps table.
     */
    @FXML
    public void removeStep() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        RecipeStep selectedStep = tablePreparation.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectStepToDelete"));
            return;
        }

        if (selectedRecipe.getId() == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.recipeNoId"));
            return;
        }
        if (selectedStep.getId() == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.stepNoId"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(resources.getString("recipeOverview.dialog.confirmDelete"));
        confirm.setHeaderText(resources.getString("recipeOverview.dialog.deleteStep"));
        confirm.setContentText(resources.getString("recipeOverview.dialog.deleteStepConfirm")
                .replace("{0}", String.valueOf(selectedStep.getPosition())));

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            // 1) Delete
            server.deleteRecipeStep(selectedRecipe.getId(), selectedStep.getId());

            // 2) Renumber on client, persist to server
            renumberAndPersistSteps(selectedRecipe.getId());

            // 3) Reload UI from server
            loadStepsForRecipe(selectedRecipe);

        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    //AI-generated code
    /**
     * Renumbers steps to be 1 through n with no gaps and persists changes to the backend.
     */
    private void renumberAndPersistSteps(Long recipeId) {
        List<RecipeStep> steps = server.getStepsForRecipe(recipeId);
        if (steps == null || steps.isEmpty()) {
            return;
        }

        steps.sort((a, b) -> {
            int pa = (a == null) ? Integer.MAX_VALUE : a.getPosition();
            int pb = (b == null) ? Integer.MAX_VALUE : b.getPosition();

            // push <=0 to the end
            if (pa <= 0) pa = Integer.MAX_VALUE;
            if (pb <= 0) pb = Integer.MAX_VALUE;

            // by id if available, otherwise keep as-is
            if (pa != pb) return Integer.compare(pa, pb);

            Long ida = (a == null) ? null : a.getId();
            Long idb = (b == null) ? null : b.getId();
            if (ida == null && idb == null) return 0;
            if (ida == null) return 1;
            if (idb == null) return -1;
            return ida.compareTo(idb);
        });

        int expected = 1;

        for (RecipeStep s : steps) {
            if (s == null) continue;
            if (s.getId() == null) continue; // cannot persist without id

            // If the server requires instruction for updates, ensure it's present.
            String instr = s.getInstruction();
            if (instr == null) instr = "";

            if (s.getPosition() != expected) {
                RecipeStep updated = new RecipeStep();
                updated.setId(s.getId());
                updated.setInstruction(instr);
                updated.setPosition(expected);

                // Persist
                server.updateRecipeStep(recipeId, updated);
            }

            expected++;
        }
    }

    @FXML
    private void editIngredientAmount() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        RecipeIngredient selectedIngredient =
                tableIngredients.getSelectionModel().getSelectedItem();

        if (selectedRecipe == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        if (selectedIngredient == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectIngredientToEdit"));
            return;
        }

        mainCtrl.showEditRecipeIngredient(selectedRecipe, selectedIngredient);
    }

    /**
     * Downloads the recipe as a file. This file is in markdown.
     * It will first open a file explorer so the user can set a name and location of the file.
     * When no recipe is selected, an error window will pop up.
     */
    @FXML
    private void downloadRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        double factor = 1.0;

        try {
            factor = Double.parseDouble(scaleFactorField.getText());
        } catch (NumberFormatException e) {
            factor = 1.0;
        }

        if (factor <= 0.0) {
            factor = 1.0;
        }

        String content = RecipeFormatter.format(selected, factor);

        FileChooser chooser = new FileChooser();
        chooser.setTitle(resources.getString("recipeOverview.dialog.saveRecipe"));

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(resources
                        .getString("recipeOverview.dialog.markdownFile"), "*.md")
        );

        chooser.setInitialFileName(
                selected.getTitle().replaceAll("\\s+", "_") + ".md"
        );

        File file = chooser.showSaveDialog(
                tableRecipes.getScene().getWindow()
        );

        if (file == null) {
            return; // user cancelled
        }

        try {
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            mainCtrl.showError(resources.getString("recipeOverview.error.failedToSave"));
        }
    }

    /**
     * Hardcoded list of languages shown in the selector.
     */
    private final List<LanguageOption> supportedLanguages = List.of(
            new LanguageOption("en", "English", "Icons/english-flag.png"),
            new LanguageOption("nl", "Nederlands", "Icons/dutch-flag.png"),
            new LanguageOption("es", "Español", "Icons/spanish-flag.png"),
            new LanguageOption("fr", "Français", "Icons/french-flag.png")
    );

    @SuppressWarnings("unused")
    private LanguageOption currentLanguage;

    /**
     * Simple value object describing a language choice.
     */
    private static class LanguageOption {
        @SuppressWarnings("unused")
        private final String code;
        private final String name;
        private final String iconPath;

        LanguageOption(String code, String name, String iconPath) {
            this.code = code;
            this.name = name;
            this.iconPath = iconPath;
        }
    }

    /**
     * Loads an image resource and returns a scaled {@link ImageView} for menu display.
     *
     * @param path   classpath to the image resource
     * @param height desired image height in pixels
     * @return image view or {@code null} if the resource cannot be found
     */
    private ImageView createFlagGraphic(String path, double height) {
        var stream = RecipeOverviewCtrl.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            return null;
        }
        ImageView view = new ImageView(new Image(stream));
        view.setPreserveRatio(true);
        view.setFitHeight(height);
        return view;
    }

    /**
     * Toggles the favorite status of the currently selected recipe.
     * Updates the button appearance and refreshes the table to show highlighting.
     */
    @FXML
    private void favouriteRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError(resources.getString("recipeOverview.error.selectRecipeFirst"));
            return;
        }

        boolean isFavorite = favoritesManager.toggleFavorite(selected.getId());
        selected.setFavorite(isFavorite);
        updateFavoriteButton(selected);

        // Refresh the table to update highlighting
        tableRecipes.refresh();
    }

    /**
     * Updates the favorite button appearance based on the recipe's favorite status.
     *
     * @param recipe the currently selected recipe, or null to reset the button
     */
    private void updateFavoriteButton(Recipe recipe) {
        if (recipe == null) {
            favButton.setText("☆");
            favButton.setStyle("");
            return;
        }

        if (favoritesManager.isFavorite(recipe.getId())) {
            favButton.setText("★");
            favButton.setStyle("-fx-text-fill: gold; -fx-font-size: 20px;");
        } else {
            favButton.setText("☆");
            favButton.setStyle("-fx-font-size: 20px;");
        }
    }

    /**
     * Sets the search field to the given query and executes the search automatically.
     * Clears any previously selected recipe.
     *AI generated javadoc
     * @param searchQuery the text to search for (typically an ingredient name)
     */
    public void setSearchQuery(String searchQuery) {
        tableRecipes.getSelectionModel().clearSelection();
        searchField.setText(searchQuery);
    }
}
package client.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import client.utils.RecipeFormatter;
import com.google.inject.Inject;

import client.ws.WebSocketService;
import client.utils.ServerUtils;
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
import org.springframework.messaging.simp.stomp.StompSession;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.stream.Collectors;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final WebSocketService webSocketService;

    private ObservableList<Recipe> data;
    private ObservableList<Recipe> allRecipes; // Unfiltered list of all recipes

    private StompSession.Subscription recipeContentSubscription;

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
    private CheckBox filterEnglish;
    @FXML
    private CheckBox filterDutch;
    @FXML
    private CheckBox filterSpanish;

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
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl,
                              WebSocketService webSocketService) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.webSocketService = webSocketService;
    }

    /**
     * Called by the JavaFX framework after the FXML elements have been injected.
     *
     * <p>Initialises UI bindings and listeners, then shows the default
     * “main menu” view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMainMenu();
        setupLanguageMenu();

        colRecipes.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));

        colIngredients.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getIngredient().getName())
        );

        colAmount.setCellValueFactory(cell -> {
            var ri = cell.getValue();

            boolean hasFormal = ri.getAmount() != null &&
                            ri.getAmount() != 0 &&
                            ri.getUnit() != null;

            String value = hasFormal
                    ? ri.getAmount() + " " + ri.getUnit()
                    : ri.getInformalAmount(); // ternary operator that executes the code
                                              // to the left of the : if the condition is true and
                                              // the code on the right otherwise

            return new SimpleStringProperty(value);
        });

        colPreparation.setCellValueFactory(cell ->
                new SimpleStringProperty(formatStepForDisplay(cell.getValue())));

        tableRecipes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        recipeName.setText(newSel.getTitle());
                        tableIngredients.setVisible(true);
                        tablePreparation.setVisible(true);

                        recipeEditButton.setVisible(true);
                        recipeName.setVisible(true);

                        recipeIngredientAdd.setVisible(true);
                        recipeIngredientDelete.setVisible(true);

                        if (newSel.getIngredients() != null) {
                            tableIngredients.setItems(
                                    FXCollections.observableArrayList(newSel.getIngredients()));
                        } else {
                            tableIngredients.getItems().clear();
                        }

                        loadRecipeOverviewUI();
                        loadStepsForRecipe(newSel);
                        subscribeToRecipeContent(newSel.getId());
                        reloadSelectedRecipeDetails(newSel);
                    }
                });

        recipeIngredientEditButton.setDisable(true);
        tableIngredients.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    recipeIngredientEditButton.setDisable(newSel == null);
                });

        setupWebSocketSubscriptions();
    }

    /**
     * Configures the language indicator/dropdown with the available options.
     * This is UI-only for now; selection changes are not yet persisted or applied.
     */
    private void setupLanguageMenu() {
        languageMenu.getItems().clear();

        for (LanguageOption option : supportedLanguages) {
            MenuItem item = new MenuItem(option.name);
            item.setGraphic(createFlagGraphic(option.iconPath, 16));
            item.setOnAction(e -> setCurrentLanguage(option));
            languageMenu.getItems().add(item);
        }

        if (!supportedLanguages.isEmpty()) {
            setCurrentLanguage(supportedLanguages.get(0));
        }
    }

    /**
     * Updates the indicator text and flag to the chosen language.
     *
     * @param option selected language option
     */
    private void setCurrentLanguage(LanguageOption option) {
        this.currentLanguage = option;
        languageMenu.setText(option.name);
        languageMenu.setGraphic(createFlagGraphic(option.iconPath, 16));
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
            mainCtrl.showError("Select recipe first!");
            return;
        }

        Recipe clone;
        if(original.getServings() != null){
            clone = new Recipe(original.getTitle() + " (copy)", original.getServings());
        }
        else{
            clone = new Recipe(original.getTitle() + " (copy)");
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
     * Selects the given recipe in the table.
     *
     * @param recipe the recipe to select
     */
    public void selectRecipe(Recipe recipe) {
        if (recipe == null) return;
        tableRecipes.getSelectionModel().select(recipe);
        loadStepsForRecipe(recipe);
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
     * Filters recipes based on selected language checkboxes.
     * If no languages are selected, all recipes are shown.
     */
    @FXML
    public void applyLanguageFilter() {
        if (allRecipes == null) {
            return; // Recipes not loaded yet
        }

        // Check if filter checkboxes are initialized
        if (filterEnglish == null || filterDutch == null || filterSpanish == null) {
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

        // If no languages are selected, show all recipes
        if (selectedLanguages.isEmpty()) {
            data = FXCollections.observableList(allRecipes);
        } else {
            // Filter recipes by selected languages
            List<Recipe> filtered = allRecipes.stream()
                    .filter(recipe -> {
                        String recipeLanguage = recipe.getLanguage();
                        // Include recipe if its language matches any selected language
                        // Also include recipes with no language set (null) when no filter is active
                        return recipeLanguage != null && selectedLanguages.contains(recipeLanguage);
                    })
                    .collect(Collectors.toList());
            data = FXCollections.observableList(filtered);
        }

        tableRecipes.setItems(data);
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
     * Handles the Edit/Save button click for a recipe name.
     *
     * <p>When not in edit mode, switches to an editable {@link TextField}.
     * When already editing, validates the input and updates the model.</p>
     */
    public void editNameClicked() {
        if (!editingName) {
            editingName = true;
            recipeEditButton.setText("Save");

            recipeEditBox.setText(recipeName.getText());
            recipeEditBox.setDisable(false);
            recipeEditBox.setVisible(true);

            recipeName.setVisible(false);
        } else {
            String newName = recipeEditBox.getText();

            if (newName == null || newName.trim().isEmpty()) {
                mainCtrl.showError("Name cannot be empty.");
                return;
            }
            recipeName.setText(newName);

            editingName = false;
            recipeEditButton.setText("Edit");

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
            mainCtrl.showError("No recipe selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Recipe?");
        confirm.setContentText("You sure you wanna delete this recipe?");

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
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        RecipeStep selectedStep = tablePreparation.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            mainCtrl.showError("Select a step to edit.");
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
            mainCtrl.showError("Select an ingredient to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Ingredient?");
        confirm.setContentText("Are you sure you want to remove this ingredient?");

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
        recipeName.setText("Welcome to FoodPal!");
        tableIngredients.setVisible(false);
        tablePreparation.setVisible(false);
        recipeIngredientAdd.setVisible(false);
        recipeIngredientDelete.setVisible(false);
        downloadRecipeButton.setVisible(false);
        editStepsButton.setVisible(false);
        removeStepButton.setVisible(false);
        addRecipeStep.setVisible(false);
        recipeIngredientEditButton.setVisible(false);
    }

    /**
     * Makes every component that the user should
     * be able to interact with when a recipe is selected visible
     */
    public void loadRecipeOverviewUI() {
        tableIngredients.setVisible(true);
        tablePreparation.setVisible(true);

        recipeEditButton.setVisible(true);
        recipeName.setVisible(true);

        recipeIngredientAdd.setVisible(true);
        recipeIngredientDelete.setVisible(true);

        downloadRecipeButton.setVisible(true);
        editStepsButton.setVisible(true);
        removeStepButton.setVisible(true);
        addRecipeStep.setVisible(true);
        recipeIngredientEditButton.setVisible(true);
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
        if (selected == null) { mainCtrl.showError("Select a recipe first."); return; }
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
            mainCtrl.showError("Select a recipe first.");
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
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        RecipeStep selectedStep = tablePreparation.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            mainCtrl.showError("Select a step to delete.");
            return;
        }

        if (selectedRecipe.getId() == null) {
            mainCtrl.showError("Selected recipe has no id.");
            return;
        }
        if (selectedStep.getId() == null) {
            mainCtrl.showError("Selected step has no id.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Step");
        confirm.setContentText("Are you sure you want to delete step " + selectedStep.getPosition() + "?");

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
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        if (selectedIngredient == null) {
            mainCtrl.showError("Select an ingredient to edit.");
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
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        String content = RecipeFormatter.format(selected);

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Recipe");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Markdown file (*.md)", "*.md")
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
            mainCtrl.showError("Failed to save recipe.");
        }
    }

    /**
     * Hardcoded list of languages shown in the selector.
     */
    private final List<LanguageOption> supportedLanguages = List.of(
            new LanguageOption("en", "English", "Icons/english-flag.png"),
            new LanguageOption("nl", "Nederlands", "Icons/dutch-flag.png"),
            new LanguageOption("es", "Español", "Icons/spanish-flag.png")
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
}
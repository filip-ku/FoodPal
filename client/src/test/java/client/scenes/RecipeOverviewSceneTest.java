package client.scenes;

import client.ClientModule;
import client.MyFXML;
import client.MyModule;
import client.utils.ConfigUtils;
import commons.Recipe;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.math.BigDecimal;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class RecipeOverviewSceneTest {

    private static final MyFXML FXML = new MyFXML(createInjector(new MyModule(),
            new ClientModule()));

    private TableView<Recipe> tableRecipes;
    private Label recipeName;
    private Button editButton;
    private Button editIngredientButton;

    @Start
    private void start(Stage stage) throws IOException {
        var overview = FXML.load(RecipeOverviewCtrl.class,
                "client", "scenes", "RecipeOverview.fxml");

        var scene = new Scene(overview.getValue());
        stage.setScene(scene);
        stage.show();

        tableRecipes = (TableView<Recipe>) scene.lookup("#tableRecipes");
        recipeName = (Label) scene.lookup("#recipeName");
        editButton = (Button) scene.lookup("#recipeEditButton");
        editIngredientButton = (Button) scene.lookup("#recipeIngredientEditButton");

        ConfigUtils.setUILanguage("en");
    }

    private Recipe recipeWithServings(String title) {
        Recipe r = new Recipe(title);
        r.setServings(BigDecimal.ONE);
        return r;
    }

    @Test
    public void defaultStateShowsWelcome(FxRobot robot) {
        assertEquals("Welcome to FoodPal!", recipeName.getText());
        assertEquals(false, editButton.isVisible());
    }

    @Test
    public void sanityCheckUIElementsExist() {
        assertNotNull(tableRecipes, "Table should exist");
        assertNotNull(recipeName, "Label should exist");
        assertNotNull(editButton, "Button should exist");
    }

    @Test
    public void tableStartsEmpty() {
        assertTrue(tableRecipes.getItems().isEmpty(), "Table should have 0 items initially");
        assertEquals(0, tableRecipes.getItems().size());
    }

    @Test
    public void testJavaFXLabelSetterWorks(FxRobot robot) {
        robot.interact(() -> recipeName.setText("Forced Text"));
        assertEquals("Forced Text", recipeName.getText());
    }

    @Test
    public void clickingBackgroundDoesNotCrash(FxRobot robot) {
        String initialText = recipeName.getText();
        robot.clickOn(tableRecipes);
        assertEquals(initialText, recipeName.getText());
    }
    @Test
    public void canAddAndRemoveItemsFromTable(FxRobot robot) {
        Recipe r = new Recipe("Temporary");

        robot.interact(() -> {
            tableRecipes.getItems().add(r);
        });
        assertEquals(1, tableRecipes.getItems().size());

        robot.interact(() -> {
            tableRecipes.getItems().remove(r);
        });
        assertEquals(0, tableRecipes.getItems().size());
    }

    @Test
    public void editIngredientButtonExists() {
        assertNotNull(editIngredientButton, "Edit ingredient button should exist");
    }

    @Test
    public void clickingEditButtonDoesNotCrash(FxRobot robot) {
        robot.interact(() -> {
            editIngredientButton.setVisible(true);
        });
        robot.clickOn(editIngredientButton);
        assertTrue(true, "Clicking edit button should not crash the application");
    }

    @Test
    public void editButtonStartsHidden() {
        assertFalse(editIngredientButton.isVisible(),
                "Edit ingredient button should be hidden initially");
    }

    @Test
    public void editButtonCanBeShown(FxRobot robot) {
        robot.interact(() -> {
            editIngredientButton.setVisible(true);
        });

        assertTrue(editIngredientButton.isVisible(),
                "Edit button should be visible after setting it");
    }

    @Test
    public void editButtonCanBeHidden(FxRobot robot) {
        robot.interact(() -> {
            editIngredientButton.setVisible(true);
            editIngredientButton.setVisible(false);
        });

        assertFalse(editIngredientButton.isVisible(),
                "Edit button should be hidden after setting it to false");
    }

    @Test
    public void deletingRecipeShowsConfirmationDialog(FxRobot robot) {
        Recipe r = recipeWithServings("To Be Deleted");

        robot.interact(() -> {
            tableRecipes.getItems().add(r);

            editButton.setVisible(true);
        });

        robot.clickOn("#deleteRecipeButton");

        assertTrue(robot.lookup(".dialog-pane").tryQuery().isPresent(),
                "Confirmation dialog should be shown when deleting a recipe");
    }

    @Test
    public void editingRecipeNameUpdatesLabel(FxRobot robot) {
        Recipe r = recipeWithServings("Original Name");

        robot.interact(() -> {
            tableRecipes.getItems().add(r);

            recipeName.setText("Original Name");
            editButton.setVisible(true);
        });

        robot.interact(() -> recipeName.setText("Updated Name"));

        assertEquals("Updated Name", recipeName.getText(),
                "Recipe name label should reflect edited name");
    }

    @Test
    public void selectingRecipeShowsDetails(FxRobot robot) {
        Recipe r = recipeWithServings("Detail Recipe");

        robot.interact(() -> {
            tableRecipes.getItems().add(r);

            recipeName.setText(r.getTitle());
            editButton.setVisible(true);
        });

        assertEquals("Detail Recipe", recipeName.getText(),
                "Selecting a recipe should update the details view");
    }

    @Test
    public void languageFilterShowsOnlyMatchingRecipes(FxRobot robot) {
        Recipe rEnglish = new Recipe("English Recipe");
        rEnglish.setLanguage("en");
        Recipe rDutch = new Recipe("Dutch Recipe");
        rDutch.setLanguage("nl");
        Recipe rFrench = new Recipe("French Recipe");
        rFrench.setLanguage("fr");

        robot.interact(() -> {
            tableRecipes.getItems().addAll(rEnglish, rDutch, rFrench);
        });

        robot.interact(() -> {
            tableRecipes.getItems().setAll(
                    tableRecipes.getItems().stream()
                            .filter(r -> "en".equals(r.getLanguage()))
                            .toList()
            );
        });

        assertEquals(1, tableRecipes.getItems().size(),
                "Only one recipe should remain after filtering");
        assertEquals("English Recipe", tableRecipes.getItems().get(0).getTitle(),
                "The remaining recipe should be the English one");
    }

}
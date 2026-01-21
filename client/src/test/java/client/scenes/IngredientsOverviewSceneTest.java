package client.scenes;

import client.ClientModule;
import client.MyFXML;
import client.MyModule;
import commons.Ingredient;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IngredientsOverviewSceneTest {

    private static final MyFXML FXML = new MyFXML(createInjector(new MyModule(),
            new ClientModule()));

    private TableView<Ingredient> tableIngredients;
    private Button seeRecipesButton;
    private Button editIngredientButton;

    @Start
    private void start(Stage stage) throws IOException {
        var overview = FXML.load(IngredientsOverviewCtrl.class,
                "client", "scenes", "IngredientOverview.fxml");

        var scene = new Scene(overview.getValue());
        stage.setScene(scene);
        stage.show();

        tableIngredients = (TableView<Ingredient>) scene.lookup("#tableIngredients");
        seeRecipesButton = (Button) scene.lookup("#seeRecipesButton");
        editIngredientButton = (Button) scene.lookup("#editIngredientButton");
    }

    @Test
    public void sanityCheckUIElementsExist() {
        assertNotNull(tableIngredients, "Table should exist");
        assertNotNull(seeRecipesButton, "See Recipes button should exist");
        assertNotNull(editIngredientButton, "Edit button should exist");
    }

    @Test
    public void tableStartsEmpty() {
        assertTrue(tableIngredients.getItems().isEmpty(),
                "Table should have 0 items initially");
        assertEquals(0, tableIngredients.getItems().size());
    }

    @Test
    public void seeRecipesButtonStartsDisabled() {
        assertTrue(seeRecipesButton.isDisabled(),
                "See Recipes button should be disabled initially");
    }

    @Test
    public void editIngredientButtonStartsDisabled() {
        assertTrue(editIngredientButton.isDisabled(),
                "Edit ingredient button should be disabled initially");
    }

    private Ingredient createIngredient(String name, Long id) {
        Ingredient ingredient = new Ingredient(name);
        ingredient.setId(id);
        ingredient.setProteinPer100g(0.0);
        ingredient.setFatPer100g(0.0);
        ingredient.setCarbsPer100g(0.0);
        return ingredient;
    }

    @Test
    public void seeRecipesButtonEnablesWhenIngredientSelected(FxRobot robot) {
        Ingredient ingredient = createIngredient("Tomato", 1L);

        robot.interact(() -> {
            tableIngredients.getItems().add(ingredient);
            tableIngredients.getSelectionModel().select(ingredient);
        });

        assertFalse(seeRecipesButton.isDisabled(),
                "See Recipes button should be enabled when ingredient is selected");
    }

    @Test
    public void seeRecipesButtonDisablesWhenSelectionCleared(FxRobot robot) {
        Ingredient ingredient = createIngredient("Tomato", 1L);

        robot.interact(() -> {
            tableIngredients.getItems().add(ingredient);
            tableIngredients.getSelectionModel().select(ingredient);
        });

        assertFalse(seeRecipesButton.isDisabled(),
                "Button should be enabled after selection");

        robot.interact(() -> {
            tableIngredients.getSelectionModel().clearSelection();
        });

        assertTrue(seeRecipesButton.isDisabled(),
                "Button should be disabled after clearing selection");
    }

    @Test
    public void bothButtonsEnableWhenIngredientSelected(FxRobot robot) {
        Ingredient ingredient = createIngredient("Onion", 2L);

        robot.interact(() -> {
            tableIngredients.getItems().add(ingredient);
            tableIngredients.getSelectionModel().select(ingredient);
        });

        assertFalse(seeRecipesButton.isDisabled(),
                "See Recipes button should be enabled");
        assertFalse(editIngredientButton.isDisabled(),
                "Edit button should be enabled");
    }

    @Test
    public void canAddAndRemoveIngredientsFromTable(FxRobot robot) {
        Ingredient ingredient = createIngredient("Temporary", 3L);

        robot.interact(() -> {
            tableIngredients.getItems().add(ingredient);
        });
        assertEquals(1, tableIngredients.getItems().size());

        robot.interact(() -> {
            tableIngredients.getItems().remove(ingredient);
        });
        assertEquals(0, tableIngredients.getItems().size());
    }

    @Test
    public void selectingDifferentIngredientsKeepsButtonEnabled(FxRobot robot) {
        Ingredient ingredient1 = createIngredient("Carrot", 4L);
        Ingredient ingredient2 = createIngredient("Potato", 5L);

        robot.interact(() -> {
            tableIngredients.getItems().addAll(ingredient1, ingredient2);
            tableIngredients.getSelectionModel().select(ingredient1);
        });

        assertFalse(seeRecipesButton.isDisabled(),
                "Button should be enabled for first ingredient");

        robot.interact(() -> {
            tableIngredients.getSelectionModel().select(ingredient2);
        });

        assertFalse(seeRecipesButton.isDisabled(),
                "Button should still be enabled for second ingredient");
    }

    @Test
    public void multipleIngredientsCanBeAddedToTable(FxRobot robot) {
        Ingredient ing1 = createIngredient("Salt", 7L);
        Ingredient ing2 = createIngredient("Pepper", 8L);
        Ingredient ing3 = createIngredient("Sugar", 9L);

        robot.interact(() -> {
            tableIngredients.getItems().addAll(ing1, ing2, ing3);
        });

        assertEquals(3, tableIngredients.getItems().size(),
                "Table should contain all three ingredients");
    }

    @Test
    public void ingredientNameIsPreserved(FxRobot robot) {
        String expectedName = "Basil";
        Ingredient ingredient = createIngredient(expectedName, 10L);

        robot.interact(() -> {
            tableIngredients.getItems().add(ingredient);
        });

        assertEquals(expectedName, tableIngredients.getItems().get(0).getName(),
                "Ingredient name should be preserved in table");
    }
}
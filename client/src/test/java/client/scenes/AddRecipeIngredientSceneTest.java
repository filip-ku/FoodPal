package client.scenes;

import client.MyFXML;
import client.MyModule;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(ApplicationExtension.class)
public class AddRecipeIngredientSceneTest {

    private static final MyFXML FXML = new MyFXML(createInjector(new MyModule()));

    private TextField ingredientNameInput;
    private TextField quantityInput;
    private TextField unitsInput;
    private TextField notesInput;

    private AddRecipeIngredientCtrl ctrl;

    @Start
    private void start(Stage stage){
        var pair = FXML.load(
                AddRecipeIngredientCtrl.class,
                "client", "scenes", "AddRecipeIngredient.fxml"
        );

        ctrl = pair.getKey();
        var root = pair.getValue();

        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        ingredientNameInput = (TextField) scene.lookup("#ingredientNameInput");
        quantityInput = (TextField) scene.lookup("#quantityInput");
        unitsInput = (TextField) scene.lookup("#unitsInput");
        notesInput = (TextField) scene.lookup("#notesInput");
    }

    @Test
    public void defaultFieldsAreEmpty(FxRobot robot) {
        assertEquals("", quantityInput.getText());
        assertEquals("", unitsInput.getText());
        assertEquals("", notesInput.getText());
    }

    @Test
    public void editContextPrefillsFields(FxRobot robot) {
        Recipe recipe = new Recipe("Pasta");
        Ingredient ing = new Ingredient("Tomato");

        RecipeIngredient ri = new RecipeIngredient();
        ri.setAmount(3.0);
        ri.setUnit("pcs");
        ri.setInformalAmount("chopped");

        robot.interact(() -> ctrl.setContextForEdit(recipe, ri, ing));

        assertEquals("Tomato", ingredientNameInput.getText());
        assertEquals("3.0", quantityInput.getText());
        assertEquals("pcs", unitsInput.getText());
        assertEquals("chopped", notesInput.getText());
    }

    @Test
    public void addContextClearsFields(FxRobot robot) {
        Recipe recipe = new Recipe("Cake");
        Ingredient ing = new Ingredient("Sugar");

        robot.interact(() -> ctrl.setContextForAdd(recipe, ing));

        assertEquals("Sugar", ingredientNameInput.getText());
        assertEquals("", quantityInput.getText());
        assertEquals("", unitsInput.getText());
        assertEquals("", notesInput.getText());
    }

    @Test
    public void clearFieldsResetsFields(FxRobot robot) {
        robot.interact(() -> {
            ingredientNameInput.setText("Test");
            quantityInput.setText("5");
            unitsInput.setText("kg");
            notesInput.setText("fine");
        });

        robot.interact(() -> ctrl.clearFields());

        robot.interact(() -> {
            assertEquals("", ingredientNameInput.getText());
            assertEquals("", quantityInput.getText());
            assertEquals("", unitsInput.getText());
            assertEquals("", notesInput.getText());
        });
    }


    @Test
    public void negativeQuantityShowsErrorAndStops(FxRobot robot) {
        robot.interact(() -> {
            quantityInput.setText("-4");
            unitsInput.setText("kg");
        });

        robot.interact(() -> ctrl.ok());

        assertEquals("-4", quantityInput.getText());
    }

    @Test
    public void UnknownCharacterForQuantity(FxRobot robot) {
        robot.interact(() -> {
            quantityInput.setText("/5");
            unitsInput.setText("kg");
        });

        robot.interact(() -> ctrl.ok());

        assertEquals("/5", quantityInput.getText());
    }

    @Test
    public void emptyUnitAndNotesAreAccepted(FxRobot robot) {
        Recipe recipe = new Recipe("Salad");
        Ingredient ing = new Ingredient("Lettuce");
        
        robot.interact(() -> ctrl.setContextForAdd(recipe, ing));

        robot.interact(() -> {
            quantityInput.setText("2");
            unitsInput.setText("  ");
            notesInput.setText("");
            ctrl.ok();
        });

        var ingredients = recipe.getIngredients();
        var stream = ingredients.stream();
        var filtered = stream.filter(ri -> ri.getIngredient().equals(ing));
        var optional = filtered.findFirst();
        RecipeIngredient added = optional.orElseThrow(() -> new AssertionError("Ingredient not added"));

        assertEquals(ing, added.getIngredient());
        assertEquals(2.0, added.getAmount());
        assertNull(added.getUnit());
        assertNull(added.getInformalAmount());
    }
}
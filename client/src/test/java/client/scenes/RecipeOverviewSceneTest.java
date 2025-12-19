package client.scenes;

import client.MyFXML;
import client.MyModule;
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

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.*; // Added assertions

@ExtendWith(ApplicationExtension.class)
public class RecipeOverviewSceneTest {

    private static final MyFXML FXML = new MyFXML(createInjector(new MyModule()));

    private TableView<Recipe> tableRecipes;
    private Label recipeName;
    private Button editButton;

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
    }

    @Test
    public void defaultStateShowsWelcome(FxRobot robot) {
        assertEquals("Welcome to FoodPal!", recipeName.getText());
        assertEquals(false, editButton.isVisible());
    }

    @Test
    public void selectingRecipeShowsDetails(FxRobot robot) {
        robot.interact(() -> {
            tableRecipes.getItems().add(new Recipe("Test Recipe"));
            tableRecipes.getSelectionModel().select(0);
        });

        assertEquals("Test Recipe", recipeName.getText());
        assertEquals(true, editButton.isVisible());
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
}
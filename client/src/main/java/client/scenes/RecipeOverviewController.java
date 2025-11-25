package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RecipeOverviewController implements Initializable {

    private final ServerUtils server;
    private final MainController mainController;

    private ObservableList<Recipe> data;

    @FXML
    private TableView<Recipe> table;
    @FXML
    private TableColumn<Recipe, String> collumnTitle;

    @Inject
    public RecipeOverviewController(ServerUtils server, MainController mainController) {
        this.server = server;
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        collumnTitle.setCellValueFactory(recipe -> new SimpleStringProperty(recipe.getValue().getTitle()));
    }

    public void addRecipe() {
        mainController.showAddRecipe();
    }

    public void refresh() {
        List<Recipe> recipes = server.getRecipes();
        data = FXCollections.observableList(recipes);
        table.setItems(data);
    }
}

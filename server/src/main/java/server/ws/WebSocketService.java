package server.ws;

import commons.ws.RecipeChangedEvent;
import commons.ws.RecipeContentChangedEvent;
import commons.ws.RecipeListEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing WebSocket events to connected clients.
 */
@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructs a new WebSocketService.
     *
     * @param messagingTemplate Spring's messaging template for sending WebSocket messages
     */
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Publishes a recipe list change event (addition/deletion).
     *
     * @param recipeId the ID of the recipe that was added or deleted
     */
    public void publishRecipeListChange(Long recipeId) {
        messagingTemplate.convertAndSend("/topic/recipe-list", new RecipeListEvent(recipeId));
    }

    /**
     * Publishes a recipe changed event (title change, creation, deletion).
     *
     * @param type the type of change: "Create", "Deleted", or "Changed"
     * @param recipeId the ID of the recipe
     * @param title the title of the recipe (nullable for deletions)
     */
    public void publishRecipeChanged(String type, Long recipeId, String title) {
        messagingTemplate.convertAndSend("/topic/recipe-changed",
                new RecipeChangedEvent(type, recipeId, title));
    }

    /**
     * Publishes a recipe content change event (ingredients/steps modified).
     *
     * @param recipeId the ID of the recipe whose content changed
     */
    public void publishRecipeContentChanged(Long recipeId) {
        messagingTemplate.convertAndSend("/topic/recipe-content/" + recipeId,
                new RecipeContentChangedEvent(recipeId));
    }
}


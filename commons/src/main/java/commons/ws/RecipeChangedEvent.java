package commons.ws;

public record RecipeChangedEvent(
        String type,     // Create, Deleted, Changed
        Long recipeId,
        String title     // nullable
) { }

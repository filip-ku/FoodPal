package client.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages local storage of favorite recipes.
 * Favorites are stored in the user's home directory and persist across sessions.
 * This is a singleton managed by Guice.
 */
public class FavoritesManager {
    private static final String FAVORITES_FILE = ".foodpal_favorites";
    private final Path favoritesPath;
    private Set<Long> favoriteIds;

    /**
     * Creates a new FavoritesManager that stores favorites in the user's directory.
     */
    public FavoritesManager() {
        String userHome = System.getProperty("user.home");
        this.favoritesPath = Paths.get(userHome, FAVORITES_FILE);
        this.favoriteIds = new HashSet<>();
        loadFavorites();
    }

    /**
     * Loads favorite recipe IDs from disk.
     * AI generated
     */
    private void loadFavorites() {
        if (!Files.exists(favoritesPath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(favoritesPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    favoriteIds.add(Long.parseLong(line.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid lines
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load favorites: " + e.getMessage());
        }
    }

    /**
     * Saves favorite recipe IDs to disk.
     * AI generated
     */
    private void saveFavorites() {
        try (BufferedWriter writer = Files.newBufferedWriter(favoritesPath)) {
            for (Long id : favoriteIds) {
                writer.write(id.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save favorites: " + e.getMessage());
        }
    }

    /**
     * Adds a recipe to favorites.
     *
     * @param recipeId the ID of the recipe to favorite
     */
    public void addFavorite(Long recipeId) {
        if (recipeId != null && favoriteIds.add(recipeId)) {
            saveFavorites();
        }
    }

    /**
     * Removes a recipe from favorites.
     * @param recipeId the ID of the recipe to unfavorite
     */
    public void removeFavorite(Long recipeId) {
        if (recipeId != null && favoriteIds.remove(recipeId)) {
            saveFavorites();
        }
    }

    /**
     * Checks if a recipe is favorited.
     *AI generated
     * @param recipeId the ID of the recipe to check
     * @return true if the recipe is favorited, false otherwise
     */
    public boolean isFavorite(Long recipeId) {
        return recipeId != null && favoriteIds.contains(recipeId);
    }

    /**
     * Gets all favorite recipe IDs.
     *
     * @return an unmodifiable set of favorite recipe IDs
     */
    public Set<Long> getFavoriteIds() {
        return Collections.unmodifiableSet(favoriteIds);
    }

    /**
     * Toggles the favorite status of a recipe.
     *AI generated
     * @param recipeId the ID of the recipe to toggle
     * @return true if the recipe is now favorited, false if unfavorited
     */
    public boolean toggleFavorite(Long recipeId) {
        if (isFavorite(recipeId)) {
            removeFavorite(recipeId);
            return false;
        } else {
            addFavorite(recipeId);
            return true;
        }
    }

    /**
     * Checks if any of the given recipe IDs are favorites and removes them.
     * Used to clean up favorites when recipes are deleted.
     *
     * @param recipeIds the IDs to check and potentially remove
     * @return the set of IDs that were removed (were favorites and now deleted)
     */
    public Set<Long> cleanupDeletedRecipes(Set<Long> recipeIds) {
        Set<Long> removed = new HashSet<>();
        for (Long id : recipeIds) {
            if (isFavorite(id)) {
                removeFavorite(id);
                removed.add(id);
            }
        }
        return removed;
    }
}

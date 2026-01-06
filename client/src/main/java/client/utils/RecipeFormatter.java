package client.utils;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;

public final class RecipeFormatter {

    private RecipeFormatter() {}

    /**
     * This method converts a recipe into a string with markdown annotation containing
     * information such as ingredients and recipe steps of the provided recipe.
     * @param recipe to be converted
     * @return string which contains prettified information about the recipe
     */
    public static String format(Recipe recipe) {
        StringBuilder sb = new StringBuilder();

        sb.append("# ")
                .append(recipe.getTitle())
                .append("\n");

        if (recipe.getServings() != null) {
            sb.append("Servings: ").append(recipe.getServings()).append("\n\n");
        }

        sb.append("## Ingredients:\n");
        for (RecipeIngredient ri : recipe.getIngredients()) {
            sb.append("- ")
                    .append(ri.getIngredient().getName())
                    .append(" - ")
                    .append(ri.getAmount())
                    .append(" ")
                    .append(ri.getUnit())
                    .append("\n");
        }

        sb.append("\n## Preparation:\n");
        int stepNr = 1;
        for (RecipeStep step : recipe.getSteps()) {
            sb.append(stepNr++)
                    .append(". ")
                    .append(step.getInstruction())
                    .append("\n");
        }

        return sb.toString();
    }
}

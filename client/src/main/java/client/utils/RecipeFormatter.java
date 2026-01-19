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
     * @param factor factor to be used for the recipe scaling
     * @return string which contains prettified information about the recipe
     */
    public static String format(Recipe recipe, double factor) {
        StringBuilder sb = new StringBuilder();

        sb.append("# ")
                .append(recipe.getTitle())
                .append("\n");

        if (recipe.getServings() != null) {
            sb.append("Servings: ")
                    .append(recipe.getServings().doubleValue() * factor)
                    .append("\n\n");
        }

        sb.append("## Ingredients:\n");
        for (RecipeIngredient ri : recipe.getIngredients()) {
            sb.append("- ")
                    .append(ri.getIngredient().getName())
                    .append(" - ")
                    .append(formatRecipeIngredient(ri, factor))
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

    /**
     * Converts a RecipeIngredient into a string with the appropriate unit.
     * Also uses the scaling factor to scale the amount.
     *
     * @param ri RecipeIngredient to be converted
     * @param factor scaling factor
     * @return a String representation of the RecipeIngredient
     */
    public static String formatRecipeIngredient(RecipeIngredient ri, double factor) {
        boolean hasFormal = ri.getAmount() != null &&
                ri.getAmount() != 0 &&
                ri.getUnit() != null;

        if (hasFormal) {
            double scaledAmount = factor * ri.getAmount();
            String displayUnit = ri.getUnit();

            if (scaledAmount >= 3) {
                switch (displayUnit) {
                    case "g" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "kg";
                            if (scaledAmount >= 1000) {
                                scaledAmount /= 1000;
                                displayUnit = "ton";
                            }
                        }
                    }
                    case "kg" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "ton";
                        }
                    }
                    case "mL" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "L";
                            if (scaledAmount >= 1000) {
                                scaledAmount /= 1000;
                                displayUnit = "kL";
                            }
                        }
                    }
                    case "L" -> {
                        if (scaledAmount >= 1000) {
                            scaledAmount /= 1000;
                            displayUnit = "kL";
                        }
                    }
                    case "tsp" -> {
                        scaledAmount /= 3;
                        displayUnit = "tbsp";
                        if (scaledAmount >= 16) {
                            scaledAmount /= 16;
                            displayUnit = "cup";
                        }
                    }
                    case "tbsp" -> {
                        if (scaledAmount >= 16) {
                            scaledAmount /= 16;
                            displayUnit = "cup";
                        }
                    }
                    default -> {
                        break;
                    }
                }
            }
            return scaledAmount + " " + displayUnit;
        } else {
            String informalAmount = ri.getInformalAmount();

            if (factor > 1.0) {
                return informalAmount + " (x" + factor + ")";
            }
            return informalAmount;
        }
    }
}

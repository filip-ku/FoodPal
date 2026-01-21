# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Get the template project running from the command line first to ensure you have the required tools on your sytem.

Once it is working, you can try importing the project into your favorite IDE. Especially the client is a bit more tricky to set up there due to the dependency on a JavaFX SDK.
To help you get started, you can find additional instructions in the corresponding README of the client project.

# Searching for Recipes

Users can now navigate from the Ingredients Overview directly to recipes that use a specific ingredient. When viewing the list of all ingredients, selecting an ingredient and clicking the "See Recipes" button will:
- Automatically navigate to the Recipe Overview screen
- Pre-fill the search field with the selected ingredient's name
- Execute the search automatically to show only recipes containing that ingredient
- Display a clean view (no recipe selected) similar to when first opening the Recipe Overview

This feature makes it easy to discover which recipes use a particular ingredient without manually searching for it. The search uses the existing search functionality, which checks recipe titles, ingredients, and preparation steps with AND semantics.

The recipe search supports ingredient exclusion using a - prefix (e.g. -nuts). This is especially useful for allergies and dietary preferences, allowing users to quickly find safe recipes by including and excluding ingredients in a single search. To use this feature, simply type the ingredient to exclude with a minus sign in the search bar.

# Nutritional Values

The user is able to scale informal amounts of ingredients just like formal amounts. For example, scaling "a pinch" by 2 should show up as "a pinch (x2)".

The program also supports the use of tablespoon as well as cup.

The program converts teaspoon to tablespoon and tablespoon to cup automatically when scaling, similar to how 1000g gets converted to 1kg. (3 teaspoons = 1 tablespoon and 16 tablespoons = 1 cup)

The value inputted into scale is also taken in mind when downloading the recipe.

# Live Language Switch 

The meaningful addition is the addition of a fourth language(French).

The only thing that wasn't translated are the tables containing recipes, ingredients, and recipe steps when they are empty. This is because Scene Builder automatically assigns the text in english without the ability to change it to another language. 


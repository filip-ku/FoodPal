package server.Controller;

import commons.Ingredient;
import commons.Recipe;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.Repository.IngredientRepository;
import server.Repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * An in‑memory implementation of {@link IngredientRepository} that is used only
 * for unit tests. It keeps all {@link Ingredient} objects in a {@code List}
 * and records the names of methods that were called so that test cases can
 * assert on repository usage.
 *
 * <p> Because this class implements the full Spring Data JPA interface,
 * many methods are left with stubbed behaviour.
 * They exist only to satisfy the compiler </p>>
 */
public class TestIngredientRepository implements IngredientRepository {

    public final List<Ingredient> ingredients = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private final AtomicLong idSequence = new AtomicLong(1); // Will be used set the recipe ids.

    @Override
    public void flush() {
        calledMethods.add("flush");
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> S saveAndFlush(S entity) {
        calledMethods.add("saveAndFlush");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> List<S> saveAllAndFlush(Iterable<S> entities) {
        calledMethods.add("saveAllAndFlush");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch(Iterable<Ingredient> entities) {
        calledMethods.add("deleteAllInBatch");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        calledMethods.add("deleteAllByIdInBatch");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch() {
        calledMethods.add("deleteAllInBatch");
        // TODO Auto-generated method stub
    }

    @Override
    public Ingredient getOne(Long aLong) {
        calledMethods.add("getOne");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public Ingredient getById(Long aLong) {
        calledMethods.add("getById");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public Ingredient getReferenceById(Long aLong) {
        calledMethods.add("getReferenceById");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> Optional<S> findOne(Example<S> example) {
        calledMethods.add("findOne");
        return Optional.empty();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> List<S> findAll(Example<S> example) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> List<S> findAll(Example<S> example, Sort sort) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> Page<S> findAll(Example<S> example, Pageable pageable) {
        calledMethods.add("findAll");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> long count(Example<S> example) {
        calledMethods.add("count");
        return 0;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient> boolean exists(Example<S> example) {
        calledMethods.add("exists");
        return false;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Ingredient, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        calledMethods.add("findBy");
        return null;
        // TODO Auto-generated method stub
    }

    /**
     * Saves a {@link Ingredient} instance.
     *
     * <p>If the ingredient doesn't have a valid id, it will be assigned
     * a new unique id by {@code idSequence}. <br>
     * If the ingredient already has a valid id, it will overwrite the old ingredient
     * with the new ingredient.</p>
     *
     * @param entity the ingredient to be saved
     * @return the saved ingredient (with its id set)
     * @param <S> a subset of ingredient
     */
    @Override
    public <S extends Ingredient> S save(S entity) {
        calledMethods.add("save");
        if (entity.getId() == null || entity.getId() <=0 ) {
            entity.setId(idSequence.getAndIncrement());
        } else {
            ingredients.removeIf(ingredient -> ingredient.getId().equals(entity.getId()));
        }

        ingredients.add(entity);
        return entity;
    }

    @Override
    public <S extends Ingredient> List<S> saveAll(Iterable<S> entities) {
        calledMethods.add("saveAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    /**
     * Retrieve a {@link Ingredient} by its id.
     *
     * @param id of the ingredient to fetch
     * @return an optional ingredient, if found.
     */
    @Override
    public Optional<Ingredient> findById(Long id) {
        calledMethods.add("findById");
        return ingredients.stream().filter(ingredient -> ingredient.getId().equals(id)).findFirst();
    }

    /**
     * Check if an ingredient with a given id exists in the repository.
     *
     * @param id of a ingredient to be searched for
     * @return boolean reflecting if there is a ingredient with the provided id
     */
    @Override
    public boolean existsById(Long id) {
        calledMethods.add("existsById");
        return ingredients.stream().anyMatch(ingredient -> ingredient.getId().equals(id));
    }

    /**
     * return all saved {@link Ingredient} saved in the repository.
     *
     * @return all saved ingredients
     */
    @Override
    public List<Ingredient> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(ingredients);
    }

    @Override
    public List<Ingredient> findAllById(Iterable<Long> longs) {
        calledMethods.add("findAllById");
        return List.of();
        // TODO Auto-generated method stub
    }

    /**
     * Returns the number of ingredients currently stored in the repository.
     *
     * @return the total count of ingredients
     */
    @Override
    public long count() {
        calledMethods.add("count");
        return ingredients.size();
    }

    /**
     * Deletes a {@link Ingredient} by its id, if the ingredient got found.
     * @param id of the ingredient that needs to be deleted
     */
    @Override
    public void deleteById(Long id) {
        calledMethods.add("deleteById");
        ingredients.removeIf(ingredient -> ingredient.getId().equals(id));
    }

    @Override
    public void delete(Ingredient entity) {
        calledMethods.add("delete");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        calledMethods.add("deleteAllById");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends Ingredient> entities) {
        calledMethods.add("deleteAll");
        // TODO Auto-generated method stub
    }

    /**
     * Deletes all ingredients from the repository.
     */
    @Override
    public void deleteAll() {
        calledMethods.add("deleteAll");
        ingredients.clear();
    }

    @Override
    public List<Ingredient> findAll(Sort sort) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public Page<Ingredient> findAll(Pageable pageable) {
        calledMethods.add("findAll");
        return null;
        // TODO Auto-generated method stub
    }
}

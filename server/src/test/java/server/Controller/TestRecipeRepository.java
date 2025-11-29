package server.Controller;

import commons.Recipe;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.Repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * An in‑memory implementation of {@link RecipeRepository} that is used only
 * for unit tests. It keeps all {@link Recipe} objects in a {@code List}
 * and records the names of methods that were called so that test cases can
 * assert on repository usage.
 *
 * <p> Because this class implements the full Spring Data JPA interface,
 * many methods are left with stubbed behaviour.
 * They exist only to satisfy the compiler </p>>
 */
public class TestRecipeRepository implements RecipeRepository {

    public final List<Recipe> recipes = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private final AtomicLong idSequence = new AtomicLong(1); // Will be used set the recipe ids.

    @Override
    public void flush() {
        calledMethods.add("flush");
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> S saveAndFlush(S entity) {
        calledMethods.add("saveAndFlush");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> List<S> saveAllAndFlush(Iterable<S> entities) {
        calledMethods.add("saveAllAndFlush");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch(Iterable<Recipe> entities) {
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
    public Recipe getOne(Long aLong) {
        calledMethods.add("getOne");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public Recipe getById(Long aLong) {
        calledMethods.add("getById");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public Recipe getReferenceById(Long aLong) {
        calledMethods.add("getReferenceById");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> Optional<S> findOne(Example<S> example) {
        calledMethods.add("findOne");
        return Optional.empty();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> List<S> findAll(Example<S> example) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> List<S> findAll(Example<S> example, Sort sort) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> Page<S> findAll(Example<S> example, Pageable pageable) {
        calledMethods.add("findAll");
        return null;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> long count(Example<S> example) {
        calledMethods.add("count");
        return 0;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe> boolean exists(Example<S> example) {
        calledMethods.add("exists");
        return false;
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Recipe, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        calledMethods.add("findBy");
        return null;
        // TODO Auto-generated method stub
    }

    /**
     * Saves a {@link Recipe} instance.
     *
     * <p>If the recipe doesn't have a valid id, it will be assigned
     * a new unique id by {@code idSequence}. <br>
     * If the recipe already has a valid id, it will overwrite the old recipe
     * with the new recipe.</p>
     *
     * @param entity the {@link Recipe} to be saved
     * @return the saved {@link Recipe} (with its id set)
     * @param <S> a subset of {@link Recipe}
     */
    @Override
    public <S extends Recipe> S save(S entity) {
        calledMethods.add("save");
        if (entity.getId() == null || entity.getId() <= 0) {
            entity.setId(idSequence.getAndIncrement());
        } else {
            recipes.removeIf(recipe -> recipe.getId().equals(entity.getId()));
        }

        recipes.add(entity);
        return entity;
    }

    @Override
    public <S extends Recipe> List<S> saveAll(Iterable<S> entities) {
        calledMethods.add("saveAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    /**
     * Retrieve a {@link Recipe} by its id.
     *
     * @param id of the Recipe to fetch
     * @return an optional recipe, if found.
     */
    @Override
    public Optional<Recipe> findById(Long id) {
        calledMethods.add("findById");
        return recipes.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    /**
     * Check if a recipe with a given id exists in the repository.
     *
     * @param id of a recipe to be searched for
     * @return boolean reflecting if there is a recipe with the provided id
     */
    @Override
    public boolean existsById(Long id) {
        calledMethods.add("existsById");
        return recipes.stream().anyMatch(recipe -> recipe.getId().equals(id));
    }

    /**
     * return all saved recipes.
     * @return all saved recipes
     */
    @Override
    public List<Recipe> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(recipes);
    }

    @Override
    public List<Recipe> findAllById(Iterable<Long> id) {
        calledMethods.add("findAllById");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public long count() {
        calledMethods.add("count");
        return 0;
        // TODO Auto-generated method stub
    }

    /**
     * Deletes a {@link Recipe}  by it id, if the recipe got found.
     * @param id of the recipe that needs to be deleted
     */
    @Override
    public void deleteById(Long id) {
        calledMethods.add("deleteById");
        recipes.removeIf(recipe -> recipe.getId().equals(id));
    }

    @Override
    public void delete(Recipe recipe) {
        calledMethods.add("delete");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> id) {
        calledMethods.add("deleteAllById");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends Recipe> entities) {
        calledMethods.add("deleteAll");
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        calledMethods.add("deleteAll");
        // TODO Auto-generated method stub
    }

    @Override
    public List<Recipe> findAll(Sort sort) {
        calledMethods.add("findAll");
        return List.of();
        // TODO Auto-generated method stub
    }

    @Override
    public Page<Recipe> findAll(Pageable pageable) {
        calledMethods.add("findAll");
        return null;
        // TODO Auto-generated method stub
    }
}

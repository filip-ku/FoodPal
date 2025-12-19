package server.Controller;

import commons.Ingredient;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.Repository.IngredientRepository;

import java.lang.reflect.Field;
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
 * They exist only to satisfy the compiler </p>
 */
public class TestIngredientRepository implements IngredientRepository {

    public final List<Ingredient> ingredients = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private final AtomicLong idSequence = new AtomicLong(1); // Will be used set the recipe ids.

    @Override
    public void flush() {
        calledMethods.add("flush");
    }

    @Override
    public <S extends Ingredient> S saveAndFlush(S entity) {
        calledMethods.add("saveAndFlush");
        return save(entity);
    }

    @Override
    public <S extends Ingredient> List<S> saveAllAndFlush(Iterable<S> entities) {
        calledMethods.add("saveAllAndFlush");
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public void deleteAllInBatch(Iterable<Ingredient> entities) {
        calledMethods.add("deleteAllInBatch");
        for (Ingredient entity : entities) {
            ingredients.removeIf(i -> i.getId().equals(entity.getId()));
        }
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        calledMethods.add("deleteAllByIdInBatch");
        for (Long id : longs) {
            ingredients.removeIf(i -> i.getId().equals(id));
        }
    }

    @Override
    public void deleteAllInBatch() {
        calledMethods.add("deleteAllInBatch");
        ingredients.clear();
    }

    @Override
    public Ingredient getOne(Long aLong) {
        calledMethods.add("getOne");
        return findById(aLong).orElse(null);
    }

    @Override
    public Ingredient getById(Long aLong) {
        calledMethods.add("getById");
        return findById(aLong).orElse(null);
    }

    @Override
    public Ingredient getReferenceById(Long aLong) {
        calledMethods.add("getReferenceById");
        return findById(aLong).orElse(null);
    }

    @Override
    public <S extends Ingredient> Optional<S> findOne(Example<S> example) {
        calledMethods.add("findOne");
        return Optional.empty();
    }

    @Override
    public <S extends Ingredient> List<S> findAll(Example<S> example) {
        calledMethods.add("findAll");
        return List.of();
    }

    @Override
    public <S extends Ingredient> List<S> findAll(Example<S> example, Sort sort) {
        calledMethods.add("findAll");
        return List.of();
    }

    @Override
    public <S extends Ingredient> Page<S> findAll(Example<S> example, Pageable pageable) {
        calledMethods.add("findAll");
        return Page.empty();
    }

    @Override
    public <S extends Ingredient> long count(Example<S> example) {
        calledMethods.add("count");
        return 0;
    }

    @Override
    public <S extends Ingredient> boolean exists(Example<S> example) {
        calledMethods.add("exists");
        return false;
    }

    @Override
    public <S extends Ingredient, R>
        R findBy(Example<S> example,
                 Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        calledMethods.add("findBy");
        return null;
    }

    /**
     * Saves an {@link Ingredient} instance.
     *
     * <p>If the ingredient doesn't have an id (null), it will be assigned
     * a new unique id by {@code idSequence} (simulating AUTO generation). <br>
     * If the ingredient already has an id, it will overwrite the old ingredient
     * with the new ingredient.</p>
     *
     * @param entity the {@link Ingredient} to be saved
     * @return the saved {@link Ingredient} (with its id set)
     * @param <S> a subset of {@link Ingredient}
     */
    @Override
    public <S extends Ingredient> S save(S entity) {
        calledMethods.add("save");
        if (entity.getId() == null) {
            setId(entity, idSequence.getAndIncrement());
        } else {
            ingredients.removeIf(ingredient -> ingredient.getId().equals(entity.getId()));
        }

        ingredients.add(entity);
        return entity;
    }

    /**
     * Sets the id field of an ingredient using reflection.
     * This is necessary because the Ingredient class uses @GeneratedValue
     * and may not have a public setter.
     *
     * @param ingredient the ingredient to modify
     * @param id the id to set
     */
    private void setId(Ingredient ingredient, Long id) {
        try {
            Field idField = ingredient.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ingredient, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set id on ingredient", e);
        }
    }

    @Override
    public <S extends Ingredient> List<S> saveAll(Iterable<S> entities) {
        calledMethods.add("saveAll");
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    /**
     * Retrieve an {@link Ingredient} by its id.
     *
     * @param id of the Ingredient to fetch
     * @return an optional ingredient, if found.
     */
    @Override
    public Optional<Ingredient> findById(Long id) {
        calledMethods.add("findById");
        return ingredients.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    /**
     * Check if an ingredient with a given id exists in the repository.
     *
     * @param id of an ingredient to be searched for
     * @return boolean reflecting if there is an ingredient with the provided id
     */
    @Override
    public boolean existsById(Long id) {
        calledMethods.add("existsById");
        return ingredients.stream().anyMatch(ingredient -> ingredient.getId().equals(id));
    }

    /**
     * return all saved ingredients.
     * @return all saved ingredients
     */
    @Override
    public List<Ingredient> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(ingredients);
    }

    @Override
    public List<Ingredient> findAllById(Iterable<Long> ids) {
        calledMethods.add("findAllById");
        List<Ingredient> result = new ArrayList<>();
        for (Long id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public long count() {
        calledMethods.add("count");
        return ingredients.size();
    }

    /**
     * Deletes an {@link Ingredient} by its id, if the ingredient got found.
     * @param id of the ingredient that needs to be deleted
     */
    @Override
    public void deleteById(Long id) {
        calledMethods.add("deleteById");
        ingredients.removeIf(ingredient -> ingredient.getId().equals(id));
    }

    @Override
    public void delete(Ingredient ingredient) {
        calledMethods.add("delete");
        if (ingredient != null && ingredient.getId() != null) {
            ingredients.removeIf(i -> i.getId().equals(ingredient.getId()));
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        calledMethods.add("deleteAllById");
        for (Long id : ids) {
            ingredients.removeIf(i -> i.getId().equals(id));
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Ingredient> entities) {
        calledMethods.add("deleteAll");
        for (Ingredient entity : entities) {
            if (entity != null && entity.getId() != null) {
                ingredients.removeIf(i -> i.getId().equals(entity.getId()));
            }
        }
    }

    @Override
    public void deleteAll() {
        calledMethods.add("deleteAll");
        ingredients.clear();
    }

    @Override
    public List<Ingredient> findAll(Sort sort) {
        calledMethods.add("findAll");
        return new ArrayList<>(ingredients);
    }

    @Override
    public Page<Ingredient> findAll(Pageable pageable) {
        calledMethods.add("findAll");
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Ingredient> pageContent;
        if (startItem >= ingredients.size()) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, ingredients.size());
            pageContent = new ArrayList<>(ingredients.subList(startItem, toIndex));
        }

        return new PageImpl<>(pageContent, pageable, ingredients.size());
    }
}

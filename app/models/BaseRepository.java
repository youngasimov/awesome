package models;

import com.google.inject.ImplementedBy;

import javax.persistence.EntityManager;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@ImplementedBy(JPABaseRepository.class)
public interface BaseRepository {
    <T> CompletionStage<T> withTransaction(Function<EntityManager, T> function);

    <T> T insert(T toPersist, EntityManager em);

    <T> T update(T toUpdate, EntityManager em);
}
package models;

import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

class JPABaseRepository implements BaseRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    JPABaseRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public <T> CompletionStage<T> withTransaction(Function<EntityManager, T> function) {
        return CompletableFuture.supplyAsync(() -> jpaApi.withTransaction(function), executionContext);
    }

    public <T> T insert(T toPersist, EntityManager em) {
        em.persist(toPersist);
        return toPersist;
    }

    @Override
    public <T> T update(T toUpdate, EntityManager em) {
        return em.merge(toUpdate);
    }
}

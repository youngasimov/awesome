package models;

import akka.actor.ActorSystem;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPAAccountRepository implements AccountRepository {

    private final ActorSystem system;
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAAccountRepository(ActorSystem system, JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.system = system;
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Account> createAccount(Account account) {
        return supplyAsync(() -> wrap(em -> insert(em, account)), executionContext);
    }

    @Override
    public CompletionStage<Account> getAccount(long id) {
        return getAccount(id, false);
    }

    @Override
    public CompletionStage<Account> getAccount(long id, boolean withNewTransactions) {
        return withNewTransactions ?
                supplyAsync(() -> wrap(em -> em.createQuery(
                        "select a, a.debits, a.credits from Account a where a.id = :id " +
                                "and a.debits.createdAt > a.balanceAt and a.credits.createdAt > a.balanceAt",
                        Account.class)
                        .setParameter("id", id)
                        .getSingleResult())) :
                supplyAsync(() -> wrap(em -> em.find(Account.class, id)));
    }

    @Override
    public CompletionStage<Account> updateAccountAsync(Account account, Duration timeout) {
        return supplyAsync(() -> wrap(em -> {
            return null;
        }));
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private <T> T insert(EntityManager em, T toPersist) {
        em.persist(toPersist);
        return toPersist;
    }
}

package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.stream.Stream;

public class JPATransactionRepository extends JPABaseRepository implements TransactionRepository {

    @Inject
    public JPATransactionRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public Transaction find(long id, EntityManager em) {
        return em.find(Transaction.class, id);
    }

    @Override
    public Stream<Transaction> get(long accountId, EntityManager em) {
        return em.createQuery(
                "SELECT t FROM Transaction t " +
                        "WHERE t.sender.id = :account_id " +
                        "OR t.receiver.id = :account_id", Transaction.class)
                .setParameter("account_id", accountId)
                .getResultList().stream();
    }


    @Override
    public Transaction create(Transaction transaction, EntityManager em) {
        return insert(transaction, em);
    }
}

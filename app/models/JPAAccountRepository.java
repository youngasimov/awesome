package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JPAAccountRepository extends JPABaseRepository implements AccountRepository {

    @Inject
    public JPAAccountRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public Account create(Account account, EntityManager em) {
        return insert(account, em);
    }

    @Override
    public Stream<Account> get(EntityManager em) {
        return em.createQuery("select a from Account a", Account.class).getResultList().stream();
    }

    @Override
    public Account find(long id, boolean withTransactions, EntityManager em) {
        return withTransactions ? find(id, null, em) : em.find(Account.class, id);
    }

    @Override
    public Account find(long id, LocalDateTime from, EntityManager em) {
        String query = "SELECT a FROM Account a " +
                "LEFT JOIN FETCH a.debits d " +
                "LEFT JOIN FETCH a.credits c " +
                "WHERE a.id = :id";
        if (from != null) {
            query += "AND (d.createdAt > :from OR c.createdAt > :from)";
        }
        TypedQuery<Account> typedQuery = em.createQuery(query, Account.class).setParameter("id", id);
        if (from != null) {
            typedQuery = typedQuery.setParameter("from", from);
        }
        Account account = typedQuery.getSingleResult();
        List<Transaction> transactions = new ArrayList<>(account.getDebits());
        for (Transaction transaction : transactions) {
            transaction.setAmount(-transaction.getAmount());
        }
        transactions.addAll(account.getCredits());
        transactions.sort((o1, o2) -> o1.getCreatedAt().equals(o2.getCreatedAt()) ? 0 :
                (o1.getCreatedAt().isAfter(o2.getCreatedAt()) ? 1 : -1));
        account.setTransactions(transactions);
        return account;
    }
}

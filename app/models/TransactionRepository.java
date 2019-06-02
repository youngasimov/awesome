package models;

import com.google.inject.ImplementedBy;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

@ImplementedBy(JPATransactionRepository.class)
public interface TransactionRepository extends BaseRepository {

    Transaction find(long id, EntityManager em);

    Stream<Transaction> get(long account, EntityManager em);

    Transaction create(Transaction transaction, EntityManager em);
}
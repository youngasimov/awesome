package models;

import com.google.inject.ImplementedBy;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@ImplementedBy(JPAAccountRepository.class)
public interface AccountRepository extends BaseRepository {

    Account create(Account account, EntityManager em);

    Stream<Account> get(EntityManager em);

    Account find(long id, boolean withTransactions, EntityManager em);

    Account find(long id, LocalDateTime transactionsFrom, EntityManager em);

}

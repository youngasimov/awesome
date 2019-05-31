package models;

import com.google.inject.ImplementedBy;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@ImplementedBy(JPAAccountRepository.class)
public interface AccountRepository {

    CompletionStage<Account> createAccount(Account account);

    CompletionStage<Account> getAccount(long id);

    CompletionStage<Account> getAccount(long id, boolean withNewTransactions);

    CompletionStage<Account> updateAccountAsync(Account account, Duration timeout);
}

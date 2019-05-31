package controllers;

import models.Account;
import models.AccountRepository;
import models.Transaction;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;


public class AccountController extends Controller {

    private final AccountRepository accountRepository;
    private final HttpExecutionContext ec;

    @Inject
    public AccountController(AccountRepository accountRepository, HttpExecutionContext ec) {
        this.accountRepository = accountRepository;
        this.ec = ec;
    }

    public CompletionStage<Result> create(final Http.Request request) {
        Account account = Json.fromJson(request.body().asJson(), Account.class);
        account.setId(null);
        LocalDateTime now = LocalDateTime.now();
        double amount = account.getBalance();
        if (amount > 0) {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            account.addCredit(transaction);
        }
        account.setBalanceAt(now);
        return this.accountRepository.createAccount(account)
                .thenApplyAsync(createdAccount -> ok(toJson(createdAccount)), ec.current());
    }

    public CompletionStage<Result> show(long id) {
        return this.accountRepository.getAccount(id, true)
                .thenApplyAsync(this::updateBalance, ec.current())
                .thenApplyAsync(this::responseOk, ec.current());
    }

    private Account updateBalance(Account account) {
        double balance = account.getBalance();
        for (Transaction transaction : account.getCredits()) {
            balance += transaction.amount;
        }
        for (Transaction transaction : account.getDebits()) {
            balance -= transaction.amount;
        }
        account.setBalance(balance);
        return account;
    }

    private Result responseOk(Account account) {
        return ok(toJson(account));
    }
}

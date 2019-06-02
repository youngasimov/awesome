package controllers;

import models.Account;
import models.AccountRepository;
import models.Transaction;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class AccountController extends Controller {

    private final AccountRepository accountRepository;
    private final HttpExecutionContext ec;

    @Inject
    public AccountController(AccountRepository accountRepository, HttpExecutionContext ec) {
        this.accountRepository = accountRepository;
        this.ec = ec;
    }

    public CompletionStage<Result> index() {
        return accountRepository
                .withTransaction(accountRepository::get)
                .thenApplyAsync(accountStream -> ok(toJson(accountStream.collect(Collectors.toList()))), ec.current())
                .exceptionally(throwable -> Results.internalServerError(throwable.getMessage()));
    }

    public CompletionStage<Result> show(long id, String from) {
        return accountRepository
                .withTransaction(em -> from == null ?
                        this.accountRepository.find(id, true, em) :
                        this.accountRepository.find(id, LocalDateTime.parse(from), em))
                .thenApplyAsync(this::responseOk, ec.current())
                .exceptionally(throwable -> {
                    if (throwable instanceof DateTimeParseException) {
                        return Results.badRequest("Date format not recognised");
                    }
                    return internalServerError(throwable.getMessage());
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> create(final Http.Request request) {
        Account account = Json.fromJson(request.body().asJson(), Account.class);
        account.setId(null);
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        double amount = account.getInitialDeposit();
        if (amount < 0) {
            return CompletableFuture.completedStage(Results.notAcceptable("Initial deposit can not be negative"));
        } else if (amount > 0) {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType(Transaction.TYPE_DEPOSIT);
            transaction.setCreatedAt(LocalDateTime.now());
            account.addCredit(transaction);
            account.setBalance(amount);
        }
        return accountRepository.withTransaction(em -> accountRepository.create(account, em))
                .thenApplyAsync(createdAccount -> created(toJson(createdAccount)), ec.current())
                .exceptionally(throwable -> Results.internalServerError(throwable.getMessage()));

    }

    private Result responseOk(Account account) {
        return ok(toJson(account));
    }
}

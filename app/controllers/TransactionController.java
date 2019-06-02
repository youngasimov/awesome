package controllers;

import exceptions.NotFoundException;
import exceptions.NotSufficientsFundsException;
import models.Account;
import models.AccountRepository;
import models.Transaction;
import models.TransactionRepository;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class TransactionController extends Controller {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final HttpExecutionContext ec;

    @Inject
    public TransactionController(TransactionRepository transactionRepository, AccountRepository accountRepository,
                                 HttpExecutionContext ec) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.ec = ec;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> create(final Http.Request request) {
        Transaction transaction = Json.fromJson(request.body().asJson(), Transaction.class);
        if (transaction.getAmount() <= 0) {
            return CompletableFuture
                    .completedStage(notAcceptable("Transaction must be bigger than zero"));
        }
        if (transaction.getSenderId() < 0 && transaction.getReceiverId() < 0) {
            return CompletableFuture
                    .completedStage(notAcceptable("Transaction must have a sender or/and a receiver"));
        }
        if (transaction.getSenderId() == transaction.getReceiverId()) {
            return CompletableFuture
                    .completedStage(notAcceptable("Transaction to same account is not permitted"));
        }
        transaction.setType(Transaction.TYPE_TRANSACTION);
        if (transaction.getSenderId() >= 0 && transaction.getReceiverId() < 0) {
            transaction.setType(Transaction.TYPE_WITHDRAW);
        } else if (transaction.getSenderId() < 0 && transaction.getReceiverId() >= 0) {
            transaction.setType(Transaction.TYPE_DEPOSIT);
        }
        LocalDateTime now = LocalDateTime.now();
        transaction.setId(0);
        transaction.setCreatedAt(now);
        return transactionRepository
                .withTransaction(em -> {
                    if (transaction.getSenderId() > -1) {
                        Account sender = accountRepository
                                .find(transaction.getSenderId(), false, em);
                        if (sender == null) {
                            throw new NotFoundException("Sender not found");
                        }
                        if (sender.getBalance() < transaction.getAmount()) {
                            throw new NotSufficientsFundsException(
                                    "maximum transfer amount for " + sender.getName() + " is " + sender.getBalance());
                        }
                        sender.setBalance(sender.getBalance() - transaction.getAmount());
                        sender.setUpdatedAt(now);
                        accountRepository.update(sender, em);
                        transaction.setSender(sender);
                    }
                    if (transaction.getReceiverId() > -1) {
                        Account receiver = accountRepository
                                .find(transaction.getReceiverId(), false, em);
                        if (receiver == null) {
                            throw new NotFoundException("Receiver not found");
                        }
                        receiver.setBalance(receiver.getBalance() + transaction.getAmount());
                        receiver.setUpdatedAt(now);
                        accountRepository.update(receiver, em);
                        transaction.setReceiver(receiver);
                    }
                    return transactionRepository.create(transaction, em);
                })
                .thenApplyAsync(createdAccount -> created(toJson(createdAccount)), ec.current())
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof NotSufficientsFundsException) {
                        return notAcceptable(throwable.getMessage());
                    }
                    return Results.internalServerError(throwable.getMessage());
                });
    }

}

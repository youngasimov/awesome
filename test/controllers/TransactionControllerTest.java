package controllers;

import models.Account;
import models.AccountRepository;
import models.Transaction;
import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.*;
import static play.mvc.Http.Status.CREATED;
import static play.test.Helpers.*;

public class TransactionControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return fakeApplication();
    }

    @Test
    public void testCreate() throws Exception {

        AccountRepository accountRepository = provideApplication().injector().instanceOf(AccountRepository.class);
        Account account1 = accountRepository.withTransaction(em -> {
            LocalDateTime td = LocalDateTime.now();
            Account a = new Account();
            Transaction t = new Transaction();
            a.setName("test-account");
            a.setBalance(100);
            a.setCreatedAt(td);
            t.setAmount(100);
            t.setCreatedAt(td);
            a.addCredit(t);
            accountRepository.create(a, em);
            return a;
        }).toCompletableFuture().get();

        Account account2 = accountRepository.withTransaction(em -> {
            LocalDateTime td = LocalDateTime.now();
            Account a = new Account();
            Transaction t = new Transaction();
            a.setName("test-account-2");
            a.setBalance(200);
            a.setCreatedAt(td);
            t.setAmount(200);
            t.setCreatedAt(td);
            a.addCredit(t);
            accountRepository.create(a, em);
            return a;
        }).toCompletableFuture().get();

        //Test successful transaction
        Http.RequestBuilder request = Helpers.fakeRequest(POST, "/v1/transactions").bodyJson(parse("{\n" +
                "  \"senderId\": " + account2.getId() + ",\n" +
                "  \"receiverId\": " + account1.getId() + ",\n" +
                "  \"amount\": 45\n" +
                "}"));
        Result result = route(app, request);
        assertEquals(result.reasonPhrase().orElse("transaction fail"), CREATED, result.status());
        Transaction executedTransaction = fromJson(parse(contentAsString(result)), Transaction.class);
        assertEquals(45, executedTransaction.getAmount(), 0.001);
        assertEquals(Transaction.TYPE_TRANSACTION, executedTransaction.getType());

        //Test not found exception
        request = Helpers.fakeRequest(POST, "/v1/transactions").bodyJson(parse("{\n" +
                "  \"senderId\": " + account2.getId() + ",\n" +
                "  \"receiverId\": " + account1.getId() + ",\n" +
                "  \"amount\": 300\n" +
                "}"));
        result = route(app, request);
        assertEquals(NOT_ACCEPTABLE, result.status());
    }
}

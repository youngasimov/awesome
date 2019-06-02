package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.AccountRepository;
import models.Transaction;
import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static play.libs.Json.*;
import static play.test.Helpers.*;

public class AccountControllerTest extends WithApplication {


    @Override
    protected Application provideApplication() {
        return fakeApplication();
    }

    @Test
    public void testIndex() throws Exception {
        AccountRepository accountRepository = provideApplication().injector().instanceOf(AccountRepository.class);
        accountRepository.withTransaction(em -> {
            Account a = new Account();
            Account b = new Account();
            a.setName("test-account-index");
            b.setName("test-account-index-2");
            accountRepository.create(a, em);
            accountRepository.create(b, em);
            return a;
        }).toCompletableFuture().get();
        Http.RequestBuilder request = Helpers.fakeRequest(GET, "/v1/accounts");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        Account[] accounts = fromJson(parse(contentAsString(result)), Account[].class);
        assertEquals(2, accounts.length);
    }

    @Test
    public void testShow() {
        Http.RequestBuilder request = Helpers.fakeRequest(POST, "/v1/accounts").bodyJson(parse(
                "{\n" +
                        "  \"name\": \"test-account-show\",\n" +
                        "  \"initialDeposit\": 100\n" +
                        "}"
        ));
        Result result = route(app, request);
        Account account = fromJson(parse(contentAsString(result)), Account.class);
        request = Helpers.fakeRequest(GET, "/v1/accounts/" + account.getId());
        result = route(app, request);
        assertEquals(OK, result.status());
        Account account2 = fromJson(parse(contentAsString(result)), Account.class);
        assertEquals(100, account.getBalance(), 0.001);
        assertEquals(account.getId(), account2.getId());
    }

    @Test
    public void testCreate() {
        Http.RequestBuilder request = Helpers.fakeRequest(POST, "/v1/accounts").bodyJson(parse(
                "{\n" +
                        "  \"name\": \"test-account-create\",\n" +
                        "  \"initialDeposit\": 200\n" +
                        "}"
        ));
        Result result = route(app, request);
        assertEquals(CREATED, result.status());
        JsonNode node = parse(contentAsString(result));
        Account account = fromJson(node, Account.class);
        Transaction[] transactions = fromJson(node.get("transactions"), Transaction[].class);
        assertEquals(200, account.getBalance(), 0.001);
        assertEquals("test-account-create", account.getName());
        assertNotNull(account.getId());
        assertEquals(1, transactions.length);
    }
}

package models;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@SuppressWarnings({"WeakerAccess", "unused"})
public class Account{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Basic
    @Column(unique = true, nullable = false)
    public String name;

    @Basic
    public double balance;

    @Basic
    @Column(name = "balance_at")
    public LocalDateTime balanceAt;

    @OneToMany(mappedBy = "sender")
    public Set<Transaction> debits;

    @OneToMany(mappedBy = "receiver")
    public Set<Transaction> credits;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Column(name = "balance_at")
    public LocalDateTime getBalanceAt() {
        return balanceAt;
    }

    public void setBalanceAt(LocalDateTime balanceAt) {
        this.balanceAt = balanceAt;
    }

    public Set<Transaction> getDebits() {
        return debits;
    }

    public void setDebits(Set<Transaction> debits) {
        this.debits = debits;
    }

    public Set<Transaction> getCredits() {
        return credits;
    }

    public void setCredits(Set<Transaction> credits) {
        this.credits = credits;
    }

    public void addCredit(Transaction transaction) {
        transaction.setReceiver(this);
        this.credits.add(transaction);
    }

    public void addDebit(Transaction transaction) {
        transaction.setSender(this);
        this.debits.add(transaction);
    }
}

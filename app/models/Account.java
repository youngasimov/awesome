package models;


import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"})
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Basic
    @Column(unique = true, nullable = false)
    public String name;

    @Basic
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime createdAt;

    @Basic
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime updatedAt;

    @Basic
    public double balance;

    @Transient
    public double initialDeposit;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<Transaction> debits = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<Transaction> credits = new HashSet<>();


    @Transient
    @JsonManagedReference
    public List<Transaction> transactions = new ArrayList<>();

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

    @JsonProperty
    public double getBalance() {
        return balance;
    }

    @JsonIgnore
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @JsonProperty
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public double getInitialDeposit() {
        return initialDeposit;
    }

    @JsonProperty
    public void setInitialDeposit(double initialDeposit) {
        this.initialDeposit = initialDeposit;
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonIgnore
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addCredit(Transaction transaction) {
        transaction.setReceiver(this);
        this.credits.add(transaction);
        this.transactions.add(transaction);
    }

    public void addDebit(Transaction transaction) {
        transaction.setSender(this);
        this.debits.add(transaction);
        this.transactions.add(transaction);
    }
}

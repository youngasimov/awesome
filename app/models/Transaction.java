package models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@SuppressWarnings({"WeakerAccess", "unused"})
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Transaction {

    public static final String TYPE_TRANSACTION = "transaction";
    public static final String TYPE_DEPOSIT = "deposit";
    public static final String TYPE_WITHDRAW = "withdraw";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Basic
    public double amount;

    @Basic
    public String type;

    @Transient
    public long senderId = -1;

    @Transient
    public long receiverId = -1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @JsonBackReference(value = "sender")
    public Account sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    @JsonBackReference(value = "receiver")
    public Account receiver;

    @Basic
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime createdAt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public long getSenderId() {
        return senderId;
    }

    @JsonProperty
    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    @JsonIgnore
    public long getReceiverId() {
        return receiverId;
    }

    @JsonProperty
    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public Account getSender() {
        return sender;
    }

    @JsonIgnore
    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    @JsonIgnore
    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

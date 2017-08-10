package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.Data;
import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthPaymentPK;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by sasha on 29.06.17.
 */
@Data
@Entity
@Table(name = "eth_payment")
public class EthPayment implements Serializable {

    private static final long serialVersionUID = -8585352259435548395L;

    public interface Status {
        int NON_CREATED = 0; // For payments
        int CREATED = 1;
        int ACCEPTED = 2;
        int CANCELED = 3;
        int ERROR = 4;
        int TIMEOUT = 5;    // For payments
    }

    public interface Type {
        int PAYMENT = 0;
        int PAYOUT = 1;
    }

    @Id
    private EthPaymentPK id;

    @Column(name = "hash")
    private String hash;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creating_time", nullable = false)
    private Date creatingTime = new Date();

    @Column(name = "status")
    private Integer status = Status.NON_CREATED;

    @Column(name = "from_address")
    private String from;

    @Column(name = "to_address")
    private String to;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "amount")
    private BigInteger amount = BigInteger.ZERO;

    @Column(name = "error")
    private String error;

    @Column(name = "stack_trace")
    private String stackTrace;

    public String getStatusName() {
        switch (status) {
            case Status.NON_CREATED:
                return "non_created";
            case Status.CREATED:
                return "created";
            case Status.ACCEPTED:
                return "accepted";
            case Status.CANCELED:
                return "canceled";
            case Status.ERROR:
                return "error";
            case Status.TIMEOUT:
                return "timeout";
            default:
                throw new RuntimeException("Unknown transaction status: " + status);
        }
    }

    public String getTypeName() {
        return getTypeName(id.getType());
    }

    public static String getTypeName(int type) {
        switch (type) {
            case Type.PAYMENT:
                return "payment";
            case Type.PAYOUT:
                return "payout";
            default:
                throw new RuntimeException("Unknown transaction type: " + type);
        }
    }

    public static int getType(String type) {
        switch (type) {
            case "payment":
                return Type.PAYMENT;
            case "payout":
                return Type.PAYOUT;
            default:
                throw new RuntimeException("Unknown transaction type: " + type);
        }
    }
}

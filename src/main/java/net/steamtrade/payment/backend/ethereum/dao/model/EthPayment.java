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
        int CREATED = 1;
        int ACCEPTED = 2;
        int CHECK_PAYOUT = 3;
        int ERROR = 4;
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

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "from_address")
    private String from;

    @Column(name = "to_address")
    private String to;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "amount")
    private BigInteger amount = BigInteger.ZERO;

    @Column(name = "payment_request_id")
    private String paymentRequestId;

    @Column(name = "error")
    private String error;

    @Column(name = "stack_trace")
    private String stackTrace;

    public String getStatusName() {
        switch (status) {
            case Status.CHECK_PAYOUT:
            case Status.CREATED:
                return "created";
            case Status.ACCEPTED:
                return "accepted";
            case Status.ERROR:
                return "error";
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

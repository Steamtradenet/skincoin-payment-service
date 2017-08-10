package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.Data;
import net.steamtrade.payment.backend.Currency;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by sasha on 04.07.17.
 */
@Data
@Entity
@Table(name = "eth_transaction")
public class EthTransaction implements Serializable {

    private static final long serialVersionUID = 3653136368295205722L;

    public interface Status {
        int PENDING = 0;
        int CREATED = 1;
        int ACCEPTED = 2;
        int ERROR = 3;
    }

    @Id
    @Column(name = "hash")
    private String hash;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "block_number")
    private BigInteger blockNumber;


    @Column(name = "nonce")
    private BigInteger nonce;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creating_time", nullable = false)
    private Date creatingTime = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable = false)
    private Date updateTime = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "hold_time")
    private Date holdTime;

    @Column(name = "status")
    private Integer status = Status.PENDING;

    @Column(name = "from_address")
    private String from;

    @Column(name = "to_address")
    private String to;

    @Column(name = "amount")
    private BigInteger amount;

    @Column(name = "gas")
    private BigInteger gas;

    @Column(name = "gas_used")
    private BigInteger gasUsed;

    @Column(name = "cumulative_gas_used")
    private BigInteger cumulativeGasUsed;

    @Column(name = "gas_price")
    private BigInteger gasPrice;

    @Column(name = "error")
    private String error;

    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "locked", nullable = false, columnDefinition = "tinyint(1) default 0")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean locked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

}

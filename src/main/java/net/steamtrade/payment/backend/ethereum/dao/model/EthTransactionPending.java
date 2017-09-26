package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by sasha on 04.07.17.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "eth_transaction_pending")
public class EthTransactionPending implements Serializable {

    private static final long serialVersionUID = -2131652510854258037L;

    public EthTransactionPending(String hash) {
        this.hash = hash;
    }

    @Id
    @Column(name = "hash")
    private String hash;

    @Column(name = "locked", nullable = false, columnDefinition = "tinyint(1) default 0")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean locked = false;
}

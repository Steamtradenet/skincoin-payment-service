package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by sasha on 04.07.17.
 */
@Data
@Entity
@Table(name = "eth_filter")
@NoArgsConstructor
@AllArgsConstructor
public class EthFilter implements Serializable {

    private static final long serialVersionUID = 3455836579168762170L;

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private BigInteger value;

}

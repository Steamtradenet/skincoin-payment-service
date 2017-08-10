package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.Data;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthAccountPK;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sasha on 29.06.17.
 */
@Data
@Entity
@Table(name = "eth_account")
public class EthAccount implements Serializable {

    private static final long serialVersionUID = -7640758162336574243L;

    public interface Status {
        int NEW = 0;
        int USED = 1;
    }

    @Id
    private EthAccountPK id;

    @Column(name = "request_id")
    private String requestId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creating_time", nullable = false)
    private Date creatingTime = new Date();

    @Column(name = "address")
    private String address;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Integer status = Status.NEW;

}

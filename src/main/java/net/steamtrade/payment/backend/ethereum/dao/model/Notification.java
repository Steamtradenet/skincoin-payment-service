package net.steamtrade.payment.backend.ethereum.dao.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sasha on 24.07.17.
 */
@Data
@Entity
@Table(name = "notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = -739306016685706581L;

    public interface Gateway {
        String ETHEREUM = "ether";
    }

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString();

    @Column(name = "app_id")
    private Integer appId;

    @Column(name = "gateway")
    private String gateway;

    @Column(name = "type")
    private Integer type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creating_time", nullable = false)
    private Date creatingTime = new Date();

    @Column(name = "request_count", nullable = false, columnDefinition = "default 0")
    private Integer requestCount = 0;

    @Column(name = "data")
    private String data;

    public void increaseRequestCount() {
        requestCount++;
    }

}

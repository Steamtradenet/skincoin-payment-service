package net.steamtrade.payment.backend.admin.dao.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Principal;

/**
 * Created by sasha on 30.06.17.
 */
@Data
@Entity
@Table(name = "app")
public class App implements Serializable, Principal {

    private static final long serialVersionUID = -3599369728990852777L;

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "token")
    private String token;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "from_password")
    private String fromPassword;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "enable_callback", nullable = false)
    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean enableCallback = false;

    @Column(name = "auth_secret")
    private String authSecret;

    @Override
    public String getName() {
        return id.toString();
    }
}

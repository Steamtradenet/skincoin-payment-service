package net.steamtrade.payment.backend.ethereum.dao.model.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by sasha on 29.06.17.
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EthAccountPK implements Serializable {

    private static final long serialVersionUID = 2091193149849314204L;

    @Column(name = "app_id")
    private Integer appId;

    @Column(name = "account_id")
    private String accountId;
}

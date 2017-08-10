package net.steamtrade.payment.web.admin.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.security.Principal;

/**
 * Created by sasha on 07.08.17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements Principal, Serializable {

    private static final long serialVersionUID = 2512758274328097555L;

    private String name;
}

package net.steamtrade.payment.web.security;

import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.web.admin.auth.Admin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import net.steamtrade.payment.backend.exceptions.Error;

/**
 * Created by sasha on 23.03.16.
 */
public class SecurityContext {

    public static App getCurrentApp() throws AppException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal().getClass() == App.class) {
            return (App) auth.getPrincipal();
        }
        throw new AppException(Error.AUTHENTICATION_FAILED, "Unknown user");
    }


    public static Admin getCurrentManager() throws AppException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal().getClass() == Admin.class) {
            return (Admin) auth.getPrincipal();
        }
        throw new AppException(Error.AUTHENTICATION_FAILED, "Unknown user");
    }
}

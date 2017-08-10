package net.steamtrade.payment.web.security.filters;

import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.web.admin.auth.Admin;
import net.steamtrade.payment.web.security.jwt.JwtTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by sasha on 01.08.17.
 */
@Component
public class JwtFilter extends GenericFilterBean {

    @Autowired
    private JwtTokenHelper tokenHelper;
    @Autowired
    private AppConfig appConfig;

    public void doFilter(final ServletRequest request, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String authToken = tokenHelper.getToken(req);
        if (authToken != null) {
            // get username from token
            try {
                String username = tokenHelper.getUsernameFromToken(authToken);
                if (username != null && username.equals(appConfig.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new Admin(username), "",
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chain.doFilter(req, res);
    }
}

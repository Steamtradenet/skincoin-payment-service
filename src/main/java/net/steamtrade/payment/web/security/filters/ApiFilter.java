package net.steamtrade.payment.web.security.filters;

import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.utils.StringUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sasha on 8/5/17.
 */
@Component
public class ApiFilter extends GenericFilterBean {

    private final AppDao appDao;

    public ApiFilter(AppDao appDao) {
        this.appDao = appDao;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getToken(httpRequest);
        if(!StringUtils.isEmpty(token)) {
            App app = appDao.getAppByToken(token);
            if (app != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(app, "",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest httpRequest) {
        Map<String, String[]> filter = new HashMap(httpRequest.getParameterMap());
        if (filter.get("api_key") != null) {
            return filter.get("api_key")[0];
        }
        return null;
    }
}

package net.steamtrade.payment.web.admin;

import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.exceptions.Error;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.web.admin.json.CredentialsJson;
import net.steamtrade.payment.web.admin.json.JwtTokenJson;
import net.steamtrade.payment.web.security.jwt.JwtTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;

/**
 * Created by sasha on 01.08.17.
 */
@Controller
public class LoginController {

    @Autowired
    private JwtTokenHelper tokenHelper;
    @Autowired
    private AppConfig appConfig;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JwtTokenJson login(CredentialsJson credentials) throws AppException {

        if (StringUtils.isAnyEmpty(credentials.getUsername(), credentials.getPassword())) {
            throw new AppException(Error.AUTHENTICATION_FAILED, "Please fill in username and password");
        }

        if (!credentials.getUsername().equals(appConfig.getUsername()) ||
                !credentials.getPassword().equals(appConfig.getPassword()) ) {
            throw new AppException(Error.AUTHENTICATION_FAILED, "Invalid login. Please check your name and password.");
        }

        String token = tokenHelper.generateToken(credentials.getUsername());
        return new JwtTokenJson(token);
    }
}

package net.steamtrade.payment.web.admin;

import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.admin.service.AppService;
import net.steamtrade.payment.web.admin.auth.Admin;
import net.steamtrade.payment.web.admin.json.AppJson;
import net.steamtrade.payment.web.security.SecurityContext;
import net.steamtrade.payment.web.utils.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by sasha on 8/6/17.
 */

@Controller
public class AdminController {

    @Autowired
    private AppService appService;

    @RequestMapping(value = "/admin/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> currentUser() throws Exception {
        Admin manager = SecurityContext.getCurrentManager();
        return new ResponseEntity<>(manager, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/app", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getApps(HttpServletRequest request) throws Exception {
        List<AppJson> apps = appService.getAllApps().stream().map(JsonBuilder::toAppJson)
                .collect(Collectors.toList());
        return new ResponseEntity<>(apps, HttpStatus.OK);
    }


    @RequestMapping(value = "/admin/app", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getApps(@RequestBody AppJson appJson) throws Exception {
        appJson.validate();

        appService.createOrUpdateApp(fromJson(appJson));
        return new ResponseEntity<>(appJson, HttpStatus.OK);
    }

    private App fromJson(AppJson json) {
        App app = new App();
        app.setId(json.getId());
        app.setDescription(json.getName());
        app.setToken(json.getToken());
        app.setFromAddress(json.getFromAddress());
        app.setFromPassword(json.getFromPassword());
        app.setCallbackUrl(json.getCallbackUrl());
        app.setEnableCallback(json.getEnableCallback());

        return app;
    }
}

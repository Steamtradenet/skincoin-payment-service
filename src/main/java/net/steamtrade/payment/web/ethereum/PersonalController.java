package net.steamtrade.payment.web.ethereum;

import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.service.PersonalService;
import net.steamtrade.payment.json.NewAccountJson;
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

/**
 * Created by sasha on 29.06.17.
 */
@Controller
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    @RequestMapping(value = "/api/personal/createAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createAccount(@RequestBody NewAccountJson body) throws Exception {
        body.validate();

        App app = SecurityContext.getCurrentApp();

        EthAccount result = personalService.getOrCreateAccount(app.getId(), body.getRequestId());
        return new ResponseEntity<>(JsonBuilder.toAccountJson(result), HttpStatus.OK);
    }

}

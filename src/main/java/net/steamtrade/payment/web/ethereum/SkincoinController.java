package net.steamtrade.payment.web.ethereum;

import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.json.EthereumInfoJson;
import net.steamtrade.payment.json.NewRequestJson;
import net.steamtrade.payment.web.ethereum.handler.SkincoinControllerHandler;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sasha on 05.07.17.
 */
@Controller
public class SkincoinController {

    @Autowired
    private SkincoinControllerHandler handler;

    @RequestMapping(value = "/api/ethereum/createPayout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createPayout(@RequestBody NewRequestJson body, HttpServletRequest request) throws Exception {
        body.validate(true);

        Map<String, String[]> filter = new HashMap(request.getParameterMap());

        EthPayment result = handler.createPayout(body, filter);
        return new ResponseEntity<>(JsonBuilder.toPaymentJson(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/ethereum/getStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getPaymentStatus(HttpServletRequest request) throws Exception {
        Map<String, String[]> filter = new HashMap(request.getParameterMap());
        EthPayment result = handler.getStatus(filter);
        return new ResponseEntity<>(JsonBuilder.toPaymentJson(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/ethereum/getInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getEthNetwork() throws Exception {
        EthereumInfoJson result = handler.getInfo();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

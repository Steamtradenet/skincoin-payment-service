package net.steamtrade.payment.web.ethereum;

import net.steamtrade.payment.web.ethereum.handler.EthControllerHandler;
import net.steamtrade.payment.web.utils.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sasha on 6/30/17.
 */
@Controller
public class EthController {

    @Autowired
    private EthControllerHandler handler;

    @RequestMapping(value = "/api/ethereum/getBalance", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getBalance(HttpServletRequest request) throws Exception {
        Map<String, String[]> filter = new HashMap(request.getParameterMap());
        BigInteger result = handler.getBalance(filter);
        return new ResponseEntity<>(JsonBuilder.toAccountJson(result), HttpStatus.OK);
    }
}

package net.steamtrade.payment.web.ethereum.handler;

import net.steamtrade.payment.backend.Currency;
import net.steamtrade.payment.backend.ethereum.config.EthereumConfig;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.service.EthService;
import net.steamtrade.payment.backend.ethereum.service.SkincoinService;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.json.EthereumInfoJson;
import net.steamtrade.payment.json.NewRequestJson;
import net.steamtrade.payment.web.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by sasha on 05.07.17.
 */
@Component
public class SkincoinControllerHandler {

    @Autowired
    private SkincoinService skincoinService;
    @Autowired
    private EthService ethService;
    @Autowired
    private EthereumConfig ethereumConfig;

    public EthPayment createPayout(NewRequestJson newPaymentJson, Map<String, String[]> filter) throws AppException {
        App app = SecurityContext.getCurrentApp();

        BigInteger gasLimit = StringUtils.getOptionalParamValue("gas_limit", filter, BigInteger.class);
        BigInteger gasPrice = StringUtils.getOptionalParamValue("gas_price", filter, BigInteger.class);

        return skincoinService.createPayout(app.getId(), newPaymentJson.getRequestId(), newPaymentJson.getTo(),
                newPaymentJson.getAmount(), Currency.fromName(newPaymentJson.getCurrency()), gasLimit, gasPrice);
    }

    public EthPayment getStatus(Map<String, String[]> filter) throws AppException {
        App app = SecurityContext.getCurrentApp();

        String requestId = StringUtils.getRequiredParamValue("request_id", filter, String.class);
        String type = StringUtils.getRequiredParamValue("type", filter, String.class);;

        return skincoinService.getPayment(app.getId(), EthPayment.getType(type), requestId);
    }

    public EthereumInfoJson getInfo() throws AppException {
        EthereumInfoJson json = new EthereumInfoJson();
        json.setClientVersion(ethService.getClientVersion());
        json.setNetwork(ethService.getEthNetwork().getName());
        json.setSkincoinAddress(ethereumConfig.getSkincoinAddress());
        return json;
    }
}

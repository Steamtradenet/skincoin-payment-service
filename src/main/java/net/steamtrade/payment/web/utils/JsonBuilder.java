package net.steamtrade.payment.web.utils;

import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.json.AccountJson;
import net.steamtrade.payment.json.EthPaymentJson;
import net.steamtrade.payment.web.admin.json.AppJson;

import java.math.BigInteger;

/**
 * Created by sasha on 6/30/17.
 */
public class JsonBuilder {

    public static AccountJson toAccountJson(EthAccount account) {
        AccountJson json = new AccountJson();
        json.setAddress(account.getAddress());
        return json;
    }

    public static AccountJson toAccountJson(BigInteger balance) {
        AccountJson json = new AccountJson();
        json.setBalance(balance);
        return json;
    }

    public static EthPaymentJson toPaymentJson(EthPayment payment) {
        EthPaymentJson json = new EthPaymentJson();
        json.setRequestId(payment.getId().getRequestId());
        json.setCreatingTime(payment.getCreatingTime().getTime());
        json.setStatus(payment.getStatusName());
        json.setType(payment.getTypeName());
        json.setAmount(payment.getAmount());
        json.setCurrency(payment.getCurrency().getName());
        json.setError(payment.getError());
        return json;
    }

    public static AppJson toAppJson(App app) {
        AppJson json = new AppJson();
        json.setId(app.getId());
        json.setName(app.getDescription());
        json.setToken(app.getToken());
        json.setCallbackUrl(app.getCallbackUrl());
        json.setEnableCallback(app.getEnableCallback());
        json.setFromAddress(app.getFromAddress());
        json.setFromPassword(app.getFromPassword());
        return json;
    }

}

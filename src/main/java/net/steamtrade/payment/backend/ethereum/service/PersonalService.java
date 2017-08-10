package net.steamtrade.payment.backend.ethereum.service;

import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.exceptions.AppException;

/**
 * Created by sasha on 29.06.17.
 */
public interface PersonalService {

    EthAccount createNewAccount(int appId, int status) throws AppException;

    EthAccount createNewAccount(int appId, String requestId, int status) throws AppException;

    EthAccount getOrCreateAccount(int appId, String requestId) throws AppException;
}

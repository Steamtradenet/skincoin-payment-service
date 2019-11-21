package net.steamtrade.payment.backend.ethereum.service;

import com.hazelcast.core.HazelcastInstance;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.dao.model.pk.EthAccountPK;
import net.steamtrade.payment.backend.ethereum.utils.AddressUtils;
import net.steamtrade.payment.backend.exceptions.AppException;
import net.steamtrade.payment.backend.utils.PasswordGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.http.HttpService;

import net.steamtrade.payment.backend.exceptions.Error;
import org.web3j.protocol.parity.Parity;

import java.util.concurrent.locks.Lock;

/**
 * Created by sasha on 29.06.17.
 */
@Service
public class PersonalServiceImpl implements PersonalService {

    private static final String NEXT_FREE_ACCOUNT_LOCK = "PersonalServiceImpl.getOrCreateAccount";

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private EthAccountDao accountDao;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * Creates a new account and save it in database
     * @return {@link EthAccount} instance
     * @throws AppException
     */
    @Override
    public EthAccount createNewAccount(int appId, int status) throws AppException {
        return createNewAccount(appId, null, status);
    }

    @Override
    public EthAccount createNewAccount(int appId, String requestId, int status) throws AppException {
        Parity parity = Parity.build(new HttpService(appConfig.getClientUrl()));

        String password = PasswordGen.generate(32);
        try {
            NewAccountIdentifier newAccount = parity.personalNewAccount(password).send();

            EthAccount account = new EthAccount();
            account.setId(new EthAccountPK(appId, newAccount.getAccountId()));
            account.setRequestId(requestId);
            account.setAddress(AddressUtils.accountToAddress(newAccount.getAccountId()));
            account.setPassword(password);
            account.setStatus(status);

            return accountDao.save(account);
        } catch (Exception ex) {
            throw new AppException(Error.UNEXPECTED_EXCEPTION, "", ex);
        }
    }

    /**
     * Threadsafe method. Get next free account from pool or create a new one. May produce NULL in case when</br>
     * no free account is found.
     * @return {@link EthAccount} or NULL
     */
    public EthAccount getOrCreateAccount(int appId, String requestId) throws AppException {
        Lock lock = hazelcastInstance.getLock(NEXT_FREE_ACCOUNT_LOCK);
        lock.lock();
        EthAccount account = accountDao.getByRequestId(appId, requestId);
        try {
            if (account == null) {
                account = accountDao.getAccountFromPool(appId);
                if (account != null) {
                    account.setRequestId(requestId);
                    account.setStatus(EthAccount.Status.USED);
                    accountDao.save(account);
                    return account;
                }
                account = createNewAccount(appId, requestId, EthAccount.Status.USED);
            }
        } finally {
            lock.unlock();
        }
        return account;
    }
}

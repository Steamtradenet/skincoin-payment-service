package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.config.AppConfig;
import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.ethereum.dao.EthAccountDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthAccount;
import net.steamtrade.payment.backend.ethereum.service.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
public class AccountCreatorService {

    @Autowired
    private EthAccountDao accountDao;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private PersonalService personalService;
    @Autowired
    private LogServiceClient log;
    @Autowired
    private AppDao appDao;

    public void poll() {
        List<App> apps = appDao.getAll();

        for (App app: apps) {
            long freeAccounts = accountDao.getAccountCountInPool(app.getId());

            while (appConfig.getAccountPoolSize() > freeAccounts) {
                try {
                    EthAccount account = personalService.createNewAccount(
                            app.getId(), EthAccount.Status.NEW);
                    log.info(this.getClass(), "Account(appId:"+account.getId().getAppId()+
                            ", accountId:"+account.getId().getAccountId()+")");
                } catch (Exception ex) {
                    log.error(this.getClass(), "Can't create an account", ex);
                }
                freeAccounts++;
            }
        }
    }
}

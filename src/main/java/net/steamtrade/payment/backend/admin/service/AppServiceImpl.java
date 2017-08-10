package net.steamtrade.payment.backend.admin.service;

import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sasha on 8/6/17.
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppDao appDao;

    @Override
    public List<App> getAllApps() {
        return appDao.getAll();
    }

    @Override
    public App createOrUpdateApp(App app) {
        return appDao.save(app);
    }
}

package net.steamtrade.payment.backend.admin.service;

import net.steamtrade.payment.backend.admin.dao.model.App;

import java.util.List;

/**
 * Created by sasha on 8/6/17.
 */
public interface AppService {

    List<App> getAllApps();

    App createOrUpdateApp(App app);
}

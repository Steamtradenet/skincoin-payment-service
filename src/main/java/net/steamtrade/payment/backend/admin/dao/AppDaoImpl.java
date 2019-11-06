package net.steamtrade.payment.backend.admin.dao;

import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.admin.dao.repository.AppRepository;
import net.steamtrade.payment.backend.cache.CacheManager;
import net.steamtrade.payment.backend.cache.CacheName;
import net.steamtrade.payment.backend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sasha on 29.06.17.
 */
@Repository
public class AppDaoImpl implements AppDao {

    @Autowired
    private AppRepository repository;
    @Autowired
    private CacheManager cacheManager;

    public App save(App app) {
        App result = repository.save(app);
        if (!StringUtils.isEmpty(app.getFromAddress())) {
            cacheManager.addValueToCache(CacheName.APP_ADDRESS_CACHE,
                    app.getFromAddress().toLowerCase(), 1);
        }
        return result;
    }

    public App getAppById(int id) {
        return repository.findById(id).get();
    }

    public App getAppByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public List<App> getAll() {
        return repository.findAll();
    }
}

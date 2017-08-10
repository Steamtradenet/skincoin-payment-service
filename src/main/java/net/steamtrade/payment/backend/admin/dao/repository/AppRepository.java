package net.steamtrade.payment.backend.admin.dao.repository;

import net.steamtrade.payment.backend.admin.dao.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sasha on 29.06.17.
 */
public interface AppRepository extends JpaRepository<App, Integer> {

    App findByToken(String token);

}

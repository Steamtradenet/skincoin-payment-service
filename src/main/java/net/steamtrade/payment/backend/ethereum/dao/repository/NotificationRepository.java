package net.steamtrade.payment.backend.ethereum.dao.repository;

import net.steamtrade.payment.backend.ethereum.dao.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created by sasha on 29.06.17.
 */
public interface NotificationRepository extends JpaRepository<Notification, String> {
}

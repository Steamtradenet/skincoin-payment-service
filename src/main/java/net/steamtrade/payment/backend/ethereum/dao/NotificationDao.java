package net.steamtrade.payment.backend.ethereum.dao;

import net.steamtrade.payment.backend.ethereum.dao.model.Notification;

import java.util.List;

/**
 * Created by sasha on 7/25/17.
 */
public interface NotificationDao {

    Notification save(Notification notification);

    List<Notification> getNotifications(int appId, String gateway, Integer type, int limit);

    void delete(Notification notification);
}

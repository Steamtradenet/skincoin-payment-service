package net.steamtrade.payment.backend.ethereum.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import net.steamtrade.payment.backend.ethereum.dao.model.Notification;
import net.steamtrade.payment.backend.ethereum.dao.model.QNotification;
import net.steamtrade.payment.backend.ethereum.dao.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by sasha on 7/25/17.
 */
@Repository
public class NotificationDaoImpl implements NotificationDao {

    @Autowired
    private NotificationRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    @Override
    public List<Notification> getNotifications(int appId, String gateway, Integer type, int limit) {
        QNotification notification = QNotification.notification;

        JPAQuery<Notification> query = new JPAQuery<>(em);
        BooleanBuilder where = new BooleanBuilder();

        where.and(notification.appId.eq(appId).and(notification.gateway.eq(gateway)));
        if (type != null) {
            where.and(notification.type.eq(type));
        }
        return query.from(notification).where(where).orderBy(notification.creatingTime.asc()).limit(limit).fetch();
    }

    @Override
    public void delete(Notification notification) {
        repository.delete(notification);
    }
}

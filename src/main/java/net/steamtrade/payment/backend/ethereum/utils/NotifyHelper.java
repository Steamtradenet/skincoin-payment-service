package net.steamtrade.payment.backend.ethereum.utils;

import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.ethereum.dao.NotificationDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.EthPayment;
import net.steamtrade.payment.backend.ethereum.dao.model.Notification;
import net.steamtrade.payment.backend.ethereum.service.SocketService;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.json.EthPaymentJson;
import net.steamtrade.payment.json.SocketMessage;
import net.steamtrade.payment.web.utils.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sasha on 24.07.17.
 */
@Component
public class NotifyHelper {

    @Autowired
    private AppDao appDao;
    @Autowired
    private SocketService socketService;
    @Autowired
    private NotificationDao notificationDao;

    public void notifyStatusChanged(EthPayment payment) {
        App app = appDao.getAppById(payment.getId().getAppId());
        notifyStatusChangedViaWebSocket(app, payment);

        if (app.getEnableCallback()) {
            notifyStatusChangedViaRest(app, payment);
        }
    }

    private void notifyStatusChangedViaWebSocket(App app, EthPayment payment) {
        SocketMessage socketMessage = new SocketMessage(SocketMessage.Type.STATUS);
        socketMessage.addPayload("data", JsonBuilder.toPaymentJson(payment));
        socketService.sendToUserSilently(app, socketMessage);
    }

    private void notifyStatusChangedViaRest(App app, EthPayment payment) {
        EthPaymentJson paymentJson = JsonBuilder.toPaymentJson(payment);
        Notification notification = new Notification();
        notification.setAppId(app.getId());
        notification.setGateway(Notification.Gateway.ETHEREUM);
        notification.setType(payment.getId().getType());
        notification.setData(StringUtils.toJson(paymentJson));
        notificationDao.save(notification);
    }
}

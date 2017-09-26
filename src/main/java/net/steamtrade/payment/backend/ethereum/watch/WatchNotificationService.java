package net.steamtrade.payment.backend.ethereum.watch;

import net.steamtrade.payment.backend.client.LogServiceClient;
import net.steamtrade.payment.backend.admin.dao.AppDao;
import net.steamtrade.payment.backend.ethereum.dao.NotificationDao;
import net.steamtrade.payment.backend.admin.dao.model.App;
import net.steamtrade.payment.backend.ethereum.dao.model.Notification;
import net.steamtrade.payment.backend.utils.DateUtils;
import net.steamtrade.payment.backend.utils.SHA256Utils;
import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.json.EthPaymentJson;
import net.steamtrade.payment.json.SimpleSuccessJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sasha on 25.07.17.
 */
public class WatchNotificationService {

    private static final int PACKAGE_SIZE = 100;

    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private AppDao appDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LogServiceClient log;

    private Map<Integer, Date> delayMap = new HashMap<>();

    public void poll() {
        List<App> apps = appDao.getAll();
        for (App app: apps) {
            if (!StringUtils.isEmpty(app.getCallbackUrl()) && !hasHold(app.getId())) {
                try {
                    List<Notification> notifications = notificationDao.getNotifications(app.getId(),
                            Notification.Gateway.ETHEREUM, null, PACKAGE_SIZE);
                    if (notifications.size() > 0) {
                        log.info(this.getClass(), "Start notify app(id:"+app.getId()+
                                ",description:"+app.getDescription()+",callbackUrl:"+app.getCallbackUrl()+
                                ",from_address:"+app.getFromAddress()+"). Found "+notifications.size()+" notifications.");
                    }
                    for (Notification notification: notifications) {
                        EthPaymentJson data = StringUtils.fromJson(notification.getData(), EthPaymentJson.class);

                        SimpleSuccessJson response;
                        if (!StringUtils.isEmpty(app.getAuthSecret())) {
                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Authorization", SHA256Utils.encodeToHex(app.getAuthSecret()));
                            HttpEntity<EthPaymentJson> httpEntity = new HttpEntity<>(data, headers);
                            response = restTemplate.postForObject(app.getCallbackUrl(), httpEntity, SimpleSuccessJson.class);
                        } else {
                            response = restTemplate.postForObject(app.getCallbackUrl(), data, SimpleSuccessJson.class);
                        }

                        if (response != null &&  response.getSuccess() != null && response.getSuccess()) {
                            notificationDao.delete(notification); // Remove notification
                        }
                    }
                } catch (Exception ex) {
                    delayMap.put(app.getId(), DateUtils.addMinutes(new Date(), 1));
                    log.error(this.getClass(), "Can't notify app(id:"+app.getId()+"). Sleep 1 min", ex);
                }
            }
        }
    }


    private boolean hasHold(int appId) {
        Date delay = delayMap.get(appId);
        return delay != null && delay.compareTo(new Date()) > 0;
    }
}

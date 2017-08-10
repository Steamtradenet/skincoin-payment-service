package net.steamtrade.payment.backend.ethereum.service;

import net.steamtrade.payment.backend.utils.StringUtils;
import net.steamtrade.payment.json.SocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Created by sasha on 12.06.16.
 */
@Service
public class SocketService {

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Principal principal, SocketMessage socketMessage) {
        if (isUserOnline(principal)) {
            SocketMessage.Type type = socketMessage.getType();
            messagingTemplate.convertAndSendToUser(principal.getName(), type.getDestination(),
                    StringUtils.toJson(socketMessage));
        }
    }

    public void broadcast(SocketMessage socketMessage) {
        SocketMessage.Type type = socketMessage.getType();
        messagingTemplate.convertAndSend(type.getDestination(), StringUtils.toJson(socketMessage));
    }

    public int getUsersCount() {
        return simpUserRegistry.getUsers().size();
    }

    private boolean isUserOnline(Principal principal) {
        SimpUser simpUser = simpUserRegistry.getUser(principal.getName());
        return simpUser != null && simpUser.getSessions().size() > 0;
    }

    public void sendToUserSilently(Principal principal, SocketMessage socketMessage) {
        try {
            sendToUser(principal, socketMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void broadcastSilently(SocketMessage socketMessage) {
        try {
            broadcast(socketMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

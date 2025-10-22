package com.smarthome.Service;

import com.smarthome.dto.StartLongLat;
import com.smarthome.model.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    private final Map<String, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    private final Map<String, Session> activeSession = new ConcurrentHashMap<>();
    private  final Map<String, StartLongLat> startLongLat= new ConcurrentHashMap<>();

    public void addOnlineUser(String uuid, WebSocketSession session) {
        onlineUsers.put(uuid, session);
    }

    public void removeOnlineUser(String uuid) {
        onlineUsers.remove(uuid);
    }

    public boolean isUserOnline(String uuid) {
        WebSocketSession session = onlineUsers.get(uuid);
        return session != null && session.isOpen();
    }

    public WebSocketSession getUserSession(String uuid) {
        return onlineUsers.get(uuid);
    }

    public Map<String, WebSocketSession> getAllOnlineUsers() {
        return Collections.unmodifiableMap(onlineUsers);
    }

    public void setActiveSession(String uuid, Session session) {
        activeSession.put(uuid, session);
    }

    public Session getActiveSession(String uuid) {
        return activeSession.get(uuid);
    }

    public void removeActiveSession(String uuid) {
        activeSession.remove(uuid);
    }

    public  void addStartLongLat(String uuid, StartLongLat longLat){
        startLongLat.put(uuid,longLat);
    }
    public  StartLongLat getLongLat(String uuid){
        return startLongLat.get(uuid);
    }
    public void removeLongLat(String uuid){
        startLongLat.remove(uuid);
    }
}

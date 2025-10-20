package com.smarthome.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.Service.UserService;
import com.smarthome.Service.UserSessionService;
import com.smarthome.model.Location;
import com.smarthome.model.Session;
import com.smarthome.model.User;
import com.smarthome.repository.LocationRepository;
import com.smarthome.repository.SessionRepository;
import com.smarthome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserWebSocketController extends TextWebSocketHandler {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final double EARTH_RADIUS = 6371000;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connected: " + session.getId());
        String successResponse = """
                {
                    "status": 200,
                    "message": "Connected to server successfully"
                }
                """;
        session.sendMessage(new TextMessage(successResponse));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
            Map<String, Object> profile = null;
            String uuid = "";

            if (data.containsKey("profile")) {
                profile = (Map<String, Object>) data.get("profile");
                uuid = (String) profile.get("user_id");
                System.out.println("Received profile data: " + profile);
            }

            if (!userSessionService.isUserOnline(uuid)) {

                session.getAttributes().put("uuid", uuid);

                userSessionService.addOnlineUser(uuid, session);

                // If location data is being sent during reconnection, mark the session as active again
                if (data.containsKey("location")) {
                    Session activeSession = userSessionService.getActiveSession(uuid);
                    if (activeSession != null) {
                        Session s = sessionRepository.findById(activeSession.getSessionId()).orElse(null);
                        if (s != null && s.getSession_status() == Session.SessionStatus.Not_Able_to_Connect) {
                            s.setSession_status(Session.SessionStatus.Active);
                            activeSession.setSession_status(Session.SessionStatus.Active);
                            sessionRepository.save(s);
                            System.out.println("✅ Reconnected: Session " + s.getSession_name() + " marked as Active again");
                        }
                    }
                }
            }

            // 🔹 Handle session creation
            if (data.containsKey("session")) {
                List<Map<String, Object>> sessionList = (List<Map<String, Object>>) data.get("session");

                if (profile == null || !profile.containsKey("user_id")) {
                    System.out.println("⚠️ Profile or user_id missing — skipping session save");
                    return;
                }

                Optional<User> optionalUser = userRepository.findById(Long.parseLong(uuid));
                if (!optionalUser.isPresent()) {
                    throw new RuntimeException("User not found with id: " + uuid);
                }
                User user = optionalUser.get();

                for (Map<String, Object> s : sessionList) {
                    String sessionName = (String) s.getOrDefault("session_name", "Untitled");
                    String startLat = (String) s.getOrDefault("start_lat", "0.0");
                    String startLon = (String) s.getOrDefault("start_lon", "0.0");
                    String notes = (String) s.getOrDefault("notes", null);
                    String startTimeStr = (String) s.getOrDefault("start_time", null);

                    LocalDateTime startTime = (startTimeStr != null)
                            ? LocalDateTime.parse(startTimeStr)
                            : LocalDateTime.now();

                    Session newSession = new Session();
                    newSession.setUser(user);
                    newSession.setSession_name(sessionName);
                    newSession.setStart_lat(Double.parseDouble(startLat));
                    newSession.setStart_lon(Double.parseDouble(startLon));
                    newSession.setStart_time(startTime);
                    newSession.setSession_status(Session.SessionStatus.Active);
                    newSession.setNotes(notes);

                    Session savedSession = sessionRepository.save(newSession);
                    userSessionService.setActiveSession(uuid, savedSession);

                    session.sendMessage(new TextMessage(
                            "{\"status\":201, \"session_id\":\"" + savedSession.getSessionId() +
                                    "\", \"session_name\":\"" + savedSession.getSession_name() + "\"}"
                    ));
                }
            }

            // 🔹 Handle location updates
            else if (data.containsKey("location")) {
                List<Map<String, Object>> locationList = (List<Map<String, Object>>) data.get("location");
                Session activeSession = userSessionService.getActiveSession(uuid);

                if (activeSession == null) {
                    session.sendMessage(new TextMessage("{\"error\":\"No active session found for location update\"}"));
                    return;
                }

                for (Map<String, Object> loc : locationList) {
                    Double lat = Double.parseDouble(loc.get("lat").toString());
                    Double lon = Double.parseDouble(loc.get("lon").toString());
                    LocalDateTime timestamp = LocalDateTime.parse(loc.get("timestamp").toString());

                    double distance = calculateDistance(activeSession.getStart_lat(), activeSession.getStart_lon(), lat, lon);

                    // Ignore minimal GPS drift (<30m)
                    if (distance >= 30) {
                        activeSession.setTotal_distance(activeSession.getTotal_distance() + distance);

                        Location location = new Location();
                        location.setLat(lat);
                        location.setLon(lon);
                        location.setTimestamp(timestamp);
                        location.setSession(activeSession);
                        locationRepository.save(location);

                        activeSession.setStart_lat(lat);
                        activeSession.setStart_lon(lon);
                        sessionRepository.save(activeSession);
                        session.sendMessage(new TextMessage("{\"status\":201, \"message\":\"Location updated\"}"));
                    }
                }

                session.sendMessage(new TextMessage("{\"status\":200, \"message\":\"Location received\"}"));
            }

            // 🔹 Handle session end and WebSocket close
            else if (data.containsKey("end_time")) {
                try {
                    String endTimeStr = data.get("end_time").toString();
                    LocalDateTime endTime = LocalDateTime.parse(endTimeStr);

                    Session activeSession = userSessionService.getActiveSession(uuid);
                    if (activeSession != null) {
                        Long activeSessionId = activeSession.getSessionId();
                        Session sessionEntity = sessionRepository.findById(activeSessionId)
                                .orElseThrow(() -> new RuntimeException("Session not found with id: " + activeSessionId));

                        sessionEntity.setEnd_time(endTime);
                        sessionEntity.setSession_status(Session.SessionStatus.Close);
                        sessionRepository.save(sessionEntity);

                        userSessionService.removeActiveSession(uuid);
                        userSessionService.removeOnlineUser(uuid);

                        session.sendMessage(new TextMessage(
                                "{\"status\":200, " +
                                        "\"message\":\"Session closed successfully\", " +
                                        "\"session_name\":\"" + sessionEntity.getSession_name() + "\"}"
                        ));

                        session.close(CloseStatus.NORMAL);
                        System.out.println("Session " + sessionEntity.getSession_name() + " closed successfully for user " + uuid);
                    } else {
                        session.sendMessage(new TextMessage("{\"error\":\"No active session found for user\"}"));
                    }

                } catch (Exception e) {
                    System.out.println("Failed to parse end_time: " + e.getMessage());
                    session.sendMessage(new TextMessage("{\"error\":\"Invalid end_time format\"}"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage("{\"status\":500,\"error\":\"" + e.getMessage() + "\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object uuidObj = session.getAttributes().get("uuid");
        if (uuidObj != null) {
            String uuid = uuidObj.toString();
            if (!uuid.isEmpty()) {
                userSessionService.removeOnlineUser(uuid);

                Session activeSession = userSessionService.getActiveSession(uuid);
                if (activeSession != null) {
                    Session s = sessionRepository.findById(activeSession.getSessionId()).orElse(null);
                    if (s != null) {
                        s.setSession_status(Session.SessionStatus.Not_Able_to_Connect);
                        activeSession.setSession_status(Session.SessionStatus.Not_Able_to_Connect);
                        sessionRepository.save(s);
                    }
                }

                System.out.println("User " + uuid + " disconnected");
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}

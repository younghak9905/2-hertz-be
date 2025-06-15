package com.hertz.hertz_be.global.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SocketIoController {
    private final SocketIOServer server;

    public SocketIoController(SocketIOServer server) {
        this.server = server;

        server.addConnectListener(listenConnected());
    }

    public ConnectListener listenConnected() {
        return (client) -> {
            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
            log.info(":: SocketIo Connect - " + params.toString() + " ::");
        };
    }

    public DisconnectListener listenDisconnected() {
        return (client) -> {
            String sessionId = client.getSessionId().toString();
            log.info(":: SocketIo Disconnect - " + sessionId + " ::");
            client.disconnect();
        };
    }
}

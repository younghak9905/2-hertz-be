package com.hertz.hertz_be.global.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hertz.hertz_be.domain.channel.dto.socketio.MessageRequest;
import com.hertz.hertz_be.domain.channel.dto.socketio.MessageResponse;
import com.hertz.hertz_be.domain.channel.entity.SignalMessage;
import com.hertz.hertz_be.global.auth.token.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.hertz.hertz_be.domain.channel.service.MessageService;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SocketIoController {
    private final SocketIOServer server;
    private final MessageService messageService;
    private final JwtTokenProvider jwtTokenProvider;

    public SocketIoController(SocketIOServer server, MessageService messageService, JwtTokenProvider jwtTokenProvider) {
        this.server = server;
        this.messageService = messageService;
        this.jwtTokenProvider = jwtTokenProvider;

        server.addConnectListener(listenConnected());

        server.addEventListener("send_message", MessageRequest.class, (client, data, ackSender) -> {
            Long senderId = getUserIdFromClient(client);

            log.info("[{}] 채널 {} 에게 메세지 : {}", senderId, data.roomId(), data.message());

            SignalMessage savedMessage = messageService.saveMessage(data.roomId(), senderId, data.message());

            server.getRoomOperations("room-" + data.roomId()).sendEvent("receive_message", MessageResponse.from(savedMessage));
        });
    }

    private Long getUserIdFromClient(SocketIOClient client) {
        List<String> tokens = client.getHandshakeData().getUrlParams().get("token"); // 토큰 불러옴

        return jwtTokenProvider.getUserIdFromToken(tokens.get(0));
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

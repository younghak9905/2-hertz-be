package com.hertz.hertz_be.global.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketIoServerRunner {

    private final SocketIOServer socketIOServer;

    @PostConstruct
    public void startServer() {
        socketIOServer.start();
    }

    @PreDestroy
    public void stopServer() {
        socketIOServer.stop();
    }
}
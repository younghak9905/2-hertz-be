package com.hertz.hertz_be.global.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.hertz.hertz_be.global.socketio.CustomJsonSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SocketIoConfig {

    @Value("${socketio.server.hostname}")
    private String hostname;

    @Value("${socketio.server.port}")
    private int port;

    @Bean
    public SocketIOServer socketIoServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        config.setOrigin("*");
        config.setAllowCustomRequests(true);
        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);

        // 커스텀 JSON 처리기
        config.setJsonSupport(new CustomJsonSupport());

        return new SocketIOServer(config); // 여기선 start() 하지 않음
    }
}
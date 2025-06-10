package com.hertz.hertz_be.global.infra.ai.client;

import com.hertz.hertz_be.global.exception.AiServerBadRequestException;
import com.hertz.hertz_be.global.infra.ai.dto.AiTuningReportGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TuningAiClient {

    @Value("${ai.server.ip}")
    private String AI_SERVER_URL;
    private static WebClient webClient;

    @Autowired
    public TuningAiClient(RestTemplate restTemplate) {
        this.webClient = WebClient.builder().baseUrl(AI_SERVER_URL).build();
    }


    public Map<String, Object> requestTuningReport(AiTuningReportGenerationRequest request) {
        String uri = AI_SERVER_URL + "api/v2/report";
        try{
            return webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new AiServerBadRequestException();
        }

    }
}

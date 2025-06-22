package com.hertz.hertz_be.global.infra.ai.client;

import com.hertz.hertz_be.global.exception.AiServerBadRequestException;
import com.hertz.hertz_be.global.infra.ai.dto.AiTuningReportGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TuningAiClient {

    @Value("${ai.tuningreport.ip}")
    private String AI_TUNING_REPORT_IP;
    private final WebClient.Builder webClientBuilder;



    public Map<String, Object> requestTuningReport(AiTuningReportGenerationRequest aiReportRequest) {
        WebClient webClient = webClientBuilder.baseUrl(AI_TUNING_REPORT_IP).build();
        String uri = "api/v2/report";

        try{
            return webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(aiReportRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new AiServerBadRequestException();
        }
    }
}

package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.channel.entity.SignalRoom;
import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import com.hertz.hertz_be.domain.tuningreport.repository.TuningReportRepository;
import com.hertz.hertz_be.domain.user.entity.User;
import com.hertz.hertz_be.domain.user.repository.UserRepository;
import com.hertz.hertz_be.global.infra.ai.client.TuningAiClient;
import com.hertz.hertz_be.global.infra.ai.dto.AiSignalRoomDto;
import com.hertz.hertz_be.global.infra.ai.dto.AiTuningReportGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TuningReportGenerationWriter implements ItemWriter<AiTuningReportGenerationRequest> {

    private final TuningReportRepository tuningReportRepository;
    private final TuningAiClient tuningAiClient;
    private final UserRepository userRepository;

    @Override
    public void write(Chunk<? extends AiTuningReportGenerationRequest> chunk) {
        if (chunk.isEmpty()) return;

        List<TuningReport> reports = chunk.getItems().stream()
                .map(request -> {
                    AiSignalRoomDto dto = request.signalRoom();

                    // senderUser, receiverUser 조회
                    User sender = userRepository.getReferenceById(dto.senderUserId());
                    User receiver = userRepository.getReferenceById(dto.receiverUserId());

                    // DTO → Entity 변환
                    SignalRoom room = dto.toEntity(sender, receiver);

                    // AI 서버 호출
                    Map<String, Object> response = tuningAiClient.requestTuningReport(request);

                    return TuningReport.of(room, request.emailDomain(), response);
                })
                .toList();

        tuningReportRepository.saveAll(reports);
    }
}

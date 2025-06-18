package com.hertz.hertz_be.global.batch;

import com.hertz.hertz_be.domain.channel.entity.SignalRoom;
import com.hertz.hertz_be.domain.channel.repository.SignalMessageRepository;
import com.hertz.hertz_be.domain.user.repository.UserRepository;
import com.hertz.hertz_be.global.infra.ai.dto.AiTuningReportGenerationRequest;
import com.hertz.hertz_be.global.infra.ai.support.UserDataAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TuningReportGenerationProcessor implements ItemProcessor<SignalRoom, AiTuningReportGenerationRequest> {

    private final UserDataAssembler userDataAssembler;
    private final SignalMessageRepository signalMessageRepository;
    private final UserRepository userRepository;
    @Override
    public AiTuningReportGenerationRequest process(SignalRoom room) throws Exception {
        int chatCount = signalMessageRepository.countBySignalRoom(room);

        AiTuningReportGenerationRequest.UserData userA = userDataAssembler.assemble(room.getSenderUser().getId());
        AiTuningReportGenerationRequest.UserData userB = userDataAssembler.assemble(room.getReceiverUser().getId());

        String senderEmailDomain = userRepository.findDistinctEmailDomains(room.getSenderUser().getId());

        return AiTuningReportGenerationRequest.of(room, chatCount, userA, userB, senderEmailDomain);
    }
}

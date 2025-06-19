package com.hertz.hertz_be.domain.tuningreport.repository;

import com.hertz.hertz_be.domain.channel.entity.SignalRoom;
import com.hertz.hertz_be.domain.tuningreport.entity.TuningReport;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuningReportRepository extends JpaRepository<TuningReport, Long> {

    @Query("SELECT r FROM TuningReport r WHERE r.deletedAt IS NULL AND r.isVisible = true ORDER BY r.createdAt DESC")
    Page<TuningReport> findAllNotDeletedOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT r FROM TuningReport r
    WHERE r.deletedAt IS NULL
    AND r.isVisible = true
    ORDER BY (
        r.reactionCelebrate +
        r.reactionThumbsUp +
        r.reactionLaugh +
        r.reactionEyes +
        r.reactionHeart
    ) DESC
""")
    Page<TuningReport> findAllNotDeletedOrderByTotalReactionDesc(Pageable pageable);

    @Query("SELECT r FROM TuningReport r WHERE r.signalRoom = :signalRoom AND r.deletedAt IS NULL")
    Optional<TuningReport> findNotDeletedBySignalRoom(@Param("signalRoom") SignalRoom signalRoom);

    List<TuningReport> findAllBySignalRoomIn(List<SignalRoom> rooms);

    @Modifying
    @Query("UPDATE TuningReport t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void softDeleteById(@Param("id") Long id);
}

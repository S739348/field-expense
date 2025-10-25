package com.smarthome.repository;

import com.smarthome.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // ✅ Get sessions for a specific user between start and end time
    @Query("SELECT s FROM Session s WHERE s.user.userId = :userId AND s.start_time BETWEEN :start AND :end")
    List<Session> findByUserAndStartTimeRange(@Param("userId") Long userId,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    // ✅ Get sessions for multiple users between start and end time
    @Query("SELECT s FROM Session s WHERE s.user.userId IN :userIds AND s.start_time BETWEEN :start AND :end")
    List<Session> findByUserIdsAndStartTimeRange(@Param("userIds") List<Long> userIds,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    // ✅ Get all sessions between start and end time
    @Query("SELECT s FROM Session s WHERE s.start_time BETWEEN :start AND :end")
    List<Session> findAllByStartTimeRange(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}

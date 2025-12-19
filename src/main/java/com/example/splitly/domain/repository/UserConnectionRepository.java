package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.UserConnection;
import com.example.splitly.domain.entity.UserConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId> {

    /**
     * Find connection by request user ID and receive user ID
     */
    @Query("""
                SELECT uc
                FROM UserConnection uc
                WHERE (uc.id.requestUserId = :user1 AND uc.id.receiveUserId = :user2)
                   OR (uc.id.requestUserId = :user2 AND uc.id.receiveUserId = :user1)
            """)
    Optional<UserConnection> findConnectionBetweenUsers(
            @Param("user1") Integer user1,
            @Param("user2") Integer user2
    );

    /**
     * Check if connection exists between two users
     */
    boolean existsByIdRequestUserIdAndIdReceiveUserId(
            Integer requestUserId,
            Integer receiveUserId
    );

    /**
     * Find all connections by acceptance status
     */
    List<UserConnection> findByIsAccepted(boolean isAccepted);

    /**
     * Find all connections where user is either requester or receiver
     */
    @Query("SELECT uc FROM UserConnection uc WHERE uc.id.requestUserId = :userId OR uc.id.receiveUserId = :userId")
    List<UserConnection> findByIdRequestUserIdOrIdReceiveUserId(
            @Param("userId") Integer requestUserId,
            @Param("userId") Integer receiveUserId
    );

    /**
     * Find connections by status where user is either requester or receiver
     */
    @Query("SELECT uc FROM UserConnection uc WHERE uc.isAccepted = :isAccepted " +
            "AND (uc.id.requestUserId = :userId OR uc.id.receiveUserId = :userId)")
    List<UserConnection> findByIsAcceptedAndIdRequestUserIdOrIdReceiveUserId(
            @Param("isAccepted") boolean isAccepted,
            @Param("userId") Integer requestUserId,
            @Param("userId") Integer receiveUserId
    );

    /**
     * Find connections where user is the receiver with specific status
     */
    List<UserConnection> findByIdReceiveUserIdAndIsAccepted(
            Integer receiveUserId,
            boolean isAccepted
    );

    /**
     * Find connections where user is the requester with specific status
     */
    List<UserConnection> findByIdRequestUserIdAndIsAccepted(
            Integer requestUserId,
            boolean isAccepted
    );

    /**
     * Find all accepted connections for a user (either as requester or receiver)
     */
    @Query("SELECT uc FROM UserConnection uc WHERE uc.isAccepted = true " +
            "AND (uc.id.requestUserId = :userId OR uc.id.receiveUserId = :userId)")
    List<UserConnection> findAcceptedConnectionsByUserId(@Param("userId") Integer userId);

    /**
     * Find all pending connections for a user (either as requester or receiver)
     */
    @Query("SELECT uc FROM UserConnection uc WHERE uc.isAccepted = false " +
            "AND (uc.id.requestUserId = :userId OR uc.id.receiveUserId = :userId)")
    List<UserConnection> findPendingConnectionsByUserId(@Param("userId") Integer userId);

    /**
     * Count accepted connections for a user
     */
    @Query("SELECT COUNT(uc) FROM UserConnection uc WHERE uc.isAccepted = true " +
            "AND (uc.id.requestUserId = :userId OR uc.id.receiveUserId = :userId)")
    long countAcceptedConnectionsByUserId(@Param("userId") Integer userId);

    /**
     * Check if two users have an accepted connection
     */
    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN true ELSE false END " +
            "FROM UserConnection uc WHERE uc.isAccepted = true " +
            "AND ((uc.id.requestUserId = :userId1 AND uc.id.receiveUserId = :userId2) " +
            "OR (uc.id.requestUserId = :userId2 AND uc.id.receiveUserId = :userId1))")
    boolean areUsersConnected(
            @Param("userId1") Integer userId1,
            @Param("userId2") Integer userId2
    );

    /**
     * Delete connection by request and receive user IDs
     */
    void deleteByIdRequestUserIdAndIdReceiveUserId(
            Integer requestUserId,
            Integer receiveUserId
    );
}
package com.example.splitly.domain.repository;

import java.util.List;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.splitly.domain.entity.UserDebt;

@Repository
public interface UserDebtRepository extends JpaRepository<UserDebt, Long> {
  List<UserDebt> findByDebtorUserId(Integer debtorId);

  @Query("""
      SELECT ud
      FROM UserDebt ud
      JOIN ud.debtor u
      JOIN GroupUser gu
        ON gu.groupUserId.userId = u.userId
       AND gu.groupUserId.groupId = :groupId
      WHERE u.userId = :debtorId
      """)
  List<UserDebt> findAllDebtInGroup(@Param("groupId") Long groupId,
      @Param("debtorId") Integer debtorId);

  UserDebt findByCreditorUserIdAndDebtorUserId(Integer creditorId, Integer debtorId);

  UserDebt findByUserDebtId(Integer userDebtId);

  boolean existsByGroupInfo_GroupIdAndStatusAndCreditor_UserIdOrGroupInfo_GroupIdAndStatusAndDebtor_UserId(
      Long groupId1, Boolean status1, Integer creditorId,
      Long groupId2, Boolean status2, Integer debtorId);
}

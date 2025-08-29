package com.example.splitly.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.GroupUser;
import com.example.splitly.domain.entity.GroupUserId;
import com.example.splitly.domain.entity.User;



@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {
    @Query("SELECT gu.user FROM GroupUser gu WHERE gu.groupInfo.groupId = :groupId and gu.status = SUCCESS")
    List<User> findUsersByGroupId(@Param("groupId") Long groupId);

    @Query("Select gu.groupInfo from GroupUser gu Where gu.user.userId = :userId")
    List<GroupInfo> findAllGroupsByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("delete from GroupUser gu where gu.groupInfo.groupId = :groupId")
    void deleteGroupUserById(@Param("groupId") Long groupId);

}

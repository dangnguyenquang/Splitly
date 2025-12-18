package com.example.splitly.application.serviceInterface;

import java.util.List;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.domain.entity.User;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


public interface IGroupInfo {
    List<GroupInfoResponse> getAllGroup();

    GroupInfoResponse createGroup(String groupName, List<String> emailList);

    GroupInfoResponse updateGroup(Long groupId, GroupDTO groupDTO);

    void deleteGroup(Long groupId);

    GroupInfoResponse findGroupInfoResponse(Long groupId);

    GroupInfo findGroupInfo(Long groupId);
    public User findLeader(Long groupId);

    void assignLeader(Long groupId, Integer userId);

    @Transactional
    void handleQuitGroup(Long groupId);

    void uploadGroupAvatar(MultipartFile file, Long groupId);
}

package com.example.splitly.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.splitly.application.serviceInterface.IGroupInfo;
import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.presentation.dto.request.CreateGroupRequest;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import com.example.splitly.presentation.dto.response.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupInfoController {
    private final IGroupInfo interfaceGroupInfo;

    @GetMapping("/all")
    public ResponseEntity<ResponseData<?>> getAllGroups() {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all groups successfully!",
                interfaceGroupInfo.getAllGroup());
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ResponseData<?>> getGroupById(@PathVariable Long groupId) {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all groups successfully!",
                interfaceGroupInfo.findGroupInfoResponse(groupId).orElseGet(GroupInfoResponse::new));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/create-group")
    public ResponseEntity<ResponseData<?>> createGroupInfo(@Valid @RequestBody CreateGroupRequest createGroupRequest) {
        GroupInfoResponse groupInfo = interfaceGroupInfo.createGroup(createGroupRequest);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Create group successfully!",
                groupInfo);
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/update-group/{groupId}")
    public ResponseEntity<ResponseData<?>> updateGroupInfo(@PathVariable Long groupId, @RequestBody GroupDTO groupDTO) {

        GroupInfoResponse groupInfo = interfaceGroupInfo.updateGroup(groupId, groupDTO);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Update group successfully!",
                groupInfo);
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/delete-group/{groupId}")
    public ResponseEntity<ResponseData<?>> deleteGroupInfo(@PathVariable Long groupId) {

        interfaceGroupInfo.deleteGroup(groupId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Delete group successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/{groupId}/assign-leader/{userId}")
    public ResponseEntity<ResponseData<?>> assignLeader(@PathVariable Long groupId, @PathVariable Integer userId) {

        interfaceGroupInfo.assignLeader(groupId, userId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Assign leader successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/upload-avatar/{groupId}")
    public ResponseEntity<ResponseData<?>> uploadAvatarGroup (@PathVariable Long groupId, @RequestParam("file") MultipartFile file) {
        interfaceGroupInfo.uploadGroupAvatar(file, groupId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Upload successfully!");
        return ResponseEntity.ok(responseData);
    }
}

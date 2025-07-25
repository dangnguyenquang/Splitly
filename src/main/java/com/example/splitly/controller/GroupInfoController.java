package com.example.splitly.controller;

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

import com.example.splitly.dto.request.GroupDTO;
import com.example.splitly.dto.response.ResponseData;
import com.example.splitly.entity.GroupInfo;
import com.example.splitly.service.InterfaceGroupInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupInfoController {
    private final InterfaceGroupInfo interfaceGroupInfo;

    @GetMapping("/get-all")
    public ResponseEntity<ResponseData<?>> getAllGroups() {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all groups successfully!",
                interfaceGroupInfo.getAllGroup());
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/create-group")
    public ResponseEntity<ResponseData<?>> createGroupInfo(@RequestBody GroupDTO groupDTO) {
        GroupInfo groupInfo = interfaceGroupInfo.createGroup(groupDTO);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Create group successfully!",
                groupInfo);
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/update-group/{groupId}")
    public ResponseEntity<ResponseData<?>> updateGroupInfo(@PathVariable Long groupId, @RequestBody GroupDTO groupDTO) {

        GroupInfo groupInfo = interfaceGroupInfo.updateGroup(groupId, groupDTO);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Update group successfully!",
                groupInfo);
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/delete-group/{groupId}")
    public ResponseEntity<ResponseData<?>> deleteGroupInfo(@PathVariable Long groupId) {

        interfaceGroupInfo.deleteGroup(groupId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Create group successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/assign-leader")
        public ResponseEntity<ResponseData<?>> assignLeader(@RequestParam Long groupId, @RequestParam Integer userId) {

        interfaceGroupInfo.assignLeader(groupId, userId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Assign leader successfully!");
        return ResponseEntity.ok(responseData);
    }
}

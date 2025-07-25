package com.example.splitly.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.splitly.dto.response.ResponseData;
import com.example.splitly.service.IGroupUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/manage-group")
@RequiredArgsConstructor
public class GroupUserController {
    private final IGroupUser iGroupUser;

    @GetMapping("/get-all-users/{groupId}")
    public ResponseEntity<ResponseData<?>> getAllUsersInGroup(@PathVariable Long groupId) {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all users successfully!",
                iGroupUser.getAllUserGroup(groupId));
        return ResponseEntity.ok(responseData);
    }

    @PatchMapping("/invite-user")
    public ResponseEntity<ResponseData<?>> inviteUser(@RequestParam Integer userId, @RequestParam Long groupId) {
        iGroupUser.inviteUserToGroup(userId, groupId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Invite user successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PatchMapping("/remove-user")
    public ResponseEntity<ResponseData<?>> removeUser(@RequestParam Integer userId, @RequestParam Long groupId, @RequestParam Integer leaderId) {
        iGroupUser.removeUserOutGroup(userId, groupId, leaderId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Remove user successfully!");
        return ResponseEntity.ok(responseData);
    }

}

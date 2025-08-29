package com.example.splitly.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.splitly.application.serviceInterface.IGroupUser;
import com.example.splitly.presentation.dto.response.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class GroupUserController {
    private final IGroupUser iGroupUser;

    @GetMapping("/groups/{groupId}/users")
    public ResponseEntity<ResponseData<?>> getAllUsersInGroup(@PathVariable Long groupId) {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all users successfully!",
                iGroupUser.getAllUserGroup(groupId));
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/user/{userId}/groups")
    public ResponseEntity<ResponseData<?>> getGroupsByUserId(@PathVariable Integer userId) {
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Get all groups successfully!",
                iGroupUser.getGroupsByUserId(userId));
        return ResponseEntity.ok(responseData);
    }

    @PatchMapping("/groups/{groupId}/invitation")
    public ResponseEntity<ResponseData<?>> inviteUser(@RequestParam String email, @PathVariable Long groupId) {
        iGroupUser.inviteUserToGroup(email, groupId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Invite user successfully!");
        return ResponseEntity.ok(responseData);
    }

    @PatchMapping("/groups/{groupId}/removal")
    public ResponseEntity<ResponseData<?>> removeUser(@RequestParam Integer userId, @PathVariable Long groupId,
            @RequestParam Integer leaderId) {
        iGroupUser.removeUserOutGroup(userId, groupId, leaderId);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Remove user successfully!");
        return ResponseEntity.ok(responseData);
    }

}

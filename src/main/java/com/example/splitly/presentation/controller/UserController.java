package com.example.splitly.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.splitly.application.service.UserService;
import com.example.splitly.application.serviceInterface.IUserConnection;
import com.example.splitly.application.serviceInterface.IUserService;
import com.example.splitly.presentation.dto.request.UserConnectionRequest;
import com.example.splitly.presentation.dto.request.UserRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.UserConnectionResponse;
import com.example.splitly.presentation.dto.response.UserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final IUserService userService;
    private final IUserConnection userConnectionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<?> getAllUsers() {
        List<UserResponse> users = userService.getAll();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all users successfully", users);
    }

    @GetMapping("/{userId}")
    public ResponseData<?> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.getUserById(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user successfully", user);
    }

    @GetMapping("/by-email")
    public ResponseData<?> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user successfully", user);
    }

    @GetMapping("/check-email")
    public ResponseData<?> checkExistsedEmail(@RequestBody String email) {
        return new ResponseData<>(HttpStatus.OK.value(), "Check email successfully", userService.checkEmail(email));
    }

    @GetMapping("/my-info")
    public ResponseData<?> getMyInfo() {
        UserResponse response = userService.getMyInfo();
        return new ResponseData<>(HttpStatus.OK.value(), "Get your info successfully", response);
    }

    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserRequest request) {
        UserResponse updatedUser = userService.updateUser(userId, request);
        return new ResponseData<>(HttpStatus.OK.value(), "User updated", updatedUser);
    }

    @PatchMapping("/invitation/{groupId}")
    public ResponseData<?> invitationUserAccept(@PathVariable Long groupId,
            @RequestParam boolean action) {
        userService.handleInvitation(groupId, action);
        return new ResponseData<>(HttpStatus.OK.value(), "Handle Successfully");
    }

    // ============= User Connection Endpoints =============

    /**
     * Send a connection request to another user
     * Returns the user info of the person you're connecting with
     */
    @PostMapping("/connections")
    public ResponseData<?> createConnection(@Valid @RequestBody UserConnectionRequest request) {
        UserConnectionResponse connection = userConnectionService.createConnection(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Connection request sent successfully", connection);
    }

    /**
     * Get all users connected with current user (both accepted and pending)
     * Returns list of users with connection timestamps
     */
    @GetMapping("/connections")
    public ResponseData<?> getAllMyConnections() {
        List<UserConnectionResponse> connections = userConnectionService.getAllConnections();
        return new ResponseData<>(HttpStatus.OK.value(), "Retrieved all your connections", connections);
    }

    /**
     * Get all users with accepted connections
     * Returns list of connected users (your friends/contacts)
     */
    @GetMapping("/connections/accepted")
    public ResponseData<?> getAcceptedConnections() {
        List<UserConnectionResponse> connections = userConnectionService.getConnectionsByStatus(true);
        return new ResponseData<>(HttpStatus.OK.value(), "Retrieved accepted connections", connections);
    }

    /**
     * Get all users with pending connections (both sent and received)
     * Returns list of users with pending connection status
     */
    @GetMapping("/connections/pending")
    public ResponseData<?> getPendingConnections() {
        List<UserConnectionResponse> connections = userConnectionService.getConnectionsByStatus(false);
        return new ResponseData<>(HttpStatus.OK.value(), "Retrieved pending connections", connections);
    }

    /**
     * Get connection requests you received from other users
     * Returns list of users who sent you connection requests
     */
    @GetMapping("/connections/requests/received")
    public ResponseData<?> getPendingReceivedRequests() {
        List<UserConnectionResponse> connections = userConnectionService.getPendingReceivedRequests();
        return new ResponseData<>(HttpStatus.OK.value(), "Retrieved received connection requests", connections);
    }

    /**
     * Get connection requests you sent to other users
     * Returns list of users you sent connection requests to
     */
    @GetMapping("/connections/requests/sent")
    public ResponseData<?> getPendingSentRequests() {
        List<UserConnectionResponse> connections = userConnectionService.getPendingSentRequests();
        return new ResponseData<>(HttpStatus.OK.value(), "Retrieved sent connection requests", connections);
    }

    /**
     * Accept a connection request
     * Returns the user info who sent the request
     */
    @PatchMapping("/connections/accept")
    public ResponseData<?> acceptConnection(
            @RequestParam Integer requestUserId) {
        UserConnectionResponse connection = userConnectionService.acceptConnection(requestUserId);
        return new ResponseData<>(HttpStatus.OK.value(), "Connection accepted", connection);
    }

    /**
     * Reject a connection request
     * Returns the user info who sent the request
     */
    @PatchMapping("/connections/reject")
    public ResponseData<?> rejectConnection(
            @RequestParam Integer requestUserId) {
        UserConnectionResponse connection = userConnectionService.changeConnectionStatus(requestUserId, false);
        return new ResponseData<>(HttpStatus.OK.value(), "Connection rejected", connection);
    }

    /**
     * Remove a connection (accepted or pending)
     */
    @DeleteMapping("/connections")
    public ResponseData<?> deleteConnection(
            @RequestParam Integer userId) {
        userConnectionService.deleteConnection(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Connection removed successfully");
    }

    @GetMapping("/search")
    public ResponseData<?> searchUsersByEmail(@RequestParam String email) {
        List<UserResponse> users = userService.searchUsersByEmail(email);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Found " + users.size() + " users matching keyword",
                users);
    }

    @GetMapping("/search/all")
    public ResponseData<?> searchUsers(@RequestParam String keyword) {
        List<UserResponse> users = ((UserService) userService).searchUsers(keyword);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Found " + users.size() + " users matching keyword",
                users);
    }

    @PostMapping({ "/upload-avatar/{folderName}" })
    public ResponseEntity<ResponseData<?>> uploadAvatarUser(
            @PathVariable String folderName,
            @RequestParam("file") MultipartFile file) {
        String url = userService.uploadUserAvatar(file, folderName);
        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), "Upload successfully!", url);
        return ResponseEntity.ok(responseData);
    }
}
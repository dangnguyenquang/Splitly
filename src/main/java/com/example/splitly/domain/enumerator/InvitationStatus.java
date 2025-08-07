package com.example.splitly.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum InvitationStatus {
    @JsonProperty("success")
    SUCCESS,

    @JsonProperty("waiting")
    WAITING,

    @JsonProperty("failed")
    FAILED,
}

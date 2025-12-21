package com.example.splitly.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventStatus {
    @JsonProperty("success")
    SUCCESS,

    @JsonProperty("pending")
    PENDING,

    @JsonProperty("failed")
    FAILED,
}

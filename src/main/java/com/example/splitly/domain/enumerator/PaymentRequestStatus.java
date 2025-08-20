package com.example.splitly.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentRequestStatus {
    @JsonProperty("waiting")
    WAITING,
    @JsonProperty("failed")
    FAILED,
    @JsonProperty("processing")
    PROCESSING,
    @JsonProperty("success")
    SUCCESS,
    @JsonProperty("awaiting_confirmation")
    AWAITING_CONFIRMATION,
    @JsonProperty("ready_to_split")
    READY_TO_SPLIT,
}

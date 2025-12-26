package com.example.splitly.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {

    GROUP_INVITATION("group_invitation"),
    PAYMENT("payment"),
    CONNECTION_ACCEPTED("connection_accepted"),
    CONNECTION_REJECTED("connection_rejected"),
    CONNECTION_REQUEST("connection_request"),
    PAYMENT_REQUEST_CREATED("payment_request_created"),
    PAYMENT_REQUEST_UPDATED("payment_request_updated"),
    PAYMENT_CONSENSUS_REQUIRED("payment_consensus_required"),
    PAYMENT_CONSENSUS_ACCEPTED("payment_consensus_accepted"),
    PAYMENT_CONSENSUS_REJECTED("payment_consensus_rejected"),
    PAYMENT_READY_TO_SPLIT("payment_ready_to_split"),
    PAYMENT_SPLIT_SUCCESS("payment_split_success"),
    PAYMENT_FAILED("payment_failed"),
    PAYMENT_DEBT_CREATED("payment_debt_created");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationType fromValue(String value) {
        for (NotificationType t : values()) {
            if (t.value.equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}

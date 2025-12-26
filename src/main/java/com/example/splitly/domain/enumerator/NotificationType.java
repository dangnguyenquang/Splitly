package com.example.splitly.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {

    GROUP_INVITATION("group_invitation"),
    PAYMENT("payment"),
    CONNECTION_ACCEPTED("connection_accepted"),
    CONNECTION_REJECTED("connection_rejected"),
    CONNECTION_REQUEST("connection_request");

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

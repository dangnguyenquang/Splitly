package com.example.splitly.presentation.dto.request;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class NotificationMessageRequest {
    private String type;
    private String title;
    private String body;
    private Map<String, String> data;
}

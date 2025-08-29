package com.example.splitly.presentation.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    private GroupDTO groupDTO;
    private List<String> emailList;
}

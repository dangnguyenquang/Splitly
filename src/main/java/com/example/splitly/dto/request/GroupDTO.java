package com.example.splitly.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class GroupDTO implements Serializable{
    private Integer numberOfMember;
    private String groupName;
}

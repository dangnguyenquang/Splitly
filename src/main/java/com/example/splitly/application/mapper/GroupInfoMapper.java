package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupInfoMapper {
    GroupInfo toGroupInfo(GroupDTO groupDTO);

    @Mapping(source = "user", target = "leader")
    GroupInfoResponse toGroupInfoResponse(GroupInfo groupInfo);
    
    @Mapping(source = "user", target = "leader")
    List<GroupInfoResponse> toGroupInfoResponses(List<GroupInfo> groupInfos);
}

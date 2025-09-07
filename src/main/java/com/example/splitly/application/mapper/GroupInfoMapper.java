package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.GroupInfo;
import com.example.splitly.presentation.dto.request.GroupDTO;
import com.example.splitly.presentation.dto.response.GroupInfoResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupInfoMapper {
    GroupInfo toGroupInfo(GroupDTO groupDTO);

    GroupInfoResponse toGroupInfoResponse(GroupInfo groupInfo);

    List<GroupInfoResponse> toGroupInfoResponses(List<GroupInfo> groupInfos);
}

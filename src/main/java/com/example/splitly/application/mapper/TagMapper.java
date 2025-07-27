package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Role;
import com.example.splitly.domain.entity.Tag;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.request.TagRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import com.example.splitly.presentation.dto.response.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toTag(TagRequest tagRequest);

    TagResponse toTagResponse(Tag tag);
}

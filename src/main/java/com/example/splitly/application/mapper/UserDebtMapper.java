package com.example.splitly.application.mapper;

import java.util.List;

import com.example.splitly.application.mapper.qualifier.WithoutRoleMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.presentation.dto.response.UserDebtResponse;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserDebtMapper {

    @Mapping(
            source = "debtor",
            target = "debtor",
            qualifiedBy = WithoutRoleMapping.class
    )
    @Mapping(
            source = "creditor",
            target = "creditor",
            qualifiedBy = WithoutRoleMapping.class
    )
    UserDebtResponse toResponse(UserDebt userDebt);

    List<UserDebtResponse> toResponses(List<UserDebt> userDebts);
}


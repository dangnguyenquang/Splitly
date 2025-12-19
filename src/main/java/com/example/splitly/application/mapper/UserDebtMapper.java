package com.example.splitly.application.mapper;

import java.util.List;

import com.example.splitly.application.mapper.qualifier.WithoutRoleMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.presentation.dto.response.UserDebtResponse;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserDebtMapper {
    List<UserDebtResponse> toUserDebtResponse(List<UserDebt> userDebts);

    @Mapping(source = "creditor", target = "userInformation",
            qualifiedBy = WithoutRoleMapping.class)
    UserDebtResponse toUserDebtResponse(UserDebt userDebt);
}

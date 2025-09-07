package com.example.splitly.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.splitly.domain.entity.UserDebt;
import com.example.splitly.presentation.dto.response.UserDebtResponse;

@Mapper(componentModel = "spring")
public interface UserDebtMapper {
    @Mapping(source = "debtor.fullName", target = "fullName")
    @Mapping(source = "debtor.userId", target = "debtorId")
    List<UserDebtResponse> toUserDebtResponse(List<UserDebt> userDebts);

    @Mapping(source = "debtor.fullName", target = "fullName")
    @Mapping(source = "debtor.userId", target = "debtorId")
    UserDebtResponse toUserDebtResponse(UserDebt userDebt);
}

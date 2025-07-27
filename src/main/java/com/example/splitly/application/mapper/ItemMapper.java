package com.example.splitly.application.mapper;

import com.example.splitly.domain.entity.Items;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Items toItems(ItemRequest itemRequest);

    ItemResponse toItemResponse(Items items);

    void updateItem(@MappingTarget Items existingItem, ItemRequest itemRequest);
}

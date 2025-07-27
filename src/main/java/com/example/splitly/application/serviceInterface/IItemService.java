package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;

import java.util.List;

public interface IItemService  {
    public ItemResponse create(ItemRequest itemRequest);

    public List<ItemResponse> getItemListByPaymentId(Integer paymentId);

    public ItemResponse update(Integer id, ItemRequest itemRequest);
}

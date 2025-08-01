package com.example.splitly.application.serviceInterface;

import com.example.splitly.domain.entity.Payment;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;

import java.util.List;
import java.util.Set;

public interface IItemService  {
    public ItemResponse create(ItemRequest itemRequest, Payment payment);

    public Set<ItemResponse> createAll(Set<ItemRequest> itemRequests, Payment payment);

    public Set<ItemResponse> getItemListByPaymentId(Integer paymentId);

    public ItemResponse update(Integer id, ItemRequest itemRequest);

    public Set<ItemResponse> updateItemsByPaymentId(Payment payment, Set<ItemRequest> itemRequests);
}

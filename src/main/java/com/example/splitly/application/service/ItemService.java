package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.User;
import com.example.splitly.domain.repository.ItemRepository;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService implements IItemService {
    public final ItemRepository itemRepository;
    public final ItemMapper itemMapper;

    @Override
    public ItemResponse create(ItemRequest itemRequest) {
        var item = itemMapper.toItems(itemRequest);
        if (item.getPaymentRequest() == null) {
           throw new IllegalArgumentException("Item must be associated with a payment.");
        }

        return itemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public List<ItemResponse> getItemListByPaymentId(Integer paymentId) {
        return itemRepository.findByPaymentRequestPaymentId(paymentId)
                .stream()
                .map(itemMapper::toItemResponse)
                .toList();
    }

    @Override
    public ItemResponse update(Integer id, ItemRequest itemRequest) {
        Items existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found in DB"));

        itemMapper.updateItem(existingItem, itemRequest);

        return itemMapper.toItemResponse(itemRepository.save(existingItem));
    }
}

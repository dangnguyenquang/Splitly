package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.repository.ItemRepository;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService implements IItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemResponse create(ItemRequest itemRequest, Payment payment) {
        var item = itemMapper.toItems(itemRequest);
        item.setPayment(payment);
        if (item.getPayment() == null) {
            throw new IllegalArgumentException("Item must be associated with a payment.");
        }

        return itemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public Set<ItemResponse> createAll(Set<ItemRequest> itemRequests, Payment payment) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException("Item request set cannot be null or empty.");
        }

        Set<Items> items = itemRequests.stream().map(itemRequest -> {
            Items item = itemMapper.toItems(itemRequest);
            item.setPayment(payment);

            if (item.getPayment() == null) {
                throw new IllegalArgumentException("Each item must be associated with a payment.");
            }

            return item;
        }).collect(Collectors.toSet());

        Set<Items> savedItems = new HashSet<>(itemRepository.saveAll(items));
        return savedItems.stream()
                .map(itemMapper::toItemResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ItemResponse> getItemListByPaymentId(Integer paymentId) {
        return itemRepository.findByPaymentPaymentId(paymentId)
                .stream()
                .map(itemMapper::toItemResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public ItemResponse update(Integer id, ItemRequest itemRequest) {
        Items existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found in DB"));

        itemMapper.updateItem(existingItem, itemRequest);

        return itemMapper.toItemResponse(itemRepository.save(existingItem));
    }

    @Override
    public Set<ItemResponse> updateItemsByPaymentId(Payment payment, Set<ItemRequest> itemRequests) {
        List<Items> oldItems = itemRepository.findByPaymentPaymentId(payment.getPaymentId());
        Map<Integer, Items> oldItemMap = oldItems.stream()
                .collect(Collectors.toMap(Items::getItemId, Function.identity()));

        List<Items> itemsToSave = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            Integer itemId = request.getItemId();

            if (itemId != null && oldItemMap.containsKey(itemId)) {
                Items existingItem = oldItemMap.get(itemId);
                itemMapper.updateItem(existingItem, request);
                existingItem.setPayment(payment);

                itemsToSave.add(existingItem);

                oldItemMap.remove(itemId);
            } else {
                Items newItem = itemMapper.toItems(request);
                newItem.setPayment(payment);

                itemsToSave.add(newItem);
            }
        }

        itemRepository.deleteAll(oldItemMap.values());

        List<Items> savedItems = itemRepository.saveAll(itemsToSave);

        return savedItems.stream().map(itemMapper::toItemResponse).collect(Collectors.toSet());
    }
}

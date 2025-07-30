package com.example.splitly.application.service;

import com.example.splitly.application.mapper.ItemMapper;
import com.example.splitly.application.serviceInterface.IItemService;
import com.example.splitly.application.serviceInterface.IPaymentRequestService;
import com.example.splitly.domain.entity.Items;
import com.example.splitly.domain.entity.Payment;
import com.example.splitly.domain.repository.ItemRepository;
import com.example.splitly.presentation.dto.request.ItemRequest;
import com.example.splitly.presentation.dto.response.ItemResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public List<ItemResponse> getItemListByPaymentId(Integer paymentId) {
        return itemRepository.findByPaymentPaymentId(paymentId)
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

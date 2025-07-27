package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Items, Integer> {
    List<Items> findByPaymentRequestPaymentId(Integer paymentId);
}

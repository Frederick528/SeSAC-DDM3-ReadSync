package com.ohgiraffers.backendapi.domain.order.repository;

import com.ohgiraffers.backendapi.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 주문 항목 엔티티를 관리하는 JPA 리포지토리입니다.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

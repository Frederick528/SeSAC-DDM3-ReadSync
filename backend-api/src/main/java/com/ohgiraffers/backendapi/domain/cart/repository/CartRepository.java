package com.ohgiraffers.backendapi.domain.cart.repository;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser_Id(Long userId);

    Optional<Cart> findByUser_IdAndBook_BookId(Long userId, Long bookId);

    void deleteByUser_Id(Long userId);
}

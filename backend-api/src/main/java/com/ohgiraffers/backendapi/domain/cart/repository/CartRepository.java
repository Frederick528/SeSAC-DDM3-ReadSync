package com.ohgiraffers.backendapi.domain.cart.repository;

import com.ohgiraffers.backendapi.domain.cart.entity.Cart;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장바구니 엔티티를 관리하는 JPA 리포지토리 인터페이스입니다.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 특정 유저와 도서의 장바구니 항목을 조회합니다.
     * 
     * @param user   사용자 정보
     * @param bookId 도서 ID
     * @return 장바구니 항목 (존재할 경우)
     */
    Optional<Cart> findByUserAndBook_BookId(User user, Long bookId);

    /**
     * 특정 유저의 모든 장바구니 항목을 조회합니다.
     * 
     * @param user 사용자 정보
     * @return 장바구니 항목 목록
     */
    List<Cart> findByUser(User user);

    /**
     * 특정 유저의 장바구니를 비웁니다.
     * 
     * @param user 사용자 정보
     */
    void deleteByUser(User user);
}

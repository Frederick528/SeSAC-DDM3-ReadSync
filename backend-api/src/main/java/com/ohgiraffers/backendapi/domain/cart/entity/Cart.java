package com.ohgiraffers.backendapi.domain.cart.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import com.ohgiraffers.backendapi.global.common.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "carts")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "장바구니 Entity")
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    @Schema(description = "장바구니 ID")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "사용자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Schema(description = "도서")
    private Book book;

    @Column(name = "quantity", nullable = false)
    @Schema(description = "수량")
    private int quantity;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}

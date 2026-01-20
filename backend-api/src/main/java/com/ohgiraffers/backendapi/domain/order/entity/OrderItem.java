package com.ohgiraffers.backendapi.domain.order.entity;

import com.ohgiraffers.backendapi.domain.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "주문 상세(아이템) Entity")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @Schema(description = "주문 상세 ID")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "주문")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @Schema(description = "도서")
    private Book book;

    @Column(name = "snapshot_price", nullable = false)
    @Schema(description = "구매 시점 가격")
    private BigDecimal snapshotPrice;

    @Column(name = "quantity", nullable = false)
    @Schema(description = "수량")
    private int quantity;

    @Column(name = "status", length = 20)
    @Schema(description = "아이템 상태")
    private String status;

    public void setOrder(Order order) {
        this.order = order;
    }
}

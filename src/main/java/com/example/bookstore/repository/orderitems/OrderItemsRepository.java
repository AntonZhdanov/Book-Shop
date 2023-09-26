package com.example.bookstore.repository.orderitems;

import com.example.bookstore.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId "
            + "AND oi.book.id = :bookId AND oi.order.user.id = :userId")
    Optional<OrderItem> findOrderItem(@Param("orderId") Long orderId,
                                      @Param("bookId") Long bookId, @Param("userId") Long userId);
}

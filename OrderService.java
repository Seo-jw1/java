package com.yourcompany.onlineshop.service;

import com.yourcompany.onlineshop.entity.Order;
import com.yourcompany.onlineshop.entity.OrderItem;
import com.yourcompany.onlineshop.entity.Product;
import com.yourcompany.onlineshop.entity.User;
import com.yourcompany.onlineshop.repository.OrderRepository;
import com.yourcompany.onlineshop.repository.ProductRepository;
import com.yourcompany.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order placeOrder(Long userId, Map<Long, Integer> productQuantities, String shippingAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Order.OrderStatus.PENDING); // 초기 상태

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getProductName());
            }

            // 재고 감소
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product); // 변경된 재고 저장

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtOrder(product.getPrice()); // 주문 당시 가격
            order.addOrderItem(orderItem); // OrderItem을 Order에 추가 (양방향 관계)

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return orderRepository.findByUser(user);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}

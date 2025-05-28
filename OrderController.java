package com.yourcompany.onlineshop.controller;

import com.yourcompany.onlineshop.entity.Order;
import com.yourcompany.onlineshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/my")
    public String getUserOrders(Model model) {
        // 실제로는 로그인된 사용자의 ID를 가져와야 합니다.
        // 예를 들어, Spring Security를 사용한다면:
        // Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        Long dummyUserId = 1L; // 임시 사용자 ID
        List<Order> userOrders = orderService.getUserOrders(dummyUserId);
        model.addAttribute("orders", userOrders);
        return "my_orders"; // my_orders.html
    }

    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        model.addAttribute("order", order);
        return "order_detail"; // order_detail.html
    }

    // 관리자용 주문 관리 페이지 (상태 변경 등) 추가 가능
}

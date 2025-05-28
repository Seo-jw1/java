package com.yourcompany.onlineshop.controller;

import com.yourcompany.onlineshop.entity.Category;
import com.yourcompany.onlineshop.entity.Product;
import com.yourcompany.onlineshop.service.CategoryService;
import com.yourcompany.onlineshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // CategoryService도 필요

    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
                               @RequestParam(required = false) Long categoryId,
                               Model model) {
        List<Product> products;
        if (search != null || categoryId != null) {
            products = productService.searchProducts(search, categoryId);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories()); // 카테고리 목록도 전달
        return "product_list"; // product_list.html
    }

    @GetMapping("/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);
        return "product_detail"; // product_detail.html
    }

    // --- 관리자 기능 (상품 등록/수정/삭제) ---
    @GetMapping("/admin/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product_form"; // admin/product_form.html (상품 등록/수정 공통)
    }

    @PostMapping("/admin")
    public String createProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.createProduct(product);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 등록되었습니다.");
        return "redirect:/products";
    }

    @GetMapping("/admin/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product_form";
    }

    @PostMapping("/admin/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.updateProduct(id, product);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 수정되었습니다.");
        return "redirect:/products";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 삭제되었습니다.");
        return "redirect:/products";
    }
}

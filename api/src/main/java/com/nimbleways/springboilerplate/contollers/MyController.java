package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.FlashSaleProduct;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {
    @Autowired
    private ProductService ps;

    @Autowired
    private ProductRepository pr;

    @Autowired
    private OrderRepository or;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = or.findById(orderId).get();
        System.out.println(order);
        List<Long> ids = new ArrayList<>();
        ids.add(orderId);
        Set<Product> products = order.getItems();
        for (Product p : products) {
            if (p.getType().equals("NORMAL")) {
                handleNormalProduct(p);
            } else if (p.getType().equals("SEASONAL")) {
                handleSeasonalProduct(p);
            } else if (p.getType().equals("EXPIRABLE")) {
                handleExpirableProduct(p);
            } else if (p.getType().equals("FLASHSALE")) {
                handleFlashSaleProduct((FlashSaleProduct) p);
            } else {
                throw new IllegalArgumentException("Unknown product type: " + p.getType());
            }
        }
        return new ProcessOrderResponse(order.getId());
    }


    private void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            pr.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                ps.notifyDelay(leadTime, product);
            }
        }
    }

    private void handleSeasonalProduct(Product product) {
        if (LocalDate.now().isAfter(product.getSeasonStartDate()) && LocalDate.now().isBefore(product.getSeasonEndDate()) && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            pr.save(product);
        } else {
            ps.handleSeasonalProduct(product);
        }
    }

    private void handleExpirableProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            pr.save(product);
        } else {
            ps.handleExpiredProduct(product);
        }
    }

    private void handleFlashSaleProduct(FlashSaleProduct flashSaleProduct) {
        if (LocalDate.now().isBefore(flashSaleProduct.getFlashSaleEndDate()) && flashSaleProduct.getAvailable() > 0) {
            flashSaleProduct.setAvailable(flashSaleProduct.getAvailable() - 1);
            pr.save(flashSaleProduct);
        } else {
            ps.handleFlashSaleEnd(flashSaleProduct);
        }
    }

}

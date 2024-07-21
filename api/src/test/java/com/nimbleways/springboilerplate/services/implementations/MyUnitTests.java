package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.FlashSaleProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }


    @Test
    public void testHandleFlashSaleWithinSalePeriod() {
        // GIVEN
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct(null, 15, 30, "FLASHSALE", "Gaming Laptop", null, null, null, LocalDate.now().plusDays(1), 50);

        Mockito.when(productRepository.save(flashSaleProduct)).thenReturn(flashSaleProduct);

        // WHEN
        // Simulate a purchase within the flash sale period
        if (LocalDate.now().isBefore(flashSaleProduct.getFlashSaleEndDate()) && flashSaleProduct.getAvailable() > 0) {
            flashSaleProduct.setAvailable(flashSaleProduct.getAvailable() - 1);
            productRepository.save(flashSaleProduct);
        }

        // THEN
        assertEquals(29, flashSaleProduct.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(flashSaleProduct);
    }
}
package com.nimbleways.springboilerplate.entities;

import lombok.*;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@DiscriminatorValue("FLASHSALE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleProduct extends Product {

    // Constructor with all parameters
    public FlashSaleProduct(Long id, Integer leadTime, Integer available, String type, String name, LocalDate expiryDate, LocalDate seasonStartDate, LocalDate seasonEndDate, LocalDate flashSaleEndDate, Integer maxQuantity) {
        super(id, leadTime, available, type, name, expiryDate, seasonStartDate, seasonEndDate);
        this.flashSaleEndDate = flashSaleEndDate;
        this.maxQuantity = maxQuantity;
    }
    @Column(name = "flash_sale_end_date")
    private LocalDate flashSaleEndDate;

    @Column(name = "max_quantity")
    private Integer maxQuantity;
}

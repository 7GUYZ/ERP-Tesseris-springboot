package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_category_index")
    private Integer storeCategoryIndex;

    @Column(name = "store_category_name", length = 30)
    private String storeCategoryName;
} 
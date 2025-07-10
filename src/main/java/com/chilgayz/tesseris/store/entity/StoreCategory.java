package com.chilgayz.tesseris.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Store_Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_category_index")
    private Integer storeCategoryIndex;

    @Column(name = "store_category_name")
    private String storeCategoryName;
}

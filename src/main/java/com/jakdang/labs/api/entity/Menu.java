package com.jakdang.labs.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @Column(name = "menu_index")
    private Integer manuIndex;

    @Column(name = "manu_name")
    private String manuName;

    @Column(name = "menu_value")
    private String menuValue;

    @Column(name = "menu_order")
    private Integer menuOrder;


}

package com.jakdang.labs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class adminType {
    @Id
    @Column(name = "admin_type_index")
    private Integer adminTypeIndex;

    @Column(name = "admin_type_name")
    private String adminTypeName;

    @Column(name = "admin_type_order")
    private Integer adminTypeOrder;




}

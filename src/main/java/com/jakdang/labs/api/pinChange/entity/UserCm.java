package com.jakdang.labs.api.pinChange.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User_Cm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCm {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_cm_index")
  private Integer userCmIndex;

  @Column(name = "user_cm_pincode")
  private String userCmPincode;
  
}

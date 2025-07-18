package com.jakdang.labs.api.post.model;

import lombok.Data;

@Data
public class MealPlanCreateDTO extends PostCreateDTO {
    private String mealDate;
    private String mealTime;
}

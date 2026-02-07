package com.example._PhamThanhDat.validators;

import com.example._PhamThanhDat.entities.Category;
import com.example._PhamThanhDat.validators.annotations.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryIdValidator implements
        ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category,
            ConstraintValidatorContext context) {
        return category != null && category.getId() != null;
    }
}
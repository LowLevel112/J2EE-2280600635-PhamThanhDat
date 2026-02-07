package com.example._PhamThanhDat.validators;

import com.example._PhamThanhDat.services.UserService;
import com.example._PhamThanhDat.validators.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

// XÓA @RequiredArgsConstructor đi
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Autowired // THÊM dòng này để Spring tự tiêm Service vào
    private UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (userService == null) {
            return true;
        }
        return userService.findByUsername(username).isEmpty();
    }
}
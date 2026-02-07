package com.example._PhamThanhDat.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

@Controller
public class ExceptionController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(status -> Integer.parseInt(status.toString()))
                .filter(status -> status == 403 || status == 404 || status == 500)
                .map(status -> "error/" + status)
                .orElse("error/500");
    }
}
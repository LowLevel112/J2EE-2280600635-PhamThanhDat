package com.example._PhamThanhDat.controllers;

import com.example._PhamThanhDat.entities.User;
import com.example._PhamThanhDat.repositories.IUserRepository;
import com.example._PhamThanhDat.services.EmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final EmailOtpService emailOtpService;
    private final IUserRepository userRepository;

    /**
     * Hiển thị trang cài đặt 2FA - Gửi OTP qua email
     */
    @GetMapping("/setup")
    public String setup(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        if (user.getTwoFaEnabled() != null && user.getTwoFaEnabled()) {
            return "redirect:/2fa/manage";
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            model.addAttribute("error", "Email không được cấu hình. Vui lòng cập nhật hồ sơ.");
            return "user/2fa-manage";
        }

        emailOtpService.generateAndSendOtp(user.getEmail(), "2FA_SETUP");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("maskedEmail", maskEmail(user.getEmail()));

        return "user/2fa-setup";
    }

    /**
     * Xác nhận OTP và kích hoạt 2FA
     */
    @PostMapping("/confirm")
    public String confirmSetup(
            Authentication authentication,
            @RequestParam String code,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        if (!emailOtpService.verifyOtp(user.getEmail(), code, "2FA_SETUP")) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            return "redirect:/2fa/setup";
        }

        user.setTwoFaEnabled(true);
        user.setTwoFaSecret("email-verified");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Đã kích hoạt 2FA!");
        return "redirect:/2fa/manage";
    }

    /**
     * Trang quản lý 2FA
     */
    @GetMapping("/manage")
    public String manage(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("twoFaEnabled", user.getTwoFaEnabled());
        model.addAttribute("email", user.getEmail());
        return "user/2fa-manage";
    }

    /**
     * Vô hiệu hóa 2FA
     */
    @PostMapping("/disable")
    public String disable(
            Authentication authentication,
            @RequestParam String code,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        if (user.getTwoFaEnabled() == null || !user.getTwoFaEnabled()) {
            return "redirect:/2fa/manage";
        }

        if (!emailOtpService.verifyOtp(user.getEmail(), code, "2FA_DISABLE")) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không hợp lệ.");
            return "redirect:/2fa/manage";
        }

        user.setTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Đã vô hiệu hóa 2FA");
        return "redirect:/2fa/manage";
    }

    /**
     * Yêu cầu OTP để vô hiệu hóa
     */
    @GetMapping("/request-otp-disable")
    public String requestOtpForDisable(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        if (user.getTwoFaEnabled() == null || !user.getTwoFaEnabled()) {
            return "redirect:/2fa/manage";
        }

        emailOtpService.generateAndSendOtp(user.getEmail(), "2FA_DISABLE");
        redirectAttributes.addFlashAttribute("info", "Mã OTP đã gửi đến email!");
        return "redirect:/2fa/manage";
    }

    /**
     * Trang xác minh 2FA khi đăng nhập
     */
    @GetMapping("/verify")
    public String verifyPage() {
        return "user/2fa-verify";
    }

    /**
     * Xác minh mã OTP khi đăng nhập
     */
    @PostMapping("/verify")
    public String verify(
            @RequestParam String code,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("error", "Phiên hết hạn. Vui lòng đăng nhập lại.");
        return "redirect:/login";
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 5) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email;
        }
        String localPart = email.substring(0, 2) + "*".repeat(atIndex - 2) + email.substring(atIndex);
        return localPart;
    }
}

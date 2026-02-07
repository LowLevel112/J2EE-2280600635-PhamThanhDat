package com.example._PhamThanhDat.services;

import com.example._PhamThanhDat.entities.Otp;
import com.example._PhamThanhDat.repositories.IOtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {

    private final IOtpRepository otpRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 10;

    /**
     * Tạo và gửi OTP qua email
     */
    public String generateAndSendOtp(String email, String type) {
        // Xóa OTP cũ đã sử dụng
        otpRepository.deleteByEmailAndIsUsedTrue(email);

        // Tạo OTP mới
        String code = generateOtpCode();

        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .type(type)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .isUsed(false)
                .build();

        otpRepository.save(otp);

        // Gửi email (log ra console cho testing)
        sendEmailWithOtp(email, code);

        return code; // Trả về code cho development/testing
    }

    /**
     * Xác minh OTP
     */
    public boolean verifyOtp(String email, String code, String type) {
        try {
            // Tìm OTP gần đây nhất
            Otp otp = otpRepository.findTop1ByEmailAndTypeOrderByCreatedAtDesc(email, type)
                    .orElse(null);

            if (otp == null) {
                log.warn("Không tìm thấy OTP cho email: {}", email);
                return false;
            }

            // Kiểm tra nếu OTP hợp lệ
            if (!otp.isValid()) {
                log.warn("OTP hết hạn hoặc đã sử dụng cho email: {}", email);
                return false;
            }

            // Kiểm tra code
            if (!otp.getCode().equals(code)) {
                log.warn("Mã OTP không đúng cho email: {}", email);
                return false;
            }

            // Đánh dấu OTP là đã sử dụng
            otp.setIsUsed(true);
            otpRepository.save(otp);

            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xác minh OTP", e);
            return false;
        }
    }

    /**
     * Tạo mã OTP ngẫu nhiên
     */
    private String generateOtpCode() {
        Random random = new Random();
        int code = random.nextInt(1000000);
        return String.format("%06d", code);
    }

    /**
     * Gửi email với OTP
     * Trong production, sử dụng JavaMailSender
     * Hiện tại chỉ log ra console
     */
    private void sendEmailWithOtp(String email, String code) {
        log.info("=".repeat(50));
        log.info("📧 GỬI OTP CHO EMAIL: {}", email);
        log.info("🔐 MÃ OTP: {}", code);
        log.info("⏱️  Thời hạn: {} phút", OTP_VALIDITY_MINUTES);
        log.info("=".repeat(50));

        // TODO: Tích hợp JavaMailSender để gửi email thực tế
        // EmailSender.send(new Email()
        // .to(email)
        // .subject("Mã xác minh 2FA - BookHaven")
        // .body("Mã xác minh của bạn: " + code)
        // );
    }
}

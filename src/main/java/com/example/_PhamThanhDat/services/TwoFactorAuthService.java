package com.example._PhamThanhDat.services;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class TwoFactorAuthService {
    private static final int SECRET_SIZE = 32;
    private static final String CRYPTO = "HmacSHA1";
    private static final Base32 base32 = new Base32();

    /**
     * Tạo một secret key mới cho 2FA
     */
    public String generateSecret() {
        Random random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        return base32.encodeToString(bytes).replaceAll("=+$", "");
    }

    /**
     * Tạo URL mã QR để quét
     */
    public String getQrCodeUrl(String secret, String email, String issuer) {
        return "otpauth://totp/" + issuer + ":" + email + "?secret=" + secret + "&issuer=" + issuer;
    }

    /**
     * Kiểm tra xem mã OTP có hợp lệ không
     */
    public boolean verifyCode(String secret, int code) {
        try {
            return verifyTOTP(secret, code);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Triển khai TOTP (Time-based One-Time Password) RFC 6238
     */
    private boolean verifyTOTP(String secret, int code) throws Exception {
        // Cho phép mã của time hiện tại và ±1 time window (30 giây mỗi cái)
        long timeCounter = System.currentTimeMillis() / 30000;

        for (int i = -1; i <= 1; i++) {
            long hash = generateTOTP(secret, timeCounter + i);
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tạo TOTP sử dụng HMAC-SHA1
     */
    private long generateTOTP(String secret, long timeCounter) throws Exception {
        byte[] decodedSecret = base32.decode(secret);

        byte[] msg = new byte[8];
        for (int i = 8; i-- > 0; timeCounter >>>= 8) {
            msg[i] = (byte) timeCounter;
        }

        Mac mac = Mac.getInstance(CRYPTO);
        mac.init(new SecretKeySpec(decodedSecret, CRYPTO));
        byte[] hash = mac.doFinal(msg);

        int offset = hash[hash.length - 1] & 0xf;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xff);
        }

        truncatedHash &= 0x7fffffff;
        truncatedHash %= 1000000;

        return truncatedHash;
    }

    /**
     * Tạo mã OTP để kiểm tra (mã hiện tại hợp lệ)
     */
    public int generateTestCode(String secret) {
        try {
            long timeCounter = System.currentTimeMillis() / 30000;
            return (int) generateTOTP(secret, timeCounter);
        } catch (Exception e) {
            return -1;
        }
    }
}

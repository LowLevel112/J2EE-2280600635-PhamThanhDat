package com.example._PhamThanhDat.repositories;

import com.example._PhamThanhDat.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IOtpRepository extends JpaRepository<Otp, Long> {
    /**
     * Tìm OTP gần đây nhất cho email
     */
    Optional<Otp> findTop1ByEmailAndTypeOrderByCreatedAtDesc(String email, String type);

    /**
     * Xóa tất cả OTP hết hạn cho email
     */
    void deleteByEmailAndIsUsedTrue(String email);
}

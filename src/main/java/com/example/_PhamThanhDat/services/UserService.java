package com.example._PhamThanhDat.services;

import com.example._PhamThanhDat.entities.User;
import com.example._PhamThanhDat.repositories.IRoleRepository;
import com.example._PhamThanhDat.repositories.IUserRepository;
import com.example._PhamThanhDat.constants.Provider;
import com.example._PhamThanhDat.constants.Role; // Thêm import Enum Role bài 7

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    // --- BỔ SUNG PHƯƠNG THỨC MẶC ĐỊNH CHO BÀI 7 ---
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            // Lấy quyền USER từ Enum Role (ID = 2)
            user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
            userRepository.save(user);
        });
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Trả về trực tiếp Entity User của bạn.
        // Spring Security sẽ tự hiểu và lấy đúng danh sách Roles/Authorities từ DB.
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public void saveOauthUser(String email, String username) {
        // 1. Dùng findByEmail để kiểm tra (Tránh lỗi truyền String vào findById)
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("Người dùng {} đã tồn tại trong Database, tiến hành đăng nhập.", email);
            return;
        }

        // 2. Nếu không tìm thấy Email, hệ thống mới tạo tài khoản mới
        var user = new User();
        // Đồng bộ username và email là 1 để dễ quản lý
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setProvider(Provider.GOOGLE.value);

        // Gán quyền USER (ID=2) cho tài khoản mới tự tạo
        var role = roleRepository.findRoleById(Role.USER.value);
        if (role != null) {
            user.getRoles().add(role);
        }

        userRepository.save(user);
        log.info("Đã tạo thành công tài khoản mới cho Google User: {}", email);
    }
}
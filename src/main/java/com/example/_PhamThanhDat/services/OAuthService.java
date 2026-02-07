package com.example._PhamThanhDat.services;

import com.example._PhamThanhDat.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService extends DefaultOAuth2UserService {

    private final IUserRepository userRepository; // Phải có khai báo này

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("-----> BẮT ĐẦU LUỒNG NAP QUYỀN OAUTH2 <-----");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        log.info("Email từ Google trả về: {}", email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("Tìm thấy User trong DB! Đang nạp danh sách quyền từ DB...");
                    var authorities = user.getAuthorities();
                    log.info("Quyền nạp từ DB cho {}: {}", email, authorities);

                    return (org.springframework.security.oauth2.core.user.OAuth2User) new DefaultOAuth2User(
                            authorities,
                            oAuth2User.getAttributes(),
                            "name");
                })
                .orElseGet(() -> {
                    log.warn("KHÔNG tìm thấy email {} trong Database! Đang dùng quyền mặc định Google.", email);
                    return oAuth2User;
                });
    }
}
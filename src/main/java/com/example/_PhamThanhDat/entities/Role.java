package com.example._PhamThanhDat.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must be less than 50 characters")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Size(max = 250, message = "Description must be less than 250 characters")
    @Column(name = "description", length = 250)
    private String description;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    // Định nghĩa các hằng số ID cho các quyền phổ biến (Tiện cho việc code Service)
    public static final Long ADMIN = 1L;
    public static final Long USER = 2L;

    @Override
    public String getAuthority() {
        return name; // Trả về tên quyền (VD: ADMIN, USER)
    }
}
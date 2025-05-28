package com.yourcompany.onlineshop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // 테이블명 지정 (자바 예약어 'User'와 충돌 방지)
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // 실제 앱에서는 BCrypt 등으로 암호화

    @Column(nullable = false, unique = true)
    private String email;

    private String address;
    private String phoneNumber;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private Role role; // enum으로 관리

    public enum Role {
        CUSTOMER, ADMIN
    }

    @PrePersist // 엔티티 저장 전에 실행
    public void prePersist() {
        this.registrationDate = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.CUSTOMER; // 기본 역할 설정
        }
    }
}

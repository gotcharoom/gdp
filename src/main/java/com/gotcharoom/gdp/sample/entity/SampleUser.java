package com.gotcharoom.gdp.sample.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity // 엔티티 클래스임을 선언.
@Table(name = "sample_user") // 해당 엔티티 클래스와 매핑될 데이터베이스 테이블 이름을 지정.
public class SampleUser {

    @Id // 엔티티 클래스의 주요 식별자(primary key)임을 선언
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 엔티티의 식별자 값을 자동으로 생성
    private Long id;

    // 해당 엔티티 클래스의 필드가 데이터베이스의 칼럼으로 매핑될 때,
    // 해당 칼럼의 제약 조건을 설정하는 어노테이션입니다. (널허용 = x 등)
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;
}
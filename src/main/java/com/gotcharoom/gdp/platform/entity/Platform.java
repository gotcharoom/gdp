package com.gotcharoom.gdp.platform.entity;

import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.model.PlatformUseYn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false, unique = true)
    private PlatformType type;

    @Column(name="url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name="use_yn", nullable = false)
    private PlatformUseYn useYn;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Platform updatePlatform(String name, String url, PlatformUseYn useYn) {
        return this.toBuilder()
                .name(name != null ? name : this.name)
                .url(url != null ? url : this.url)
                .useYn(useYn != null ? useYn : this.useYn)
                .build();
    }
}

package com.trippia.travel.domain.notification;

import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String content;

    private String relatedUrl;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isRead;

    private LocalDateTime createdAt;

    @Builder
    private Notification(User user, String content, NotificationType type, String relatedUrl, boolean isRead) {
        this.user = user;
        this.content = content;
        this.type = type;
        this.relatedUrl = relatedUrl;
        this.isRead = isRead;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead(){
        this.isRead = true;
    }
}

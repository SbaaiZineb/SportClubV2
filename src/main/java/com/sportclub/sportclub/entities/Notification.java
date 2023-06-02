package com.sportclub.sportclub.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean isRead;
    private String message;
    @JsonFormat(pattern = "YYYY-MM-DD HH:mm")
    private LocalDateTime timestamp;


    @ManyToMany
    @JoinColumn(name = "recipient_id")
        private List<UserApp> recipient;
}

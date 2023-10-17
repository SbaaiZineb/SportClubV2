package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor@NoArgsConstructor
public class Finger {
    @Lob
    @Column(length = 100000)
    private byte[] fPrint;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}

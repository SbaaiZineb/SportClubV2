package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Role {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long role_id;
    private String roleName;
    @OneToMany(mappedBy = "roles",fetch = FetchType.EAGER)
    private List<UserApp> user;
    public Role(Long role_id, String roleName) {
        this.role_id = role_id;
        this.roleName = roleName;
    }
}

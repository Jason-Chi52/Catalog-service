// Path: catalog-service-api/src/main/java/com/example/catalog_service/model/User.java

package com.example.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")  // avoid reserved word collisions in H2
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Unique username */
    @Column(unique = true, nullable = false)
    private String username;

    /** BCrypt‐hashed password */
    @Column(nullable = false)
    private String password;

    /**
     * Roles granted to this user.
     * Stored in a join table named `user_roles`.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
}

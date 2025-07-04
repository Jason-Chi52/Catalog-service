// Path: catalog-service-api/src/main/java/com/example/catalog_service/model/User.java

package com.example.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an application user.
 * Renamed to avoid using the reserved word "USER" as a table name in H2.
 */
@Entity
@Table(name = "app_user")  // rename the table so H2 will create/drop it without errors
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

    /** BCrypt-hashed password */
    @Column(nullable = false)
    private String password;
}

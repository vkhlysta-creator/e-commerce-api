package org.example.ecommerceapi.model;

import jakarta.persistence.*;
import jdk.jfr.Name;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}

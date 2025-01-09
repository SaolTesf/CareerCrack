package com.careercrack.careercrack.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "username")
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @Column(name = "hashed_password")
    @NotNull
    private String hashedPassword;

    @Column(name = "Email")
    private String email;

    @Column(name = "first_name")
    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
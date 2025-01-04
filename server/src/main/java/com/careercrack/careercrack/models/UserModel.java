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
@Table(name = "User_table")

public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Username")
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @Column(name = "HashedPassword")
    @NotNull
    private String hashedPassword;

    @Column(name = "Email")
    private String email;

    @Column(name = "CreatedAt")
    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private Date updatedAt;

}

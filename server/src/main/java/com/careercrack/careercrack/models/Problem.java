package com.careercrack.careercrack.models;

import com.careercrack.careercrack.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problems", schema = "careercrack")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Long id;

    // A User can have many Problems
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    // A Category can have many Problems
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private ProblemCategory problemCategory;

    @Column(name = "title")
    @NotNull
    @Size(min = 5, max = 255)
    private String title;

    @Column(name = "external_link")
    private String externalLink;

    @Column(name = "difficulty")
    @Size(max = 10)
    private String difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Size(min = 4, max = 20)
    private Status status;

    @Column(name = "description")
    private String description;

    @Column(name = "solution")
    private String solution;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Many Problems can have many Tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "problem_tags",
            schema = "careercrack",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    // One problem can have many resources
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private List<ProblemResource> problemResources;

}

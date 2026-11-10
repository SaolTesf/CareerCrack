package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Every lookup takes the user's id, so one user can never read or change another user's companies. */
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByIdAndUser_Id(Long id, Long userId);

    // Case-insensitive like the unique index on (user_id, lower(name)).
    // Use it for find-or-create, and to check for a duplicate name when renaming.
    @Query("select c from Company c where c.user.id = :userId and lower(c.name) = lower(:name)")
    Optional<Company> findForUserByName(@Param("userId") Long userId, @Param("name") String name);

    List<Company> findAllByUser_IdOrderByNameAsc(Long userId);

    Page<Company> findAllByUser_Id(Long userId, Pageable pageable);

    // autocomplete for the company field; Spring Data escapes any % or _ the user types
    List<Company> findTop10ByUser_IdAndNameContainingIgnoreCaseOrderByNameAsc(Long userId, String fragment);
}

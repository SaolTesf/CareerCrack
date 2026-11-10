package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.ImportJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Every lookup takes the user's id. To undo an import, call JobApplicationRepository.deleteAllByImportJob.
 */
public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {

    Optional<ImportJob> findByIdAndUser_Id(Long id, Long userId);

    // newest first
    Page<ImportJob> findAllByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

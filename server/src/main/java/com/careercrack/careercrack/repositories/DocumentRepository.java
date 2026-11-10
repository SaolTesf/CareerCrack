package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.enums.DocumentType;
import com.careercrack.careercrack.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Every lookup takes the user's id, so one user can never read or change another user's documents. */
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByIdAndUser_Id(Long id, Long userId);

    List<Document> findAllByUser_IdOrderByDocTypeAscNameAscVersionDesc(Long userId);

    // for a picker that shows only resumes, or only cover letters
    List<Document> findAllByUser_IdAndDocTypeOrderByNameAscVersionDesc(Long userId, DocumentType docType);

    // When attaching documents to an application: load them in one query and compare the count.
    List<Document> findAllByIdInAndUser_Id(Collection<Long> ids, Long userId);

    boolean existsByUser_IdAndDocTypeAndNameAndVersion(Long userId, DocumentType docType, String name, Integer version);

    // Highest version saved under this name, or 0 if none. The next upload is this + 1.
    @Query("""
            select coalesce(max(d.version), 0) from Document d
            where d.user.id = :userId and d.docType = :docType and d.name = :name
            """)
    Integer findMaxVersion(@Param("userId") Long userId, @Param("docType") DocumentType docType, @Param("name") String name);

    // the versions attached to one application
    @Query("""
            select d from JobApplication a join a.documents d
            where a.id = :applicationId and a.user.id = :userId
            order by d.docType, d.name, d.version
            """)
    List<Document> findAllForApplication(@Param("applicationId") Long applicationId, @Param("userId") Long userId);
}

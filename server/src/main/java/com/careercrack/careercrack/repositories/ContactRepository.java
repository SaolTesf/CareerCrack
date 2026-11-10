package com.careercrack.careercrack.repositories;

import com.careercrack.careercrack.models.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Every lookup takes the user's id, so one user can never read or change another user's contacts.
 * The company comes back in the same query, because every contact response shows its company's name.
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @EntityGraph(attributePaths = "company")
    Optional<Contact> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = "company")
    Page<Contact> findAllByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "company")
    List<Contact> findAllByUser_IdAndCompany_IdOrderByNameAsc(Long userId, Long companyId);

    // When linking contacts to an application: load them all in one query, then check that
    // the number returned equals the number requested. A smaller result means one wasn't theirs.
    @EntityGraph(attributePaths = "company")
    List<Contact> findAllByIdInAndUser_Id(Collection<Long> ids, Long userId);

    // the contacts linked to one application
    @Query("""
            select c from JobApplication a join a.contacts c join fetch c.company
            where a.id = :applicationId and a.user.id = :userId
            order by c.name
            """)
    List<Contact> findAllForApplication(@Param("applicationId") Long applicationId, @Param("userId") Long userId);
}
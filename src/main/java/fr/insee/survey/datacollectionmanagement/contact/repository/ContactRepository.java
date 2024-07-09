package fr.insee.survey.datacollectionmanagement.contact.repository;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact, String>, JpaRepository<Contact, String> {

    @Override
    Page<Contact> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT *  FROM contact ORDER BY random() LIMIT 1")
    Contact findRandomContact();

    @Query(nativeQuery = true, value = "SELECT identifier FROM contact TABLESAMPLE system_rows(1)")
    String findRandomIdentifierContact();

    @Query(
            value = """ 
        SELECT
            *
        FROM
            contact c
        JOIN 
            address a
        ON 
            c.address_id = a.id
        WHERE
            (:param IS NULL OR UPPER(c.identifier) = UPPER(:param))
            OR
            (:param IS NULL OR UPPER(CONCAT(c.first_name, ' ', c.last_name)) LIKE UPPER(CONCAT('%', :param, '%')))
            OR
            (:param IS NULL OR UPPER(c.email) = UPPER(:param))
    """,
            nativeQuery = true
    )
    Page<Contact> findByParameter(String param, Pageable pageable);


}

package fr.insee.survey.datacollectionmanagement.contact.repository;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact, String>, JpaRepository<Contact, String> {

    Page<Contact> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT *  FROM contact ORDER BY random() LIMIT 1")
    Contact findRandomContact();

    @Query(nativeQuery = true, value = "SELECT identifier FROM contact TABLESAMPLE system_rows(1)")
    String findRandomIdentifierContact();

    @Query(
            value = """ 
                     select 
                        * 
                     from 
                        contact c  
                     where 
                        upper(c.first_name) ||  ' ' || upper(c.last_name) like %:name%                            
                    """,
            nativeQuery = true)
    Page<Contact> findByNameIgnoreCase(String name, Pageable pageable);

    Page<Contact> findByEmailIgnoreCase(String email, Pageable pageable);


}

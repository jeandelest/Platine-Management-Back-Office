package fr.insee.survey.datacollectionmanagement.query.repository;

import fr.insee.survey.datacollectionmanagement.query.domain.QuestioningInformations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InformationsRepository {

    private final JdbcTemplate jdbcTemplate;


    static final String GET_QUESTIONING_INFORMATIONS_INTERVIEWER = """
            select
                c.identifier,
            	c.gender,
            	c.first_name,
            	c.last_name,
            	c.email as email,
            	c.phone,
            	c.phone2,
            	c.usual_company_name,
            	a.*,
            	su.label,
            	su.identification_code ,
            	su.identification_name,
            	p.return_date,
            	s2.logo,
            	q.id as questioning_id,
                s2.id as source_id
            from
            	questioning q
            join questioning_accreditation qa on
            	q.id = qa.questioning_id
            join contact c on
            	c.identifier = qa.id_contact
            join address a on
            	c.address_id = a.id
            join survey_unit su on
            	su.id_su = q.survey_unit_id_su
            join partitioning p on p.id =q.id_partitioning 
            join campaign c2 on c2.id =p.campaign_id
            join survey s on s.id=c2.survey_id
            join "source" s2 on s2.id =s.source_id
            where 
            	q.id_partitioning = ?
                and q.survey_unit_id_su = ?
            	and qa.id_contact= ?
            """;

    static final String GET_QUESTIONING_INFORMATIONS_REVIEWER = """
            select
                c.identifier,
            	c.gender,
            	c.first_name,
            	c.last_name,
            	c.email as email,
            	c.phone,
            	c.phone2,
            	c.usual_company_name,
            	a.*,
            	su.label,
            	su.identification_code ,
            	su.identification_name,
            	p.return_date,
            	s2.logo,
            	q.id as questioning_id,
                s2.id as source_id
            from
            	questioning q
            join questioning_accreditation qa on
            	q.id = qa.questioning_id
            join contact c on
            	c.identifier = qa.id_contact
            join address a on
            	c.address_id = a.id
            join survey_unit su on
            	su.id_su = q.survey_unit_id_su
            join partitioning p on p.id =q.id_partitioning 
            join campaign c2 on c2.id =p.campaign_id
            join survey s on s.id=c2.survey_id
            join "source" s2 on s2.id =s.source_id
            where 
            	q.id_partitioning = ?
                and q.survey_unit_id_su = ?
            	and qa.is_main is true
            """;

    public QuestioningInformations findQuestioningInformationsInterviewer(String idCampaign, String idSu, String contactId) {

        return jdbcTemplate.queryForObject(GET_QUESTIONING_INFORMATIONS_INTERVIEWER, new BeanPropertyRowMapper<>(QuestioningInformations.class), idCampaign, idSu, contactId);

    }
    public QuestioningInformations findQuestioningInformationsReviewer(String idCampaign, String idSu) {

        return jdbcTemplate.queryForObject(GET_QUESTIONING_INFORMATIONS_REVIEWER, new BeanPropertyRowMapper<>(QuestioningInformations.class), idCampaign, idSu);

    }



}

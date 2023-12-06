package fr.insee.survey.datacollectionmanagement.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
class MyQuestioningsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckHabilitationService checkAccreditationService;

//    @Test
//    void myQuestionings() throws Exception {
//        String identifier = "CONT2";
//
//        MvcResult result = this.mockMvc.perform(get(Constants.API_MY_QUESTIONINGS_ID, identifier)).andDo(print())
//                .andExpect(status().isOk()).andReturn();
//        String json = result.getResponse().getContentAsString();
//        MyQuestioningDto[] myQuestionings = new ObjectMapper().readValue(json, MyQuestioningDto[].class);
//        System.out.println(json);
//        assertEquals(10, myQuestionings.length);
//        Stream.of(myQuestionings).forEach(q -> {
//            assertFalse(q.isMandatoryMySurveys());
//            assertNotNull(q.getClosingDate());
//            assertNotNull(q.getOpeningDate());
//            assertNotNull(q.getReturnDate());
//            assertNotNull(q.getIdentificationCode());
//        });
//
//    }

    @Test
    void myQuestioningsContactNotExist() throws Exception {
        String identifier = "CONT500";

        MvcResult result = this.mockMvc.perform(get(Constants.API_MY_QUESTIONINGS_ID, identifier)).andDo(print())
                .andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        MyQuestioningDto[] myQuestionings = new ObjectMapper().readValue(json, MyQuestioningDto[].class);
        System.out.println(json);
        assertEquals(0, myQuestionings.length);

    }

}

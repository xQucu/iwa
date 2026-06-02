package com.example.demo;

import com.example.demo.message.request.LoginForm;
import com.example.demo.message.request.SignUpForm;
import com.example.demo.message.request.GradeRequest;
import com.example.demo.models.Subject;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class GradebookIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        gradeRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String getJwtToken(String username, String password) throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);

        MvcResult result = mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        java.util.Map<String, Object> map = objectMapper.readValue(responseString, java.util.Map.class);
        return (String) map.get("accessToken");
    }

    @Test
    public void testGradebookFlow() throws Exception {
        // 1. Sign up Teacher
        SignUpForm teacherSignUp = new SignUpForm();
        teacherSignUp.setUsername("teacher1");
        teacherSignUp.setPassword("password123");
        teacherSignUp.setRole(Set.of("teacher"));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherSignUp)))
                .andExpect(status().isOk());

        // 2. Sign up Student
        SignUpForm studentSignUp = new SignUpForm();
        studentSignUp.setUsername("student1");
        studentSignUp.setPassword("password123");
        studentSignUp.setRole(Set.of("student"));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentSignUp)))
                .andExpect(status().isOk());

        // 3. Obtain tokens
        String teacherToken = getJwtToken("teacher1", "password123");
        String studentToken = getJwtToken("student1", "password123");

        // 4. Teacher adds a subject
        Subject math = new Subject("Mathematics");
        String mathJson = mockMvc.perform(post("/subjects")
                .header("Authorization", "Bearer " + teacherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(math)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Subject savedMath = objectMapper.readValue(mathJson, Subject.class);

        // 5. Student tries to add a subject (should be FORBIDDEN - 403)
        Subject history = new Subject("History");
        mockMvc.perform(post("/subjects")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(history)))
                .andExpect(status().isForbidden());

        // 6. Student lists subjects (should be allowed)
        mockMvc.perform(get("/subjects")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        // 7. Teacher grades student in Mathematics
        com.example.demo.models.User studentUser = userRepository.findByUsername("student1").orElseThrow();

        GradeRequest gradeRequest = new GradeRequest(
                studentUser.getId(),
                savedMath.getId(),
                5.0,
                "Excellent midterm paper"
        );

        mockMvc.perform(post("/grades")
                .header("Authorization", "Bearer " + teacherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isCreated());

        // 8. Student checks their own grades
        String studentGradesJson = mockMvc.perform(get("/grades")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(studentGradesJson.contains("Excellent midterm paper"));
        assertTrue(studentGradesJson.contains("Mathematics"));
        assertTrue(studentGradesJson.contains("5.0"));

        // 9. Student tries to post a grade (should be FORBIDDEN - 403)
        mockMvc.perform(post("/grades")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isForbidden());

        // 10. Teacher updates the subject's name from "Mathematics" to "Advanced Mathematics"
        savedMath.setName("Advanced Mathematics");
        mockMvc.perform(put("/subjects/" + savedMath.getId())
                .header("Authorization", "Bearer " + teacherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedMath)))
                .andExpect(status().isOk());

        // 11. Student tries to update the subject's name (should be FORBIDDEN - 403)
        mockMvc.perform(put("/subjects/" + savedMath.getId())
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedMath)))
                .andExpect(status().isForbidden());

        // 12. Teacher modifies the assigned grade (change value to 4.5 and description to "Correction after review")
        com.example.demo.models.Grade grade = gradeRepository.findAll().get(0);
        GradeRequest modifyRequest = new GradeRequest(
                studentUser.getId(),
                savedMath.getId(),
                4.5,
                "Correction after review"
        );
        mockMvc.perform(put("/grades/" + grade.getId())
                .header("Authorization", "Bearer " + teacherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
                .andExpect(status().isOk());

        // 13. Student tries to modify the grade (should be FORBIDDEN - 403)
        mockMvc.perform(put("/grades/" + grade.getId())
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
                .andExpect(status().isForbidden());

        // Verify the grade was updated for the student
        String studentGradesJsonAfterUpdate = mockMvc.perform(get("/grades")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(studentGradesJsonAfterUpdate.contains("Correction after review"));
        assertTrue(studentGradesJsonAfterUpdate.contains("Advanced Mathematics"));
        assertTrue(studentGradesJsonAfterUpdate.contains("4.5"));

        // 14. Teacher deletes the subject (should trigger programmatic cascade delete of grades)
        mockMvc.perform(delete("/subjects/" + savedMath.getId())
                .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isNoContent());

        // Verify grades list is now empty
        String studentGradesJsonAfterDelete = mockMvc.perform(get("/grades")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(studentGradesJsonAfterDelete.equals("[]"));
    }
}

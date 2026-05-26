package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private Student student;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setName("1º Desenvolvimento");

        student = new Student();
        student.setId(1L);
        student.setEmail("pedro@example.com");
        student.setName("Pedro Lucas");
        student.setGrade(grade);

        studentDTO = new StudentDTO();
        studentDTO.setId(1L);
        studentDTO.setEmail("pedro@example.com");
        studentDTO.setName("Pedro Lucas");
        studentDTO.setGradeId(1L);
        studentDTO.setGradeName("1º Desenvolvimento");
    }

    @Test
    @DisplayName("POST /api/students - ADMIN pode cadastrar aluno")
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(studentService.create(any(StudentDTO.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("pedro@example.com"))
                .andExpect(jsonPath("$.gradeId").value(1))
                .andExpect(jsonPath("$.gradeName").value("1º Desenvolvimento"));
    }

    @Test
    @DisplayName("POST /api/students - STUDENT não tem permissão (403)")
    @WithMockUser(roles = "STUDENT")
    void create_asStudent_returns403() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/students/{id} - retorna aluno encontrado")
    @WithMockUser(roles = "ADMIN")
    void findById_found() throws Exception {
        when(studentService.findById(1L)).thenReturn(student);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pedro Lucas"))
                .andExpect(jsonPath("$.gradeName").value("1º Desenvolvimento"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - retorna 404 quando não encontrado")
    @WithMockUser(roles = "ADMIN")
    void findById_notFound_returns404() throws Exception {
        when(studentService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Aluno não encontrado com id: 99"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aluno não encontrado com id: 99"));
    }

    @Test
    @DisplayName("GET /api/students - lista todos os alunos")
    @WithMockUser(roles = "TEACHER")
    void findAll_returnsList() throws Exception {
        when(studentService.findAll()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("pedro@example.com"));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - ADMIN remove aluno sem ocorrências")
    @WithMockUser(roles = "ADMIN")
    void delete_success() throws Exception {
        doNothing().when(studentService).delete(1L);

        mockMvc.perform(delete("/api/students/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - retorna 409 quando há ocorrências vinculadas")
    @WithMockUser(roles = "ADMIN")
    void delete_withLinkedOccurrences_returns409() throws Exception {
        doThrow(new BusinessException("Não é possível remover o aluno pois existem ocorrências vinculadas a ele."))
                .when(studentService).delete(1L);

        mockMvc.perform(delete("/api/students/1").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("ocorrências vinculadas")));
    }

    @Test
    @DisplayName("POST /api/students - email inválido retorna 400")
    @WithMockUser(roles = "ADMIN")
    void create_invalidEmail_returns400() throws Exception {
        StudentDTO invalid = new StudentDTO();
        invalid.setEmail("nao-e-email");
        invalid.setName("Pedro");
        invalid.setGradeId(1L);

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/students - gradeId ausente retorna 400")
    @WithMockUser(roles = "ADMIN")
    void create_missingGradeId_returns400() throws Exception {
        StudentDTO invalid = new StudentDTO();
        invalid.setEmail("pedro@example.com");
        invalid.setName("Pedro");
        // gradeId não informado (null)

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.gradeId").exists());
    }
}
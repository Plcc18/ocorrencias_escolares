package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.config.LoginRateLimitFilter;
import com.example.ocorrencias_escolares_api.config.SecurityConfig;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.security.CustomUserDetailsService;
import com.example.ocorrencias_escolares_api.security.JwtTokenProvider;
import com.example.ocorrencias_escolares_api.service.OccurrenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OccurrenceController.class)
@Import({SecurityConfig.class, LoginRateLimitFilter.class})
class OccurrenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OccurrenceService occurrenceService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private Occurrence occurrence;
    private OccurrenceDTO dto;

    @BeforeEach
    void setUp() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setName("1º Desenvolvimento");
        grade.setCourse("Desenvolvimento de Sistemas");
        grade.setShift(GradeShift.MANHA);

        Student student = new Student();
        student.setId(1L);
        student.setName("Pedro Lucas");
        student.setEmail("pedro@example.com");
        student.setEnrollment("2024001");
        student.setGrade(grade);
        student.setCourse("Desenvolvimento de Sistemas");
        student.setShift(GradeShift.MANHA);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Maria Oliveira");
        teacher.setEmail("maria@example.com");
        teacher.setSubject("Matemática");

        occurrence = new Occurrence();
        occurrence.setId(1L);
        occurrence.setDescription("Aluno chegou atrasado");
        occurrence.setOccurrenceDate(LocalDate.of(2025, 10, 21));
        occurrence.setOccurrenceType(OccurrenceType.DISCIPLINA);
        occurrence.setStudent(student);
        occurrence.setTeacher(teacher);

        dto = new OccurrenceDTO();
        dto.setDescription("Aluno chegou atrasado");
        dto.setOccurrenceDate(LocalDate.of(2025, 10, 21));
        dto.setOccurrenceType(OccurrenceType.DISCIPLINA);
        dto.setStudentId(1L);
        dto.setTeacherId(1L);
    }

    @Test
    @DisplayName("POST /api/occurrences - TEACHER cria ocorrência com sucesso")
    @WithMockUser(roles = "TEACHER")
    void create_asTeacher_returns201() throws Exception {
        when(occurrenceService.create(any(OccurrenceDTO.class))).thenReturn(occurrence);

        mockMvc.perform(post("/api/occurrences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Aluno chegou atrasado"))
                .andExpect(jsonPath("$.occurrenceType").value("DISCIPLINA"))
                .andExpect(jsonPath("$.studentName").value("Pedro Lucas"))
                .andExpect(jsonPath("$.teacherName").value("Maria Oliveira"));
    }

    @Test
    @DisplayName("POST /api/occurrences - STUDENT não tem permissão (403)")
    @WithMockUser(roles = "STUDENT")
    void create_asStudent_returns403() throws Exception {
        mockMvc.perform(post("/api/occurrences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/occurrences - descrição ausente retorna 400")
    @WithMockUser(roles = "ADMIN")
    void create_missingDescription_returns400() throws Exception {
        dto.setDescription(null);

        mockMvc.perform(post("/api/occurrences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    @DisplayName("GET /api/occurrences/{id} - retorna ocorrência por ID")
    @WithMockUser(roles = "TEACHER")
    void findById_found() throws Exception {
        when(occurrenceService.findById(1L)).thenReturn(occurrence);

        mockMvc.perform(get("/api/occurrences/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.teacherId").value(1));
    }

    @Test
    @DisplayName("GET /api/occurrences/{id} - retorna 404 para ID inexistente")
    @WithMockUser(roles = "ADMIN")
    void findById_notFound_returns404() throws Exception {
        when(occurrenceService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Ocorrência não encontrada com id: 99"));

        mockMvc.perform(get("/api/occurrences/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/occurrences - TEACHER lista com paginação")
    @WithMockUser(roles = "TEACHER")
    void findAll_returnsPaginatedList() throws Exception {
        when(occurrenceService.findWithFilters(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(occurrence)));

        mockMvc.perform(get("/api/occurrences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/occurrences/student/{id} - STUDENT pode ver as próprias ocorrências")
    @WithMockUser(roles = "STUDENT")
    void findByStudent_asStudent_returns200() throws Exception {
        when(occurrenceService.findWithFilters(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(occurrence)));

        mockMvc.perform(get("/api/occurrences/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/occurrences/{id} - ADMIN remove com sucesso")
    @WithMockUser(roles = "ADMIN")
    void delete_asAdmin_returns204() throws Exception {
        doNothing().when(occurrenceService).delete(1L);

        mockMvc.perform(delete("/api/occurrences/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/occurrences/{id} - TEACHER não tem permissão (403)")
    @WithMockUser(roles = "TEACHER")
    void delete_asTeacher_returns403() throws Exception {
        mockMvc.perform(delete("/api/occurrences/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}

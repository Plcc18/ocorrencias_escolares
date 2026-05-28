package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private OccurrenceRepository occurrenceRepository;

    @Mock
    private GradeService gradeService;

    @InjectMocks
    private StudentServiceImpl service;

    private Grade grade;
    private Student student;
    private StudentDTO dto;

    @BeforeEach
    void setUp() {
        grade = new Grade();
        grade.setId(1L);
        grade.setName("1º Desenvolvimento");
        grade.setCourse("Desenvolvimento de Sistemas");
        grade.setShift(GradeShift.MANHA);

        student = new Student();
        student.setId(1L);
        student.setEmail("pedro@example.com");
        student.setName("Pedro Lucas");
        student.setEnrollment("2024001");
        student.setGrade(grade);
        student.setCourse("Desenvolvimento de Sistemas");
        student.setShift(GradeShift.MANHA);

        dto = new StudentDTO();
        dto.setEmail("pedro@example.com");
        dto.setName("Pedro Lucas");
        dto.setEnrollment("2024001");
        dto.setGradeId(1L);
        dto.setCourse("Desenvolvimento de Sistemas");
        dto.setShift(GradeShift.MANHA);
    }

    @Test
    @DisplayName("create - sucesso quando email não existe")
    void create_success() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(gradeService.findById(1L)).thenReturn(grade);
        when(repository.save(any(Student.class))).thenReturn(student);

        Student result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getGrade().getName()).isEqualTo("1º Desenvolvimento");
        verify(repository).save(any(Student.class));
    }

    @Test
    @DisplayName("create - lança BusinessException quando email já existe")
    void create_duplicateEmail_throwsBusinessException() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create - lança ResourceNotFoundException quando grade não existe")
    void create_gradeNotFound_throwsException() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(gradeService.findById(1L))
                .thenThrow(new ResourceNotFoundException("Turma não encontrada com id: 1"));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("findById - retorna aluno quando encontrado")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(student));

        Student result = service.findById(1L);

        assertThat(result).isEqualTo(student);
    }

    @Test
    @DisplayName("findById - lança ResourceNotFoundException quando não encontrado")
    void findById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findAll - retorna lista de alunos")
    void findAll_returnsList() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findWithFilters(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(student), pageable, 1));

        var result = service.findAll(null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("pedro@example.com");
    }

    @Test
    @DisplayName("delete - executa quando aluno existe e sem ocorrências vinculadas")
    void delete_success() {
        when(repository.existsById(1L)).thenReturn(true);
        when(occurrenceRepository.existsByStudentId(1L)).thenReturn(false);

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - lança BusinessException quando aluno tem ocorrências vinculadas")
    void delete_withLinkedOccurrences_throwsBusinessException() {
        when(repository.existsById(1L)).thenReturn(true);
        when(occurrenceRepository.existsByStudentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ocorrências vinculadas");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete - lança ResourceNotFoundException quando aluno não existe")
    void delete_notFound_throwsException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}

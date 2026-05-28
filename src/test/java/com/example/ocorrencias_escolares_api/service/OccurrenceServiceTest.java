package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.OccurrenceFilterDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.service.impl.OccurrenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OccurrenceServiceTest {

    @Mock
    private OccurrenceRepository repository;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private OccurrenceServiceImpl service;

    private Student student;
    private Teacher teacher;
    private Occurrence occurrence;
    private OccurrenceDTO dto;
    private Grade grade;

    @BeforeEach
    void setUp() {
        grade = new Grade();
        grade.setId(1L);
        grade.setName("1º Ano");
        grade.setCourse("Ensino Médio");
        grade.setShift(GradeShift.MANHA);

        student = new Student();
        student.setId(1L);
        student.setName("Pedro Lucas");
        student.setEmail("pedro@example.com");
        student.setEnrollment("2024001");
        student.setGrade(grade);
        student.setCourse("Ensino Médio");
        student.setShift(GradeShift.MANHA);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Maria Oliveira");
        teacher.setEmail("maria@example.com");
        teacher.setSubject("Matemática");

        occurrence = new Occurrence();
        occurrence.setId(1L);
        occurrence.setDescription("Aluno chegou atrasado");
        occurrence.setOccurrenceDate(LocalDate.now());
        occurrence.setOccurrenceType(OccurrenceType.DISCIPLINA);
        occurrence.setStudent(student);
        occurrence.setTeacher(teacher);

        dto = new OccurrenceDTO();
        dto.setDescription("Aluno chegou atrasado");
        dto.setOccurrenceDate(LocalDate.now());
        dto.setOccurrenceType(OccurrenceType.DISCIPLINA);
        dto.setStudentId(1L);
        dto.setTeacherId(1L);
    }

    @Test
    @DisplayName("create - cria ocorrência com sucesso")
    void create_success() {
        when(studentService.findById(1L)).thenReturn(student);
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(repository.save(any(Occurrence.class))).thenReturn(occurrence);

        Occurrence result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getTeacher()).isEqualTo(teacher);
        assertThat(result.getOccurrenceType()).isEqualTo(OccurrenceType.DISCIPLINA);
    }

    @Test
    @DisplayName("create - lança ResourceNotFoundException quando aluno não existe")
    void create_studentNotFound_throwsException() {
        when(studentService.findById(1L))
                .thenThrow(new ResourceNotFoundException("Aluno não encontrado com id: 1"));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Aluno");
    }

    @Test
    @DisplayName("findById - retorna ocorrência existente")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(occurrence));

        Occurrence result = service.findById(1L);

        assertThat(result).isEqualTo(occurrence);
    }

    @Test
    @DisplayName("findById - lança exceção para id inexistente")
    void findById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findWithFilters - retorna página filtrada")
    void findWithFilters_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        OccurrenceFilterDTO filter = new OccurrenceFilterDTO();
        filter.setStudentId(1L);
        Page<Occurrence> page = new PageImpl<>(List.of(occurrence));

        when(repository.findWithFilters(1L, null, null, null, null, null, pageable)).thenReturn(page);

        Page<Occurrence> result = service.findWithFilters(filter, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("delete - remove ocorrência existente")
    void delete_success() {
        when(repository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - lança exceção para ocorrência inexistente")
    void delete_notFound_throwsException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}

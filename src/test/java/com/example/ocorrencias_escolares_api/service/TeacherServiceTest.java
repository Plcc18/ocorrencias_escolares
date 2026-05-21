package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.TeacherRepository;
import com.example.ocorrencias_escolares_api.service.impl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TeacherServiceImpl service;

    private Teacher teacher;
    private TeacherDTO dto;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setEmail("maria@example.com");
        teacher.setName("Maria Oliveira");
        teacher.setSubject("Matemática");

        dto = new TeacherDTO();
        dto.setEmail("maria@example.com");
        dto.setName("Maria Oliveira");
        dto.setSubject("Matemática");
    }

    @Test
    @DisplayName("create - sucesso quando email não existe")
    void create_success() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(modelMapper.map(dto, Teacher.class)).thenReturn(teacher);
        when(repository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @DisplayName("create - lança BusinessException quando email já existe")
    void create_duplicateEmail_throwsException() {
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("findById - retorna professor quando encontrado")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = service.findById(1L);

        assertThat(result).isEqualTo(teacher);
    }

    @Test
    @DisplayName("findById - lança ResourceNotFoundException quando não encontrado")
    void findById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findAll - retorna página paginada")
    void findAll_returnsPage() {
        var pageable = PageRequest.of(0, 10);
        Page<Teacher> page = new PageImpl<>(List.of(teacher));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<Teacher> result = service.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("delete - remove professor existente")
    void delete_success() {
        when(repository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - lança exceção para professor inexistente")
    void delete_notFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

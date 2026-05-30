package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.entity.TeacherSubject;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.enums.Role;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.TeacherRepository;
import com.example.ocorrencias_escolares_api.repository.TeacherSubjectRepository;
import com.example.ocorrencias_escolares_api.repository.UserRepository;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final TeacherSubjectRepository subjectRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public TeacherServiceImpl(TeacherRepository repository,
                              TeacherSubjectRepository subjectRepository,
                              OccurrenceRepository occurrenceRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              EntityManager entityManager) {
        this.repository = repository;
        this.subjectRepository = subjectRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Teacher create(TeacherDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado para outro professor!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário de login com este email!");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("Senha é obrigatória ao cadastrar um professor.");
        }
        if (dto.getPassword().length() < 6) {
            throw new BusinessException("A senha deve ter no mínimo 6 caracteres.");
        }
        if (dto.getPassword().length() > 255) {
            throw new BusinessException("A senha deve ter no máximo 255 caracteres.");
        }

        Teacher teacher = new Teacher();
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher = repository.save(teacher);

        setSubjects(teacher, dto.getSubjects());

        User user = new User();
        user.setUsername(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.TEACHER);
        userRepository.save(user);

        return teacher;
    }

    @Override
    @Transactional
    public Teacher update(Long id, TeacherDTO dto) {
        Teacher teacher = findById(id);

        repository.findByEmail(dto.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Email já cadastrado para outro professor!");
                });

        String oldEmail = teacher.getEmail();
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());

        // Atualiza disciplinas via coleção
        setSubjects(teacher, dto.getSubjects());

        Teacher saved = repository.saveAndFlush(teacher);

        userRepository.findByEmail(oldEmail).ifPresent(user -> {
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getName());
            userRepository.save(user);
        });

        return saved;
    }

    @Override
    @Transactional
    public void changePassword(Long teacherId, String newPassword) {
        Teacher teacher = findById(teacherId);

        User user = userRepository.findByEmail(teacher.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário de login não encontrado para o professor!" +
                                " (" + teacher.getEmail() + "). " +
                                "O professor pode ter sido cadastrado antes desta funcionalidade. " +
                                "Crie um usuário manualmente via POST /api/auth/register com role TEACHER e mesmo email."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher teacher = findById(id);

        if (occurrenceRepository.existsByTeacherId(id)) {
            throw new BusinessException(
                    "Não é possível remover o professor pois existem ocorrências vinculadas a ele. " +
                            "Remova as ocorrências antes de excluir o professor.");
        }

        userRepository.findByEmail(teacher.getEmail()).ifPresent(userRepository::delete);
        repository.deleteById(id);
    }

    private void setSubjects(Teacher teacher, List<String> subjects) {
        if (subjects == null) {
            teacher.getTeacherSubjects().clear();
            return;
        }

        List<String> normalized = subjects.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        // Remove disciplinas que não estão na nova lista
        teacher.getTeacherSubjects().removeIf(ts -> !normalized.contains(ts.getSubject()));

        // Obtém lista das que restaram
        List<String> existing = teacher.getTeacherSubjects().stream()
                .map(TeacherSubject::getSubject)
                .toList();

        // Adiciona apenas as novas
        for (String s : normalized) {
            if (!existing.contains(s)) {
                TeacherSubject ts = new TeacherSubject();
                ts.setTeacher(teacher);
                ts.setSubject(s);
                teacher.getTeacherSubjects().add(ts);
            }
        }
    }
}
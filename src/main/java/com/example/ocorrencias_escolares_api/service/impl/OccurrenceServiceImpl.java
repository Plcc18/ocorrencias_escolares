package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.service.OccurrenceService;
import com.example.ocorrencias_escolares_api.service.StudentService;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OccurrenceServiceImpl implements OccurrenceService {

    private final OccurrenceRepository repository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ModelMapper modelMapper;

    public OccurrenceServiceImpl(OccurrenceRepository repository, StudentService studentService, TeacherService teacherService, ModelMapper modelMapper) {
        this.repository = repository;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Occurrence create(OccurrenceDTO dto) {
        Occurrence occurrence = modelMapper.map(dto, Occurrence.class);
        Student student = studentService.findById(dto.getStudentId());
        Teacher teacher = teacherService.findById(dto.getTeacherId());
        occurrence.setStudent(student);
        occurrence.setTeacher(teacher);
        return repository .save(occurrence);
    }

    @Override
    public Occurrence update(Long id, OccurrenceDTO dto) {
        Occurrence occurrence = findById(id);
        modelMapper.map(dto, occurrence);
        if (dto.getStudentId() != null) {
            occurrence.setStudent(studentService.findById(dto.getStudentId()));
        }
        if (dto.getTeacherId() != null) {
            occurrence.setTeacher(teacherService.findById(dto.getTeacherId()));
        }
        return repository.save(occurrence);
    }

    @Override
    public Occurrence findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Occurrence not found"));
    }

    @Override
    public List<Occurrence> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

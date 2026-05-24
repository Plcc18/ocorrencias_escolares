package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de repositório usando @DataJpaTest com banco H2 em memória.
 * Valida as queries customizadas do OccurrenceRepository.
 *
 * Requisito: adicionar H2 como dependência de test no pom.xml:
 * <dependency>
 *     <groupId>com.h2database</groupId>
 *     <artifactId>h2</artifactId>
 *     <scope>test</scope>
 * </dependency>
 */
@DataJpaTest
class OccurrenceRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OccurrenceRepository repository;

    private Student student1;
    private Student student2;
    private Teacher teacher1;
    private Occurrence occ1;
    private Occurrence occ2;
    private Occurrence occ3;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setEmail("pedro@example.com");
        student1.setName("Pedro Lucas");
        student1.setGrade("1º Ano");
        em.persist(student1);

        student2 = new Student();
        student2.setEmail("ana@example.com");
        student2.setName("Ana Silva");
        student2.setGrade("2º Ano");
        em.persist(student2);

        teacher1 = new Teacher();
        teacher1.setEmail("maria@example.com");
        teacher1.setName("Maria Oliveira");
        teacher1.setSubject("Matemática");
        em.persist(teacher1);

        occ1 = new Occurrence();
        occ1.setDescription("Atraso");
        occ1.setOccurrenceDate(LocalDate.of(2025, 1, 10));
        occ1.setOccurrenceType(OccurrenceType.DISCIPLINA);
        occ1.setStudent(student1);
        occ1.setTeacher(teacher1);
        em.persist(occ1);

        occ2 = new Occurrence();
        occ2.setDescription("Falta injustificada");
        occ2.setOccurrenceDate(LocalDate.of(2025, 3, 15));
        occ2.setOccurrenceType(OccurrenceType.FALTA);
        occ2.setStudent(student1);
        occ2.setTeacher(teacher1);
        em.persist(occ2);

        occ3 = new Occurrence();
        occ3.setDescription("Participação exemplar");
        occ3.setOccurrenceDate(LocalDate.of(2025, 5, 20));
        occ3.setOccurrenceType(OccurrenceType.ELOGIO);
        occ3.setStudent(student2);
        occ3.setTeacher(teacher1);
        em.persist(occ3);

        em.flush();
    }

    @Test
    @DisplayName("findWithFilters - sem filtros retorna todas as ocorrências")
    void findWithFilters_noFilters_returnsAll() {
        Page<Occurrence> result = repository.findWithFilters(
                null, null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("findWithFilters - filtro por studentId retorna apenas ocorrências do aluno")
    void findWithFilters_byStudentId() {
        Page<Occurrence> result = repository.findWithFilters(
                student1.getId(), null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .allMatch(o -> o.getStudent().getId().equals(student1.getId()));
    }

    @Test
    @DisplayName("findWithFilters - filtro por occurrenceType retorna somente o tipo correto")
    void findWithFilters_byOccurrenceType() {
        Page<Occurrence> result = repository.findWithFilters(
                null, null, OccurrenceType.ELOGIO, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getOccurrenceType()).isEqualTo(OccurrenceType.ELOGIO);
    }

    @Test
    @DisplayName("findWithFilters - filtro por data inicial exclui registros anteriores")
    void findWithFilters_byStartDate() {
        Page<Occurrence> result = repository.findWithFilters(
                null, null, null,
                LocalDate.of(2025, 3, 1), null,
                PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2); // occ2 e occ3
        assertThat(result.getContent())
                .allMatch(o -> !o.getOccurrenceDate().isBefore(LocalDate.of(2025, 3, 1)));
    }

    @Test
    @DisplayName("findWithFilters - filtro por intervalo de datas")
    void findWithFilters_byDateRange() {
        Page<Occurrence> result = repository.findWithFilters(
                null, null, null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 31),
                PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2); // occ1 e occ2
    }

    @Test
    @DisplayName("findWithFilters - combinação de filtros studentId + tipo")
    void findWithFilters_studentIdAndType() {
        Page<Occurrence> result = repository.findWithFilters(
                student1.getId(), null, OccurrenceType.FALTA, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Falta injustificada");
    }

    @Test
    @DisplayName("existsByStudentId - retorna true quando há ocorrências para o aluno")
    void existsByStudentId_true() {
        assertThat(repository.existsByStudentId(student1.getId())).isTrue();
    }

    @Test
    @DisplayName("existsByStudentId - retorna false para aluno sem ocorrências")
    void existsByStudentId_false() {
        Student outro = new Student();
        outro.setEmail("outro@example.com");
        outro.setName("Outro Aluno");
        outro.setGrade("3º Ano");
        em.persist(outro);
        em.flush();

        assertThat(repository.existsByStudentId(outro.getId())).isFalse();
    }

    @Test
    @DisplayName("existsByTeacherId - retorna true quando há ocorrências para o professor")
    void existsByTeacherId_true() {
        assertThat(repository.existsByTeacherId(teacher1.getId())).isTrue();
    }
}
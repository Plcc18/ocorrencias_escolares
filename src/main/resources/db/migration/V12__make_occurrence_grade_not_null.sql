-- V12: Garante que o snapshot de turma da ocorrência seja obrigatório

-- Preenche registros antigos que ainda estejam sem snapshot com a turma atual do aluno.
UPDATE occurrences o
SET grade_id = s.grade_id
FROM students s
WHERE o.student_id = s.id
  AND o.grade_id IS NULL;

ALTER TABLE occurrences
    ALTER COLUMN grade_id SET NOT NULL;

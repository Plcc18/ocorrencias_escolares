-- Garante apenas uma matricula ativa por aluno.
-- A constraint antiga usava UNIQUE NULLS NOT DISTINCT (student_id, end_date),
-- o que tambem bloqueava historicos encerrados no mesmo dia.
ALTER TABLE enrollment_history
    DROP CONSTRAINT IF EXISTS uq_enrollment_active;

CREATE UNIQUE INDEX IF NOT EXISTS uq_enrollment_active
    ON enrollment_history(student_id)
    WHERE end_date IS NULL;

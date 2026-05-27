-- V5: Adiciona campos course e shift em grades; expande students com novos campos

-- grades: novos campos
ALTER TABLE grades
    ADD COLUMN IF NOT EXISTS course VARCHAR(100) NOT NULL DEFAULT 'Não informado',
    ADD COLUMN IF NOT EXISTS shift  VARCHAR(20)  NOT NULL DEFAULT 'MANHA';

ALTER TABLE grades
    ADD CONSTRAINT chk_grades_shift
    CHECK (shift IN ('MANHA', 'TARDE', 'NOITE', 'INTEGRAL'));

-- students: novos campos
ALTER TABLE students
    ADD COLUMN IF NOT EXISTS enrollment      VARCHAR(30)  UNIQUE,
    ADD COLUMN IF NOT EXISTS course          VARCHAR(100),
    ADD COLUMN IF NOT EXISTS shift           VARCHAR(20)  NOT NULL DEFAULT 'MANHA',
    ADD COLUMN IF NOT EXISTS status          VARCHAR(10)  NOT NULL DEFAULT 'ATIVO',
    ADD COLUMN IF NOT EXISTS birth_date      VARCHAR(10),
    ADD COLUMN IF NOT EXISTS guardian        VARCHAR(100),
    ADD COLUMN IF NOT EXISTS guardian_phone  VARCHAR(20),
    ADD COLUMN IF NOT EXISTS guardian_email  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS notes           TEXT;

ALTER TABLE students
    ADD CONSTRAINT chk_students_shift
    CHECK (shift IN ('MANHA', 'TARDE', 'NOITE', 'INTEGRAL'));

ALTER TABLE students
    ADD CONSTRAINT chk_students_status
    CHECK (status IN ('ATIVO', 'INATIVO'));

-- occurrences: novos tipos
ALTER TABLE occurrences DROP CONSTRAINT IF EXISTS chk_occurrences_type;
ALTER TABLE occurrences
    ADD CONSTRAINT chk_occurrences_type
    CHECK (occurrence_type IN (
        'DISCIPLINA', 'FALTA', 'ELOGIO', 'ADVERTENCIA',
        'SUSPENSAO', 'ATRASO', 'CELULAR', 'OUTRO'
    ));
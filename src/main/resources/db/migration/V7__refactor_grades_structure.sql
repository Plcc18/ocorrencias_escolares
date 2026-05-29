-- V7: Refatora tabela grades
-- Remove campo 'name' (string livre) e adiciona gradeLevel + schoolYear
-- Gera nome visual dinamicamente no backend

-- 1. Adicionar novos campos (nullable temporariamente para migração)
ALTER TABLE grades
    ADD COLUMN IF NOT EXISTS grade_level  SMALLINT,
    ADD COLUMN IF NOT EXISTS school_year  SMALLINT;

-- 2. Tentar extrair grade_level a partir do nome existente
--    Padrão esperado: "1º ...", "2º ...", "3º ..." → pega o primeiro dígito
UPDATE grades
SET grade_level = CASE
    WHEN name ~ '^[1-3]'
    THEN CAST(SUBSTRING(name FROM 1 FOR 1) AS SMALLINT)
    ELSE 1
END;

-- 3. Definir ano letivo atual para todos os registros existentes
UPDATE grades
SET school_year = EXTRACT(YEAR FROM NOW())::SMALLINT
WHERE school_year IS NULL;

-- 4. Tornar os campos NOT NULL
ALTER TABLE grades
    ALTER COLUMN grade_level SET NOT NULL,
    ALTER COLUMN school_year SET NOT NULL;

-- 5. Adicionar constraint de integridade
ALTER TABLE grades
    ADD CONSTRAINT chk_grades_level CHECK (grade_level IN (1, 2, 3));

ALTER TABLE grades
    ADD CONSTRAINT chk_grades_year CHECK (school_year >= 2000 AND school_year <= 2100);

-- 6. Unique constraint: um curso só pode ter uma turma por série/ano/turno
ALTER TABLE grades
    ADD CONSTRAINT uq_grades_course_level_year_shift
    UNIQUE (course_id, grade_level, school_year, shift);

-- 7. Remover o campo 'name' (agora substituído)
ALTER TABLE grades
    DROP COLUMN IF EXISTS name;
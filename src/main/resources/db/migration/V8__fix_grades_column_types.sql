-- V8: Corrige tipos das colunas grade_level e school_year
-- Hibernate espera INTEGER (int4), mas foram criadas como SMALLINT (int2) na V7

ALTER TABLE grades
    ALTER COLUMN grade_level TYPE INTEGER USING grade_level::INTEGER,
    ALTER COLUMN school_year TYPE INTEGER USING school_year::INTEGER;
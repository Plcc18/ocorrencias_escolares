-- V2: Ajusta coluna 'role' da tabela users e 'occurrence_type' da tabela occurrences
-- para compatibilidade com os enums Java (Role e OccurrenceType)

-- Garante que os valores existentes sejam válidos antes de adicionar constraint CHECK
ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(20);

ALTER TABLE occurrences
    ALTER COLUMN occurrence_type TYPE VARCHAR(50);

-- Adiciona constraint CHECK para a coluna role
ALTER TABLE users
    ADD CONSTRAINT chk_users_role
    CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT'));

-- Adiciona constraint CHECK para occurrence_type
ALTER TABLE occurrences
    ADD CONSTRAINT chk_occurrences_type
    CHECK (occurrence_type IN ('DISCIPLINA', 'FALTA', 'ELOGIO', 'ADVERTENCIA', 'SUSPENSAO', 'OUTRO'));

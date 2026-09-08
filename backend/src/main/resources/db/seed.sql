-- Dados iniciais para desenvolvimento do SAEP.
-- Execute este arquivo depois que o backend criar as tabelas.

BEGIN;

-- Administrador usado para testar o login.
INSERT INTO admin (email, senha)
SELECT 'admin@saep.com', 'admin123'
WHERE NOT EXISTS (
    SELECT 1 FROM admin WHERE email = 'admin@saep.com'
);

-- A atividade exige pelo menos cinco estufas de pintura.
INSERT INTO estufa (nome, ativa)
SELECT 'Estufa 01', TRUE
WHERE NOT EXISTS (SELECT 1 FROM estufa WHERE nome = 'Estufa 01');

INSERT INTO estufa (nome, ativa)
SELECT 'Estufa 02', TRUE
WHERE NOT EXISTS (SELECT 1 FROM estufa WHERE nome = 'Estufa 02');

INSERT INTO estufa (nome, ativa)
SELECT 'Estufa 03', TRUE
WHERE NOT EXISTS (SELECT 1 FROM estufa WHERE nome = 'Estufa 03');

INSERT INTO estufa (nome, ativa)
SELECT 'Estufa 04', TRUE
WHERE NOT EXISTS (SELECT 1 FROM estufa WHERE nome = 'Estufa 04');

INSERT INTO estufa (nome, ativa)
SELECT 'Estufa 05', TRUE
WHERE NOT EXISTS (SELECT 1 FROM estufa WHERE nome = 'Estufa 05');

-- Clientes de exemplo para testar os agendamentos.
INSERT INTO cliente (nome, cpf, telefone, email)
SELECT 'Maria da Silva', '11111111111', '(11) 99999-1111', 'maria@example.com'
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf = '11111111111');

INSERT INTO cliente (nome, cpf, telefone, email)
SELECT 'Joao Santos', '22222222222', '(11) 99999-2222', 'joao@example.com'
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE cpf = '22222222222');

COMMIT;

CREATE TABLE contato (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	codigo_pessoa BIGINT(20) NOT NULL,
	nome VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	telefone VARCHAR(20) NOT NULL,
	FOREIGN KEY (codigo_pessoa) REFERENCES pessoa(codigo)
) 	ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO contato (codigo_pessoa, nome, email, telefone) values ('1', 'Patricia Nunes', 'srtapatricianunes@gmail.com', '9-8736-1339');
CREATE DATABASE API_Mercado_Pago;

USE API_Mercado_Pago;

CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE productos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE transacciones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estado VARCHAR(50),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT,
    producto_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);


INSERT INTO usuarios (nombre, email) VALUES
('Santiago Lamot', 'santilamot@gmail.com'),
('Bautista Lamot', 'bauti.lamot@gmail.com'),
('Carlos Tévez', 'carlitos10@boca.com');


INSERT INTO productos (nombre, precio) VALUES
('Camiseta Boca Juniors 23/24', 28999.99),
('Mate con escudo de Boca', 3999.50),
('Bufanda Azul y Oro', 4500.00),
('Yerba Mate Playadito 1kg', 1800.00),
('Alfajores Havanna x6', 2500.00),
('Camiseta Selección Argentina 3 estrellas', 32000.00),
('Pulcera Boca Juniors', 1990.00),
('Pelota Adidas AFA', 8900.00);
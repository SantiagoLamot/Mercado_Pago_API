CREATE DATABASE API_Mercado_Pago;

USE API_Mercado_Pago;

CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    vendedor BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE productos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    reservado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_reserva DATETIME DEFAULT CURRENT_TIMESTAMP,
    vendido BOOLEAN NOT NULL DEFAULT FALSE
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

CREATE TABLE oauth_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500) NOT NULL,
    public_key VARCHAR(255),
    user_id BIGINT,-- ID del vendedor en MP
    expires_at DATETIME NOT NULL,
    live_mode BOOLEAN DEFAULT false,-- Si es modo producción o prueba
    
    usuario_id BIGINT NOT NULL,-- id de usuario de la aplicacion propia no mp
    CONSTRAINT fk_oauth_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

CREATE TABLE state_oauth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(255) NOT NULL,
    usuario_id BIGINT NOT NULL,
    creado DATETIME NOT NULL
);
INSERT INTO usuarios (nombre, email) VALUES
('Santiago Lamot', 'santilamot@gmail.com'),
('Bautista Lamot', 'bauti.lamot@gmail.com'),
('Carlos Tévez', 'carlitos10@boca.com');


INSERT INTO productos (nombre, precio) VALUES
('Camiseta Boca Juniors 23/24', 1.99),
('Mate con escudo de Boca', 3999.50),
('Bufanda Azul y Oro', 4500.00),
('Yerba Mate Playadito 1kg', 1800.00),
('Alfajores Havanna x6', 2500.00),
('Camiseta Selección Argentina 3 estrellas', 32000.00),
('Pulcera Boca Juniors', 1990.00),
('Pelota Adidas AFA', 8900.00);
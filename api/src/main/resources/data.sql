INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

/* Contraseña codificada es: 123456 */
INSERT INTO usuario( username, password)
    VALUES('admin', '$2a$10$qrdMD7vB.9DT.hoL0Wyate67.pbyDsWwozoY0cyNMXVbp5aixfvWe');

INSERT INTO user_roles(user_id, role_id)
    VALUES(
           (SELECT id FROM usuario WHERE username='admin'),
           (SELECT id FROM roles WHERE name='ROLE_ADMIN')
    );
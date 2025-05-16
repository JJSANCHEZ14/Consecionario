-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`persona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`persona` (
  `idpersona` INT NOT NULL,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido` VARCHAR(45) NOT NULL,
  `telefono` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  PRIMARY KEY (`idpersona`),
  UNIQUE INDEX `idpersona_UNIQUE` (`idpersona` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `telefono_UNIQUE` (`telefono` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`vendedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`vendedor` (
  `idvendedor` INT NOT NULL,
  `nombreUsuario` VARCHAR(45) NOT NULL,
  `clave` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idvendedor`),
  UNIQUE INDEX `nombreUsuario_UNIQUE` (`nombreUsuario` ASC) VISIBLE,
  CONSTRAINT `usuario`
    FOREIGN KEY (`idvendedor`)
    REFERENCES `mydb`.`persona` (`idpersona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`vehiculo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`vehiculo` (
  `placa` VARCHAR(10) NOT NULL,
  `marca` VARCHAR(45) NOT NULL,
  `modelo` VARCHAR(45) NOT NULL,
  `anio` INT NOT NULL,
  `tipo` VARCHAR(45) NOT NULL,
  `estado` INT NOT NULL, -- si es 0 esta vendido, 1 disponible y es nuevo, 2 disponible y es usado
  `detalle` VARCHAR(200) NULL,
  `costo` DECIMAL(10,2) NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`placa`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`propietario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`propietario` (
  `idcliente` INT NOT NULL,
  `vehiculo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idcliente`),
  INDEX `deuño_idx` (`vehiculo` ASC) VISIBLE,
  CONSTRAINT `idcliente`
    FOREIGN KEY (`idcliente`)
    REFERENCES `mydb`.`persona` (`idpersona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `deuño`
    FOREIGN KEY (`vehiculo`)
    REFERENCES `mydb`.`vehiculo` (`placa`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`almacen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`almacen` (
  `idvehiculo` VARCHAR(50) NOT NULL,
  `costo` INT NOT NULL,
  `precio` INT NOT NULL,
  PRIMARY KEY (`idvehiculo`),
  CONSTRAINT `compra`
    FOREIGN KEY (`idvehiculo`)
    REFERENCES `mydb`.`vehiculo` (`placa`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`factura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`factura` (
  `idfactura` INT NOT NULL AUTO_INCREMENT,
  `usuario` INT NOT NULL,
  `cliente` INT NOT NULL,
  `vehiculo` VARCHAR(45) NOT NULL,
  `precio` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `factura` VARCHAR(45),
  PRIMARY KEY (`idfactura`),
  INDEX `vendedor_idx` (`usuario` ASC) VISIBLE,
  INDEX `cliente_idx` (`cliente` ASC) VISIBLE,
  INDEX `vehiculo_idx` (`vehiculo` ASC) VISIBLE,
  CONSTRAINT `vendedor`
    FOREIGN KEY (`usuario`)
    REFERENCES `mydb`.`vendedor` (`idvendedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `cliente`
    FOREIGN KEY (`cliente`)
    REFERENCES `mydb`.`persona` (`idpersona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `vehiculo`
    FOREIGN KEY (`vehiculo`)
    REFERENCES `mydb`.`vehiculo` (`placa`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- PROCEDURE: sp_login_vendedor
DELIMITER $$
CREATE PROCEDURE sp_login_vendedor(
    IN p_usuario VARCHAR(100),
    IN p_clave VARCHAR(100)
)
BEGIN
    SELECT v.idvendedor, p.nombre, p.apellido, v.nombreUsuario
    FROM vendedor v
    INNER JOIN persona p ON v.idvendedor = p.idpersona
    WHERE v.nombreUsuario = p_usuario AND v.clave = p_clave;
END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_obtener_lista_clientes;
DELIMITER //
CREATE PROCEDURE sp_obtener_lista_clientes()
BEGIN
    SELECT idpersona, nombre, apellido, telefono, email
    FROM persona
    WHERE idpersona NOT IN (SELECT idvendedor FROM vendedor);
END; //
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_obtener_cliente_por_id;
DELIMITER //
CREATE PROCEDURE sp_obtener_cliente_por_id(IN p_idpersona INT)
BEGIN
    SELECT idpersona, nombre, apellido, telefono, email
    FROM persona
    WHERE idpersona = p_idpersona
      AND idpersona NOT IN (SELECT idvendedor FROM vendedor);
END; //
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_guardar_cliente;
DELIMITER //
CREATE PROCEDURE sp_guardar_cliente(
    IN p_idpersona INT,
    IN p_nombre VARCHAR(45),
    IN p_apellido VARCHAR(45),
    IN p_telefono VARCHAR(45),
    IN p_email VARCHAR(45)
)
BEGIN
    INSERT INTO persona (idpersona, nombre, apellido, telefono, email)
    VALUES (p_idpersona, p_nombre, p_apellido, p_telefono, p_email)
    ON DUPLICATE KEY UPDATE
        nombre = VALUES(nombre),
        apellido = VALUES(apellido),
        telefono = VALUES(telefono),
        email = VALUES(email);
END; //
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_actualizar_cliente;
DELIMITER //
CREATE PROCEDURE sp_actualizar_cliente(
    IN p_idpersona INT,
    IN p_nombre VARCHAR(45),
    IN p_apellido VARCHAR(45),
    IN p_telefono VARCHAR(45),
    IN p_email VARCHAR(45)
)
BEGIN
    UPDATE persona
    SET nombre = p_nombre,
        apellido = p_apellido,
        telefono = p_telefono,
        email = p_email
    WHERE idpersona = p_idpersona;
END; //
DELIMITER ;

-- Obtener vehículos actuales de un cliente (no vendidos)
DROP PROCEDURE IF EXISTS sp_obtener_vehiculos_por_cliente_id;
DELIMITER //
CREATE PROCEDURE sp_obtener_vehiculos_por_cliente_id(IN p_cliente_id INT)
BEGIN
    SELECT v.placa, v.marca, v.modelo, v.anio, v.precio
    FROM vehiculo v
    INNER JOIN propietario p ON v.placa = p.placa_vehiculo
    WHERE p.id_persona = p_cliente_id
      AND v.vendido = 0;
END //
DELIMITER ;

-- Registrar una nueva factura
USE mydb;

DROP PROCEDURE IF EXISTS sp_registrar_factura;
DELIMITER //
CREATE PROCEDURE sp_registrar_factura(
    IN p_usuario VARCHAR(45),
    IN p_idCliente INT,
    IN p_placa VARCHAR(20),
    IN p_precio DOUBLE,
    IN p_fecha DATE
)
BEGIN
    INSERT INTO factura (usuario, idcliente, placa, precio, fecha)
    VALUES (p_usuario, p_idCliente, p_placa, p_precio, p_fecha);
END //
DELIMITER ;

-- Buscar facturas por id de cliente
DROP PROCEDURE IF EXISTS sp_buscar_facturas_por_cliente;
DELIMITER //
CREATE PROCEDURE sp_buscar_facturas_por_cliente(IN p_idCliente INT)
BEGIN
    SELECT * FROM factura WHERE cliente = p_idCliente;
END //
DELIMITER ;

-- Buscar facturas por rango de fechas
DROP PROCEDURE IF EXISTS sp_buscar_facturas_por_fecha;
DELIMITER //
CREATE PROCEDURE sp_buscar_facturas_por_fecha(IN p_fechaInicio DATE, IN p_fechaFin DATE)
BEGIN
    SELECT * FROM factura WHERE fecha BETWEEN p_fechaInicio AND p_fechaFin;
END //
DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Login vendedor
USE mydb;

DROP PROCEDURE IF EXISTS sp_login_vendedor;
DELIMITER //
CREATE PROCEDURE sp_login_vendedor(IN p_usuario VARCHAR(45), IN p_clave VARCHAR(45))
BEGIN
    SELECT p.idpersona, p.nombre, p.apellido, p.email
    FROM persona p
    INNER JOIN vendedor v ON p.idpersona = v.idvendedor
    WHERE v.usuario = p_usuario
      AND v.clave = p_clave;
END //
DELIMITER ;

-- Corrige el JOIN para usar f.vehiculo en vez de f.placa
DROP PROCEDURE IF EXISTS sp_vehiculos_mas_vendidos;
DELIMITER //
CREATE PROCEDURE sp_vehiculos_mas_vendidos()
BEGIN
    SELECT v.marca, COUNT(*) AS cantidad_vendida
    FROM factura f
    JOIN vehiculo v ON f.vehiculo = v.placa
    GROUP BY v.marca
    ORDER BY cantidad_vendida DESC;
END //
DELIMITER ;
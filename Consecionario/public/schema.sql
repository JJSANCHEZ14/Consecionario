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
  `factura` VARCHAR(45) NOT NULL,
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

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

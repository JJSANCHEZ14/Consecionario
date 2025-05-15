package Controller;

import Coneccion.CRUD;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controllerConcesionario {
    private CRUD crud;

    public controllerConcesionario() {
        crud = new CRUD();
    }

    public boolean validarCampos(String marca, String modelo, String año, String placa) {
        if (marca.equals("Seleccione...") || modelo.isEmpty() ||
            año.isEmpty() || placa.isEmpty()) {
            return false;
        }
        try {
            int anioNum = Integer.parseInt(año);
            int anioActual = 2025;
            if (anioNum < 1900 || anioNum > anioActual) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        if (placa.length() < 6 || placa.length() > 7) {
            return false;
        }
        return true;
    }

    public String mensajeValidacion(String marca, String modelo, String año, String placa) {
        if (marca.equals("Seleccione...") || modelo.isEmpty() ||
            año.isEmpty() || placa.isEmpty()) {
            return "Por favor complete los campos: Marca, Modelo, Año y Placa";
        }
        try {
            int anioNum = Integer.parseInt(año);
            int anioActual = 2025;
            if (anioNum < 1900 || anioNum > anioActual) {
                return "El año debe estar entre 1900 y " + anioActual;
            }
        } catch (NumberFormatException e) {
            return "El año debe ser un número válido";
        }
        if (placa.length() < 6 || placa.length() > 7) {
            return "La placa debe tener entre 6 y 7 caracteres";
        }
        return "";
    }

    public ResultSet obtenerVehiculos() throws SQLException {
        return crud.obtenerVehiculos();
    }

    public boolean existePlaca(String placa) throws SQLException {
        return crud.existePlaca(placa);
    }

    public int insertarVehiculo(String placa, String marca, String modelo, int año, double costo, double precio, int estado, String detalles) throws SQLException {
        return crud.insertarVehiculo(placa, marca, modelo, año, costo, precio, estado, detalles);
    }

    public ResultSet obtenerVehiculoPorPlaca(String placa) throws SQLException {
        return crud.obtenerVehiculoPorPlaca(placa);
    }

    public int actualizarVehiculo(String placa, String marca, String modelo, int año, double costo, double precio, int estado, String detalles) throws SQLException {
        return crud.actualizarVehiculo(placa, marca, modelo, año, costo, precio, estado, detalles);
    }

    public String validarCostos(double costo, double precio) {
        if (costo >= precio) {
            return "El costo debe ser menor que el precio de venta";
        }
        if (costo <= 0 || precio <= 0) {
            return "El costo y precio deben ser mayores a 0";
        }
        return "";
    }

    public String validarPlacaBusqueda(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return "Por favor ingrese una placa para buscar";
        }
        if (placa.length() < 6 || placa.length() > 7) {
            return "La placa debe tener entre 6 y 7 caracteres";
        }
        return "";
    }
}

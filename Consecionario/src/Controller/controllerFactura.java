package Controller;

import Coneccion.CRUD_Factura;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controllerFactura {
    private CRUD_Factura crudFactura;

    public controllerFactura() {
        crudFactura = new CRUD_Factura();
    }

    public int crearFactura(int idVendedor, int idCliente, String placa, double precio, Date fecha) throws SQLException {
        // Convertir placa a mayúsculas antes de enviarla al CRUD
        return crudFactura.crearFactura(idVendedor, idCliente, placa.toUpperCase(), precio, fecha);
    }

    public ResultSet obtenerVentas() throws SQLException {
        return crudFactura.listarVentas();
    }
}

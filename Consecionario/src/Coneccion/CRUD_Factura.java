package Coneccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class CRUD_Factura {
    // Crear factura usando procedimiento almacenado
    public int crearFactura(int idVendedor, int idCliente, String placa, double precio, Date fecha) throws SQLException {
        String sql = "CALL sp_registrar_factura(?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idVendedor);
        stmt.setInt(2, idCliente);
        stmt.setString(3, placa);
        stmt.setDouble(4, precio);
        stmt.setDate(5, fecha);
        return stmt.executeUpdate();
    }

    // Buscar facturas por id de cliente
    public ResultSet buscarFacturasPorCliente(int idCliente) throws SQLException {
        String sql = "CALL sp_buscar_facturas_por_cliente(?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idCliente);
        return stmt.executeQuery();
    }

    // Buscar facturas por fecha o rango de fechas
    public ResultSet buscarFacturasPorFecha(Date fechaInicio, Date fechaFin) throws SQLException {
        String sql = "CALL sp_buscar_facturas_por_fecha(?, ?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setDate(1, fechaInicio);
        stmt.setDate(2, fechaFin);
        return stmt.executeQuery();
    }

    public ResultSet listarVentas() throws SQLException {
        String sql = "CALL sp_listar_ventas()";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        return stmt.executeQuery();
    }
}

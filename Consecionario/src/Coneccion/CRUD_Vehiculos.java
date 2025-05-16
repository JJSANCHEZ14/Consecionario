package Coneccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CRUD_Vehiculos {
    private Connection con;

    public CRUD_Vehiculos() {
        con = Coneccion.Conexion.getConexion();
    }

    // Obtiene los vehículos actuales de un cliente (no vendidos)
    public ResultSet obtenerVehiculosPorClienteId(int clienteId) throws Exception {
        String sql = "CALL sp_obtener_vehiculos_por_cliente_id(?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, clienteId);
        return ps.executeQuery();
    }
}

package Coneccion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;

public class CRUD_Persona {
    // Obtener lista de clientes (todas las personas que NO son vendedores)
    public ResultSet obtenerListaClientes() throws SQLException {
        String sql = "CALL sp_obtener_lista_clientes()";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        return stmt.executeQuery();
    }

    // Buscar persona/cliente por ID usando procedure
    public ResultSet obtenerClientePorId(int idpersona) throws SQLException {
        String sql = "CALL sp_obtener_cliente_por_id(?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idpersona);
        return stmt.executeQuery();
    }

    // Guardar persona/cliente usando procedure
    public int guardarCliente(int idpersona, String nombre, String apellido, String telefono, String email) throws SQLException {
        String sql = "CALL sp_guardar_cliente(?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idpersona);
        stmt.setString(2, nombre);
        stmt.setString(3, apellido);
        stmt.setString(4, telefono);
        stmt.setString(5, email);
        return stmt.executeUpdate();
    }

    // Actualizar persona/cliente usando procedure
    public int actualizarCliente(int idpersona, String nombre, String apellido, String telefono, String email) throws SQLException {
        String sql = "CALL sp_actualizar_cliente(?, ?, ?, ?, ?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idpersona);
        stmt.setString(2, nombre);
        stmt.setString(3, apellido);
        stmt.setString(4, telefono);
        stmt.setString(5, email);
        return stmt.executeUpdate();
    }

    // Login de persona/vendedor
    public ResultSet login(String usuario, String clave) throws SQLException {
        Connection cn = Conexion.getConexion();
        CallableStatement cs = cn.prepareCall("{CALL sp_login_vendedor(?, ?)}");
        cs.setString(1, usuario);
        cs.setString(2, clave);
        return cs.executeQuery();
    }

    // Registrar nuevo vendedor
    public int registrarVendedor(int idPersona, String nombreUsuario, String clave) throws SQLException {
        String sql = "CALL sp_insertar_vendedor(?, ?, ?)";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setInt(1, idPersona);
        stmt.setString(2, nombreUsuario);
        stmt.setString(3, clave);
        return stmt.executeUpdate();
    }
}

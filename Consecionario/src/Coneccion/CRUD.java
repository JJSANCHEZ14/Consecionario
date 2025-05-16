/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Coneccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CRUD {
    
    /**
     * Inserta una nueva persona en la tabla persona
     * @return el id de la persona insertada, -1 si hay error
     */
    public int insertarPersona(int id, String nombre, String apellido, String telefono, String email) {
        String QUERY = "INSERT INTO persona (idpersona, nombre, apellido, telefono, email) VALUES (?,?,?,?,?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, id);
            PS.setString(2, nombre);
            PS.setString(3, apellido);
            PS.setString(4, telefono);
            PS.setString(5, email);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar persona");
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Inserta un nuevo vendedor en la tabla vendedor
     * @return 1 si se insertó correctamente, -1 si hay error
     */
    public int insertarVendedor(int idPersona, String nombreUsuario, String clave) {
        String QUERY = "INSERT INTO vendedor (idvendedor, nombreUsuario, clave) VALUES (?,?,?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, idPersona);
            PS.setString(2, nombreUsuario);
            PS.setString(3, clave);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar vendedor");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Reporte de vehículos más vendidos en un período
     * @param fechaInicio Fecha inicial del período (java.sql.Date)
     * @param fechaFin Fecha final del período (java.sql.Date)
     * @return ResultSet con: placa, marca, modelo, cantidad_vendida
     */
    public ResultSet reporteVehiculosMasVendidos(Date fechaInicio, Date fechaFin) {
        String QUERY = """
            SELECT v.placa, v.marca, v.modelo, COUNT(*) as cantidad_vendida 
            FROM vehiculo v 
            INNER JOIN factura f ON v.placa = f.vehiculo 
            WHERE f.fecha BETWEEN ? AND ? 
            GROUP BY v.placa, v.marca, v.modelo 
            ORDER BY cantidad_vendida DESC
        """;
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            PS.setDate(2, new java.sql.Date(fechaFin.getTime()));
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de vehículos más vendidos");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Reporte de clientes que más vehículos han comprado
     * @return ResultSet con: idpersona, nombre, apellido, total_compras
     */
    public ResultSet reporteClientesTopCompradores() {
        String QUERY = """
            SELECT p.idpersona, p.nombre, p.apellido, COUNT(*) as total_compras 
            FROM persona p 
            INNER JOIN factura f ON p.idpersona = f.cliente 
            GROUP BY p.idpersona, p.nombre, p.apellido 
            ORDER BY total_compras DESC
        """;
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de clientes top");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Reporte de tipos de vehículos más vendidos
     * @return ResultSet con: tipo, cantidad_vendida
     */
    public ResultSet reporteTiposVehiculosMasVendidos() {
        String QUERY = """
            SELECT v.tipo, COUNT(*) as cantidad_vendida 
            FROM vehiculo v 
            INNER JOIN factura f ON v.placa = f.vehiculo 
            GROUP BY v.tipo 
            ORDER BY cantidad_vendida DESC
        """;
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de tipos más vendidos");
            e.printStackTrace();
            return null;
        }
    }

    // OPERACIONES CRUD PARA VEHÍCULOS
    
    /**
     * Insertar nuevo vehículo
     * @param placa Placa del vehículo (única)
     * @param marca Marca del vehículo
     * @param modelo Modelo del vehículo
     * @param anio Año del vehículo
     * @param costo Costo de compra del vehículo
     * @param precio Precio de venta esperado
     * @param estado Estado del vehículo (1=nuevo, 2=usado)
     * @param detalles Detalles adicionales del vehículo
     * @return 1 si se insertó correctamente, -1 si hubo error
     */
    public int insertarVehiculo(String placa, String marca, String modelo, 
                              int anio, double costo, double precio,
                              int estado, String detalles) {
        try {
            Connection cn = Conexion.getConexion();
            CallableStatement cs = cn.prepareCall("{CALL sp_insertar_vehiculo(?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setString(1, placa);
            cs.setString(2, marca);
            cs.setString(3, modelo);
            cs.setInt(4, anio);
            cs.setDouble(5, costo);
            cs.setDouble(6, precio);
            cs.setInt(7, estado);
            cs.setString(8, detalles);
            int res = cs.executeUpdate();
            cs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo (SP)");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Registrar venta de vehículo usando procedimiento almacenado
     * @param idVendedor ID del vendedor
     * @param idCliente ID del cliente
     * @param placa Placa del vehículo
     * @param precio Precio de venta
     * @param numeroFactura Número de factura
     * @return 1 si se registró correctamente, -1 si hubo error
     */
    public int registrarVenta(int idVendedor, int idCliente, String placa, int precio, String numeroFactura) {
        try {
            Connection cn = Conexion.getConexion();
            CallableStatement cs = cn.prepareCall("{CALL sp_registrar_venta(?, ?, ?, ?, ?)}");
            cs.setInt(1, idVendedor);
            cs.setInt(2, idCliente);
            cs.setString(3, placa);
            cs.setInt(4, precio);
            cs.setString(5, numeroFactura);
            int res = cs.executeUpdate();
            cs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error al registrar venta (SP)");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Actualizar estado de un vehículo
     * @param placa Placa del vehículo
     * @param estado Nuevo estado (0=no disponible, 1=disponible)
     * @return 1 si se actualizó correctamente, -1 si hubo error
     */
    public int actualizarEstadoVehiculo(String placa, int estado) {
        String QUERY = "UPDATE vehiculo SET estado = ? WHERE placa = ?";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, estado);
            PS.setString(2, placa);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del vehículo");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Registrar nuevo vendedor
     * @param idPersona ID de la persona (debe existir en tabla persona)
     * @param nombreUsuario Nombre de usuario único
     * @param clave Clave del vendedor
     * @return 1 si se registró correctamente, -1 si hubo error
     */
    public int registrarVendedor(int idPersona, String nombreUsuario, String clave) {
        String QUERY = "INSERT INTO vendedor (idvendedor, nombreUsuario, clave) VALUES (?,?,?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, idPersona);
            PS.setString(2, nombreUsuario);
            PS.setString(3, clave);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar vendedor");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Registrar nuevo propietario de vehículo
     * @param idCliente ID del cliente (debe existir en tabla persona)
     * @param placa Placa del vehículo
     * @return 1 si se registró correctamente, -1 si hubo error
     */
    public int registrarPropietario(int idCliente, String placa) {
        String QUERY = "INSERT INTO propietario (idcliente, vehiculo) VALUES (?,?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, idCliente);
            PS.setString(2, placa);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar propietario");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Buscar vehículos disponibles por criterios
     * @param marca Marca del vehículo (opcional)
     * @param tipo Tipo de vehículo (opcional)
     * @param anioMin Año mínimo (opcional)
     * @return ResultSet con los vehículos que cumplen los criterios
     */
    public ResultSet buscarVehiculos(String marca, String tipo, Integer anioMin) {
        StringBuilder QUERY = new StringBuilder("SELECT * FROM vehiculo WHERE estado = 1");
        List<Object> params = new ArrayList<>();
        
        if (marca != null) {
            QUERY.append(" AND marca = ?");
            params.add(marca);
        }
        if (tipo != null) {
            QUERY.append(" AND tipo = ?");
            params.add(tipo);
        }
        if (anioMin != null) {
            QUERY.append(" AND anio >= ?");
            params.add(anioMin);
        }
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY.toString());
            for (int i = 0; i < params.size(); i++) {
                PS.setObject(i + 1, params.get(i));
            }
            
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al buscar vehículos");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Validar credenciales de login de un vendedor
     * @param nombreUsuario Nombre de usuario del vendedor
     * @param clave Contraseña del vendedor
     * @return ResultSet con los datos del vendedor si las credenciales son correctas, null si son incorrectas
     */
    public ResultSet login(String nombreUsuario, String clave) {
        String QUERY = """
            SELECT v.idvendedor, p.nombre, p.apellido, v.nombreUsuario 
            FROM vendedor v 
            INNER JOIN persona p ON v.idvendedor = p.idpersona 
            WHERE v.nombreUsuario = ? AND v.clave = ?
        """;
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setString(1, nombreUsuario);
            PS.setString(2, clave);
            
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al intentar hacer login");
            e.printStackTrace();
            return null;
        }
    }
    
    public ResultSet obtenerVehiculos() {
        String QUERY = """
            SELECT marca, modelo, anio, placa, costo, precio,
            CASE estado 
                WHEN 0 THEN 'Vendido'
                WHEN 1 THEN 'Disponible (Nuevo)'
                WHEN 2 THEN 'Disponible (Usado)'
                ELSE 'Desconocido'
            END as estado
            FROM vehiculo 
            ORDER BY estado DESC, marca, modelo
        """;
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            return PS.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al obtener vehículos");
            e.printStackTrace();
            return null;
        }
    }

    public boolean existePlaca(String placa) {
        String QUERY = "SELECT COUNT(*) FROM vehiculo WHERE placa = ?";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setString(1, placa);
            ResultSet RS = PS.executeQuery();
            
            if (RS.next()) {
                return RS.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al verificar placa");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualizar el precio de un vehículo
     * @param placa Placa del vehículo
     * @param nuevoPrecio Nuevo precio de venta
     * @return 1 si se actualizó correctamente, -1 si hubo error
     */
    public int actualizarPrecioVehiculo(String placa, double nuevoPrecio) {
        String QUERY = "UPDATE vehiculo SET precio = ? WHERE placa = ?";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setDouble(1, nuevoPrecio);
            PS.setString(2, placa);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar precio del vehículo");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Buscar cliente por nombre usando procedimiento almacenado
     */
    public ResultSet buscarClientePorNombre(String nombre) throws SQLException {
        String sql = "CALL sp_buscar_cliente_por_nombre(?)";
        PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, nombre);
        return stmt.executeQuery();
    }

    /**
     * Obtener vehículo por placa usando procedimiento almacenado
     */
    public ResultSet obtenerVehiculoPorPlaca(String placa) throws SQLException {
        String sql = "CALL sp_obtener_vehiculo_por_placa(?)";
        PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, placa);
        return stmt.executeQuery();
    }

    /**
     * Marcar vehículo como vendido usando procedimiento almacenado
     */
    public int marcarVehiculoVendido(String placa) throws SQLException {
        String sql = "CALL sp_marcar_vehiculo_vendido(?)";
        PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, placa);
        return stmt.executeUpdate();
    }

    /**
     * Obtener facturas por fecha usando procedimiento almacenado
     */
    public ResultSet obtenerFacturasPorFecha(java.sql.Date fecha) throws SQLException {
        String sql = "CALL sp_obtener_facturas_por_fecha(?)";
        PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setDate(1, fecha);
        return stmt.executeQuery();
    }

    /**
     * Obtener vehículos más vendidos por marca usando procedimiento almacenado
     */
    public ResultSet vehiculosMasVendidosPorMarca() throws SQLException {
        String sql = "CALL sp_vehiculos_mas_vendidos()";
        PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        return stmt.executeQuery();
    }

    public int actualizarVehiculo(String placa, String marca, String modelo, int anio, 
                                double costo, double precio, int estado, String detalle) throws SQLException {
        String sql = "CALL sp_actualizar_vehiculo(?, ?, ?, ?, ?, ?, ?, ?)";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, placa);
        stmt.setString(2, marca);
        stmt.setString(3, modelo);
        stmt.setInt(4, anio);
        stmt.setDouble(5, costo);
        stmt.setDouble(6, precio);
        stmt.setInt(7, estado);
        stmt.setString(8, detalle);
        return stmt.executeUpdate();
    }

    public int guardarCliente(int idpersona, String nombre, String apellido, 
                            String telefono, String email) throws SQLException {
        String sql = "CALL sp_guardar_cliente(?, ?, ?, ?, ?)";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setInt(1, idpersona);
        stmt.setString(2, nombre);
        stmt.setString(3, apellido);
        stmt.setString(4, telefono);
        stmt.setString(5, email);
        return stmt.executeUpdate();
    }

    public ResultSet obtenerClientePorId(int idpersona) throws SQLException {
        String sql = "CALL sp_obtener_cliente_por_id(?)";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setInt(1, idpersona);
        return stmt.executeQuery();
    }

    public int actualizarCliente(int idpersona, String nombre, String apellido, 
                            String telefono, String email) throws SQLException {
        String sql = "CALL sp_actualizar_cliente(?, ?, ?, ?, ?)";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setInt(1, idpersona);
        stmt.setString(2, nombre);
        stmt.setString(3, apellido);
        stmt.setString(4, telefono);
        stmt.setString(5, email);
        return stmt.executeUpdate();
    }

    /**
     * Obtiene la lista de todas las personas que no son vendedores
     * @return ResultSet con los datos de los clientes
     */
    public ResultSet obtenerListaClientes() throws SQLException {
        String sql = "CALL sp_obtener_lista_clientes()";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        return stmt.executeQuery();
    }

    /**
     * Inserta una nueva persona usando procedimiento almacenado
     * @param id id de la persona
     * @param nombre nombre
     * @param apellido apellido
     * @param telefono telefono
     * @param email email
     */
    public void insertarPersonaSP(int id, String nombre, String apellido, String telefono, String email) throws SQLException {
        Connection cn = Conexion.getConexion();
        CallableStatement cs = cn.prepareCall("{CALL sp_insertar_persona(?, ?, ?, ?, ?)}");
        cs.setInt(1, id);
        cs.setString(2, nombre);
        cs.setString(3, apellido);
        cs.setString(4, telefono);
        cs.setString(5, email);
        cs.executeUpdate();
        cs.close();
    }

    /**
     * Inserta un nuevo vendedor usando procedimiento almacenado
     * @param idPersona id de la persona
     * @param usuario nombre de usuario
     * @param clave clave del vendedor
     */
    public void insertarVendedorSP(int idPersona, String usuario, String clave) throws SQLException {
        Connection cn = Conexion.getConexion();
        CallableStatement cs = cn.prepareCall("{CALL sp_insertar_vendedor(?, ?, ?)}");
        cs.setInt(1, idPersona);
        cs.setString(2, usuario);
        cs.setString(3, clave);
        cs.executeUpdate();
        cs.close();
    }

    public int asignarPropietario(String placa, int idCliente) throws SQLException {
        String sql = "INSERT INTO propietario (vehiculo, idcliente) VALUES (?, ?) ON DUPLICATE KEY UPDATE idcliente = ?";
        Connection cn = Conexion.getConexion();
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setString(1, placa);
        stmt.setInt(2, idCliente);
        stmt.setInt(3, idCliente);
        return stmt.executeUpdate();
    }
}

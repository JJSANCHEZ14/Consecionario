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
        String QUERY = "INSERT INTO vehiculo (placa, marca, modelo, anio, tipo, estado, detalle, costo, precio) " +
                      "VALUES (?, ?, ?, ?, 'GENERAL', ?, ?, ?, ?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setString(1, placa);
            PS.setString(2, marca);
            PS.setString(3, modelo);
            PS.setInt(4, anio);
            PS.setInt(5, estado);
            PS.setString(6, detalles);
            PS.setDouble(7, costo);
            PS.setDouble(8, precio);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Registrar venta de vehículo
     * @param idVendedor ID del vendedor
     * @param idCliente ID del cliente
     * @param placa Placa del vehículo
     * @param precio Precio de venta
     * @param numeroFactura Número de factura
     * @return 1 si se registró correctamente, -1 si hubo error
     */
    public int registrarVenta(int idVendedor, int idCliente, String placa, int precio, String numeroFactura) {
        String QUERY = "INSERT INTO factura (usuario, cliente, vehiculo, precio, fecha, factura) VALUES (?,?,?,?,?,?)";
        
        try {
            Connection Con = Conexion.getConexion();
            PreparedStatement PS = Con.prepareStatement(QUERY);
            PS.setInt(1, idVendedor);
            PS.setInt(2, idCliente);
            PS.setString(3, placa);
            PS.setInt(4, precio);
            PS.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            PS.setString(6, numeroFactura);
            
            // Actualizar estado del vehículo a vendido (0)
            actualizarEstadoVehiculo(placa, 0);
            
            return PS.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar venta");
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

    public ResultSet obtenerVehiculoPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM vehiculo WHERE placa = ?";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, placa);
        return stmt.executeQuery();
    }

    public int actualizarVehiculo(String placa, String marca, String modelo, int anio, 
                                 double costo, double precio, int estado, String detalle) throws SQLException {
        String sql = "UPDATE vehiculo SET marca = ?, modelo = ?, anio = ?, " +
                    "costo = ?, precio = ?, estado = ?, detalle = ? WHERE placa = ?";
        
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, marca);
        stmt.setString(2, modelo);
        stmt.setInt(3, anio);
        stmt.setDouble(4, costo);
        stmt.setDouble(5, precio);
        stmt.setInt(6, estado);
        stmt.setString(7, detalle);
        stmt.setString(8, placa);
        
        return stmt.executeUpdate();
    }

    public int guardarCliente(int idpersona, String nombre, String apellido, 
                            String telefono, String email) throws SQLException {
        String sql = "INSERT INTO persona (idpersona, nombre, apellido, telefono, email) " +
                    "VALUES (?, ?, ?, ?, ?)";
                    
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setInt(1, idpersona);
        stmt.setString(2, nombre);
        stmt.setString(3, apellido);
        stmt.setString(4, telefono);
        stmt.setString(5, email);
        
        return stmt.executeUpdate();
    }

    public ResultSet obtenerClientePorId(int idpersona) throws SQLException {
        String sql = "SELECT * FROM persona WHERE idpersona = ?";
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setInt(1, idpersona);
        return stmt.executeQuery();
    }

    public int actualizarCliente(int idpersona, String nombre, String apellido, 
                               String telefono, String email) throws SQLException {
        String sql = "UPDATE persona SET nombre = ?, apellido = ?, " +
                    "telefono = ?, email = ? WHERE idpersona = ?";
                    
        java.sql.PreparedStatement stmt = Conexion.getConexion().prepareStatement(sql);
        stmt.setString(1, nombre);
        stmt.setString(2, apellido);
        stmt.setString(3, telefono);
        stmt.setString(4, email);
        stmt.setInt(5, idpersona);
        
        return stmt.executeUpdate();
    }

    /**
     * Obtiene la lista de todas las personas que no son vendedores
     * @return ResultSet con los datos de los clientes
     */
    public ResultSet obtenerListaClientes() throws SQLException {
        String QUERY = """
            SELECT p.* FROM persona p 
            LEFT JOIN vendedor v ON p.idpersona = v.idvendedor 
            WHERE v.idvendedor IS NULL 
            ORDER BY p.idpersona
        """;
        
        Connection Con = Conexion.getConexion();
        return Con.createStatement().executeQuery(QUERY);
    }
}

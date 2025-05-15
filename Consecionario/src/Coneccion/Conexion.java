package Coneccion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    static String connectionURL = "jdbc:mysql://localhost:3306/mydb";
    static String user = "root";
    static String password = "cbn2016";

    public static Connection getConexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(connectionURL, user, password);
            System.out.println("Conexion exitosa");
        } catch (SQLException ex) {
            System.out.println("Error en la conexión a la base de datos");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: No se encontró el driver de MySQL");
            ex.printStackTrace();
        }
        return con;
    }
}

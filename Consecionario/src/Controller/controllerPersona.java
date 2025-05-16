package Controller;

import Coneccion.CRUD_Persona;
import java.sql.ResultSet;
import java.sql.SQLException;
import Modelo.Persona;

public class controllerPersona {
    private CRUD_Persona crudPersona;

    public controllerPersona() {
        crudPersona = new CRUD_Persona();
    }

    // Nuevo método: retorna Persona si login exitoso, null si no
    public Persona loginVendedorYObtenerDatos(String usuario, String clave) throws SQLException {
        ResultSet rs = crudPersona.login(usuario.toUpperCase(), clave.toUpperCase());
        if (rs.next()) {
            int id = rs.getInt("idpersona");
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            String email = rs.getString("email");
            return new Persona(id, nombre, apellido, email);
        }
        return null;
    }

    // Método anterior, si lo sigues usando
    public boolean loginVendedor(String usuario, String clave) throws SQLException {
        ResultSet rs = crudPersona.login(usuario.toUpperCase(), clave.toUpperCase());
        return rs.next();
    }

    // Puedes agregar más métodos de validación o lógica de negocio aquí
}

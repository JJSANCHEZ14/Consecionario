package util;

public class SesionUsuario {
    private static int id;
    private static String nombre;
    private static String apellido;
    private static String email;

    public static void setDatos(int id, String nombre, String apellido, String email) {
        SesionUsuario.id = id;
        SesionUsuario.nombre = nombre;
        SesionUsuario.apellido = apellido;
        SesionUsuario.email = email;
    }

    public static int getId() { return id; }
    public static String getNombre() { return nombre; }
    public static String getApellido() { return apellido; }
    public static String getEmail() { return email; }
}
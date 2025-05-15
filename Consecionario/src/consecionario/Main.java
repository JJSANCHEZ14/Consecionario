package consecionario;

public class Main {

    public static void main(String[] args) {
        /* Crear y mostrar el formulario de login */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Crear instancia del login
                Pantalla pantalla = new Pantalla();
                pantalla.setSize(680, 420);
                pantalla.setLocation(0, 0);
                // Mostrar la ventana
                pantalla.setVisible(true);
            }
        });
    }
}

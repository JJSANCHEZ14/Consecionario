/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package consecionario;

/**
 *
 * @author Isabel Pallares
 */
import java.util.ArrayList;

public class Consignataria {
    private ArrayList<Propietario> propietarios;

    public Consignataria() {
        this.propietarios = new ArrayList<>();
    }

    public void agregarPropietario(Propietario propietario) {
        this.propietarios.add(propietario);
    }

    public Propietario buscarPropietario(String nombre) {
        for (Propietario propietario : propietarios) {
            if (propietario.getNombre().equals(nombre)) {
                return propietario;
            }
        }
        return null;
    }
}

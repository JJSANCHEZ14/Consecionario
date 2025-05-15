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

public class Propietario {
    private String nombre;
    private ArrayList<Vehiculo> vehiculosConsignados;

    public Propietario(String nombre) {
        this.nombre = nombre;
        this.vehiculosConsignados = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        this.vehiculosConsignados.add(vehiculo);
    }

    public ArrayList<Vehiculo> getVehiculosConsignados() {
        return vehiculosConsignados;
    }
}

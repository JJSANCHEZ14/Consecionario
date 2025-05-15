/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package consecionario;

/**
 *
 * @author Isabel Pallares
 */
public class Vehiculo {
    private String marca;
    private String modelo;
    private int anio;
    private String placa;
    private String tipo;
    private int estado;
    private String detalle;
    private double costo;
    private double precio;

    public Vehiculo(String placa, String marca, String modelo, int anio, 
                   String tipo, double costo, double precio) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.tipo = tipo;
        this.estado = 1; // Por defecto es disponible y nuevo
        this.detalle = "Vehículo nuevo";
        this.costo = costo;
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Vehiculo{" + 
               "placa='" + placa + '\'' + 
               ", marca='" + marca + '\'' + 
               ", modelo='" + modelo + '\'' + 
               ", anio=" + anio + 
               ", tipo='" + tipo + '\'' +
               ", estado=" + estado +
               ", detalle='" + detalle + '\'' +
               ", costo=" + costo +
               ", precio=" + precio +
               '}';
    }
}

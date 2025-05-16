package Controller;

import Coneccion.CRUD_Vehiculos;
import java.sql.ResultSet;

public class controllerVentas {
    private CRUD_Vehiculos crudVehiculos = new CRUD_Vehiculos();

    public ResultSet obtenerVehiculosPorClienteId(int clienteId) throws Exception {
        return crudVehiculos.obtenerVehiculosPorClienteId(clienteId);
    }


}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package consecionario;

import javax.swing.JOptionPane;
import util.SesionUsuario;
import Controller.controllerFactura;

/**
 *
 * @author jjsan
 */
public class MenuVenta extends javax.swing.JPanel {

    // Instancias para acceso a base de datos y controladores
    
    private Coneccion.CRUD_Persona crudPersona = new Coneccion.CRUD_Persona(); // Solo para clientes
    private Coneccion.CRUD crud = new Coneccion.CRUD(); // Para vehículos y facturas
    private Controller.controllerConcesionario controlador = new Controller.controllerConcesionario();
    private Controller.controllerVentas controllerVehiculos = new Controller.controllerVentas(); // Para vehículos por cliente
    
    private int idClienteSeleccionado = 0; // Agregado para seguimiento del cliente seleccionado
    
    /**
     * Creates new form MenuVenta
     */
    public MenuVenta() {
        initComponents();
        inicializarInputs();
        agregarListeners();
    }

    private void inicializarInputs() {
        NombreClienteCompleto.setText("");
        PlacaVehiculo.setText("");
        PrecioVehiculo.setText("");
        NombreClienteCompleto.setEditable(false);
        PlacaVehiculo.setEditable(false);
        PrecioVehiculo.setEditable(false);
    }

    private void agregarListeners() {
        btnBuscarCliente.addActionListener(e -> buscarClientePorId());
        btnBuscarVehiculo.addActionListener(e -> buscarVehiculo());
        btnVenderVehiculo.addActionListener(e -> venderVehiculo());
        btnMostrarFacturas.addActionListener(e -> mostrarFacturasPorFecha());
        btnVehiculosVendidos.addActionListener(e -> mostrarVehiculosMasVendidos());
        btnVerVentas.addActionListener(e -> mostrarVentas());
    }

    private void buscarClientePorId() {
        String idStr = javax.swing.JOptionPane.showInputDialog(this, "Ingrese el ID del cliente:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            java.sql.ResultSet rs = crudPersona.obtenerClientePorId(id); // Usar CRUD_Persona
            if (rs.next()) {
                idClienteSeleccionado = id; // Guardar el ID del cliente seleccionado
                NombreClienteCompleto.setText(rs.getString("nombre") + " " + rs.getString("apellido"));
                javax.swing.JOptionPane.showMessageDialog(this, "Cliente encontrado");
                // Mostrar vehículos del cliente en la tabla usando el controller
                refrescarTablaVehiculosCliente(id);
            } else {
                idClienteSeleccionado = 0;
                NombreClienteCompleto.setText("");
                TablaInformes.setModel(new javax.swing.table.DefaultTableModel()); // Limpiar tabla
                javax.swing.JOptionPane.showMessageDialog(this, "No se encontró el cliente");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage());
        }
    }

    private void buscarVehiculo() {
        String placa = JOptionPane.showInputDialog(this, "Ingrese la placa del vehículo:");
        if (placa.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ingrese la placa del vehículo a buscar");
            return;
        }
        try {
            java.sql.ResultSet rs = crud.obtenerVehiculoPorPlaca(placa);
            if (rs.next()) {
                PlacaVehiculo.setText(rs.getString("placa"));
                PrecioVehiculo.setText(String.valueOf(rs.getDouble("precio")));
                javax.swing.JOptionPane.showMessageDialog(this, "Vehículo encontrado");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "No se encontró el vehículo");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al buscar vehículo: " + ex.getMessage());
        }
    }

    private void venderVehiculo() {
        String placa = PlacaVehiculo.getText().trim();
        if (placa.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ingrese la placa del vehículo a vender");
            return;
        }
        if (idClienteSeleccionado == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe buscar y seleccionar un cliente antes de vender.");
            return;
        }
        try {
            // 1. Marcar el vehículo como vendido
            System.err.println("Venta inializada");
            int res = crud.marcarVehiculoVendido(placa);
            if (res > 0) {
                // 2. Asignar el vehículo al cliente en propietario
                int resProp = crud.asignarPropietario(placa, idClienteSeleccionado);
                if (resProp <= 0) {
                    javax.swing.JOptionPane.showMessageDialog(this, "No se pudo asignar el propietario. Venta cancelada.");
                    return;
                }
                System.out.println("Vehículo vendido y propietario asignado.");

                // 3. Preguntar por la fecha de la venta
                String fechaStr = javax.swing.JOptionPane.showInputDialog(this, "Ingrese la fecha de la venta (DDMMAAAA):");
                if (fechaStr == null || fechaStr.trim().length() != 8) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Fecha inválida. Debe tener el formato DDMMAAAA.");
                    return;
                }
                String fechaFormateada = fechaStr.substring(4,8) + "-" + fechaStr.substring(2,4) + "-" + fechaStr.substring(0,2);
                java.sql.Date fechaVenta = java.sql.Date.valueOf(fechaFormateada);

                // 4. Crear la factura usando el controlador
                int idVendedor = SesionUsuario.getId();
                Controller.controllerFactura controllerFactura = new Controller.controllerFactura();
                double precio = Double.parseDouble(PrecioVehiculo.getText());
                int resFactura = controllerFactura.crearFactura(idVendedor, idClienteSeleccionado, placa, precio, fechaVenta);

                // 5. Mostrar mensaje de éxito y refrescar tabla
                javax.swing.JOptionPane.showMessageDialog(this, "El carro fue vendido exitosamente, factura creada.");
                PlacaVehiculo.setText("");
                PrecioVehiculo.setText("");
                refrescarTablaVehiculosCliente(idClienteSeleccionado);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo vender el vehículo");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al vender vehículo: " + ex.getMessage());
        }
    }

    // Método para refrescar la tabla de vehículos del cliente
    private void refrescarTablaVehiculosCliente(int idCliente) {
        try {
            java.sql.ResultSet vehiculos = controllerVehiculos.obtenerVehiculosPorClienteId(idCliente);
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Placa", "Marca", "Modelo", "Año", "Precio"});
            boolean tieneVehiculos = false;
            while (vehiculos.next()) {
                tieneVehiculos = true;
                model.addRow(new Object[]{
                    vehiculos.getString("placa"),
                    vehiculos.getString("marca"),
                    vehiculos.getString("modelo"),
                    vehiculos.getInt("anio"),
                    vehiculos.getDouble("precio")
                });
            }
            TablaInformes.setModel(model);
            if (!tieneVehiculos) {
                javax.swing.JOptionPane.showMessageDialog(this, "El cliente no posee vehículos actualmente.");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al refrescar vehículos: " + ex.getMessage());
        }
    }

    private void mostrarFacturasPorFecha() {
        String fecha = javax.swing.JOptionPane.showInputDialog(this, "Ingrese la fecha (YYYY-MM-DD):");
        if (fecha == null || fecha.trim().isEmpty()) return;
        try {
            java.sql.Date fechaSQL = java.sql.Date.valueOf(fecha.trim());
            java.sql.ResultSet rs = crud.obtenerFacturasPorFecha(fechaSQL);
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"ID Factura", "Usuario", "Cliente", "Vehículo", "Precio", "Fecha", "Factura"});
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idfactura"),
                    rs.getInt("usuario"),
                    rs.getInt("cliente"),
                    rs.getString("vehiculo"),
                    rs.getDouble("precio"),
                    rs.getDate("fecha"),
                    rs.getString("factura")
                });
            }
            TablaInformes.setModel(model);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al mostrar facturas: " + ex.getMessage());
        }
    }

    private void mostrarVehiculosMasVendidos() {
        try {
            java.sql.ResultSet rs = crud.vehiculosMasVendidosPorMarca();
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Marca", "Cantidad Vendida"});
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("marca"),
                    rs.getInt("cantidad_vendida")
                });
            }
            TablaInformes.setModel(model);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al mostrar vehículos más vendidos: " + ex.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        NombreClienteCompleto = new javax.swing.JTextField();
        PlacaVehiculo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        PrecioVehiculo = new javax.swing.JTextField();
        NVendedor = new javax.swing.JLabel();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        btnBuscarCliente = new javax.swing.JButton();
        btnBuscarVehiculo = new javax.swing.JButton();
        btnVenderVehiculo = new javax.swing.JButton();
        btnMostrarFacturas = new javax.swing.JButton();
        btnVehiculosVendidos = new javax.swing.JButton();
        btnVerVentas = new javax.swing.JButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaInformes = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 3, 18)); // NOI18N
        jLabel1.setText("Modulo de Ventas");

        jLabel2.setText("Cliente");

        NombreClienteCompleto.setText("jTextField1");

        PlacaVehiculo.setText("jTextField1");

        jLabel3.setText("Vehiculo");

        jLabel4.setText("Precio de Venta");

        PrecioVehiculo.setText("jTextField1");

        NVendedor.setFont(new java.awt.Font("Segoe UI Black", 3, 18)); // NOI18N
        NVendedor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        NVendedor.setText("Vendedor Acutal");

        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(NombreClienteCompleto, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(PlacaVehiculo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(PrecioVehiculo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(NVendedor, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(NVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(NombreClienteCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PrecioVehiculo, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                            .addComponent(PlacaVehiculo))))
                .addGap(17, 17, 17))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(NVendedor))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(NombreClienteCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PlacaVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(PrecioVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Funciones"));

        btnBuscarCliente.setText("Buscar Cliente");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        btnBuscarVehiculo.setText("Buscar Vehiculo");
        btnBuscarVehiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarVehiculoActionPerformed(evt);
            }
        });

        btnVenderVehiculo.setText("Vender Vehiculo");
        btnVenderVehiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVenderVehiculoActionPerformed(evt);
            }
        });

        btnMostrarFacturas.setText("Facturas");

        btnVehiculosVendidos.setText("Vehiculos + Vendidos");
        btnVehiculosVendidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVehiculosVendidosActionPerformed(evt);
            }
        });

        btnVerVentas.setText("Ver Ventas");

        jLayeredPane3.setLayer(btnBuscarCliente, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(btnBuscarVehiculo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(btnVenderVehiculo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(btnMostrarFacturas, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(btnVehiculosVendidos, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(btnVerVentas, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnMostrarFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarVehiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVehiculosVendidos, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVenderVehiculo, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(btnVerVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscarCliente)
                    .addComponent(btnBuscarVehiculo)
                    .addComponent(btnVenderVehiculo))
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMostrarFacturas)
                    .addComponent(btnVehiculosVendidos)
                    .addComponent(btnVerVentas))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLayeredPane2.setBackground(new java.awt.Color(204, 255, 255));
        jLayeredPane2.setBorder(new javax.swing.border.MatteBorder(null));

        TablaInformes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(TablaInformes);

        jLayeredPane2.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                .addContainerGap())
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLayeredPane3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnVenderVehiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVenderVehiculoActionPerformed
        //venderVehiculo();
    }//GEN-LAST:event_btnVenderVehiculoActionPerformed

    private void btnMostrarFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarFacturasActionPerformed
        mostrarFacturasPorFecha();
    }//GEN-LAST:event_btnMostrarFacturasActionPerformed

    private void btnVehiculosVendidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVehiculosVendidosActionPerformed
        mostrarVehiculosMasVendidos();
    }//GEN-LAST:event_btnVehiculosVendidosActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        // No hacer nada, el listener real está en agregarListeners()
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnBuscarVehiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarVehiculoActionPerformed
        // No hacer nada, el listener real está en agregarListeners()
    }//GEN-LAST:event_btnBuscarVehiculoActionPerformed

    private void mostrarVentas() {
        try {
            Controller.controllerFactura controllerFactura = new Controller.controllerFactura();
            java.sql.ResultSet rs = controllerFactura.obtenerVentas();
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Cliente", "Marca", "Placa", "Fecha"});
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("cliente"),
                    rs.getString("marca"),
                    rs.getString("placa"),
                    rs.getDate("fecha")
                });
            }
            TablaInformes.setModel(model);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al mostrar ventas: " + ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NVendedor;
    private javax.swing.JTextField NombreClienteCompleto;
    private javax.swing.JTextField PlacaVehiculo;
    private javax.swing.JTextField PrecioVehiculo;
    private javax.swing.JTable TablaInformes;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarVehiculo;
    private javax.swing.JButton btnMostrarFacturas;
    private javax.swing.JButton btnVehiculosVendidos;
    private javax.swing.JButton btnVenderVehiculo;
    private javax.swing.JButton btnVerVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}

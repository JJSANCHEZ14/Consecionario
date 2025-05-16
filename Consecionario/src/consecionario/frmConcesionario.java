package consecionario;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JDialog;
import Coneccion.CRUD;
import Controller.controllerConcesionario;
import util.SesionUsuario;

public class frmConcesionario extends javax.swing.JFrame {

    ArrayList<Vehiculo> listaVehiculos;
    String[] columnas = {"Marca", "Modelo", "Año", "Placa", "Costo", "Precio", "Estado"};
    String nombre;
    String marca;
    String modelo;
    String año;
    String placa;
    String valor;
    String detalles;
    private CRUD crud;
    private controllerConcesionario controlador;

    /**
     * Creates new form frmConcesionario
     */
    public frmConcesionario() {
        initComponents();
        this.setLocationRelativeTo(null);
        listaVehiculos = new ArrayList<>();
        controlador = new controllerConcesionario();
        
        // Ocultar campos no necesarios
        
        txtValor.setVisible(false);
        
        jLabel6.setVisible(false); // Label de Valor
        
        // Inicializar campos
        txtAño.setText("");
        txtModelo.setText("");
        txtPlaca.setText("");
        
        configurarTabla();
        cargarVehiculos();
        
        NUsuario.setText("Bienvenido, " + SesionUsuario.getNombre());
    }

    private void cargarVehiculos() {
        try {
            ResultSet rs = controlador.obtenerVehiculos();
            DefaultTableModel modelo = (DefaultTableModel) tbInfo.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio"),
                    rs.getString("placa"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio"),
                    rs.getString("estado")
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar los vehículos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(columnas);
        tbInfo.setModel(modelo);
    }

    public void llenarCombo() {
        comboMarca.removeAllItems();
        comboMarca.addItem("Seleccione...");
        comboMarca.addItem("TOYOTA");
        comboMarca.addItem("AUDI");
        comboMarca.addItem("BMW");
        comboMarca.addItem("MERCEDES BENZ");
        comboMarca.addItem("KIA");
        comboMarca.addItem("CHEVROLET");
        comboMarca.addItem("RENAULT");
        comboMarca.addItem("VOLKSWAGEN");
        comboMarca.addItem("MITSUBISHI");
        comboMarca.addItem("MAZDA");
        comboMarca.addItem("NISSAN");
    }

    public void Guardar() {
        try {
            placa = txtPlaca.getText().toUpperCase();
            modelo = txtModelo.getText().toUpperCase();
            año = txtAño.getText();
            marca = comboMarca.getSelectedItem().toString();
            detalles = tDetalles.getText();

            if (!controlador.validarCampos(marca, modelo, año, placa)) {
                String mensaje = controlador.mensajeValidacion(marca, modelo, año, placa);
                JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!controlador.existePlaca(placa)) {
                double costo = 0;
                double precio = 0;
                boolean costosValidos = false;
                int estado = SelectEstado.getSelectedIndex() + 1;

                while (!costosValidos) {
                    try {
                        String costoStr = JOptionPane.showInputDialog(this, 
                            "Ingrese el costo de compra del vehículo:", 
                            "Costo", JOptionPane.QUESTION_MESSAGE);
                        if (costoStr == null) return;
                        String precioStr = JOptionPane.showInputDialog(this,
                            "Ingrese el precio de venta esperado:",
                            "Precio", JOptionPane.QUESTION_MESSAGE);
                        if (precioStr == null) return;
                        costo = Double.parseDouble(costoStr);
                        precio = Double.parseDouble(precioStr);

                        String mensajeCostos = controlador.validarCostos(costo, precio);
                        if (!mensajeCostos.isEmpty()) {
                            JOptionPane.showMessageDialog(this, mensajeCostos, "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            costosValidos = true;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                            "Por favor, ingrese valores numéricos válidos",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                int resultado = controlador.insertarVehiculo(
                    placa, marca, modelo,
                    Integer.parseInt(año),
                    costo, precio,
                    estado,
                    detalles
                );

                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Vehículo guardado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarVehiculos();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al guardar el vehículo",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "La placa ya está registrada en el sistema",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese valores numéricos válidos para el año",
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el vehículo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (marca.equals("Seleccione...") || modelo.isEmpty() ||
            año.isEmpty() || placa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete los campos: Marca, Modelo, Año y Placa",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int anioNum = Integer.parseInt(año);
            int anioActual = 2025; // Esto debería obtenerse del sistema
            if (anioNum < 1900 || anioNum > anioActual) {
                JOptionPane.showMessageDialog(this,
                    "El año debe estar entre 1900 y " + anioActual,
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El año debe ser un número válido",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (placa.length() < 6 || placa.length() > 7) {
            JOptionPane.showMessageDialog(this,
                "La placa debe tener entre 6 y 7 caracteres",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void limpiarCampos() {
        txtModelo.setText("");
        txtAño.setText("");
        txtPlaca.setText("");
        comboMarca.setSelectedIndex(0);
        tDetalles.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbInfo = new javax.swing.JTable();
        comboMarca = new javax.swing.JComboBox<>();
        txtAño = new javax.swing.JTextField();
        txtModelo = new javax.swing.JTextField();
        txtPlaca = new javax.swing.JTextField();
        txtValor = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        SelectEstado = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tDetalles = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        btnMenuCliente = new javax.swing.JButton();
        btnBuscarVehiculo = new javax.swing.JButton();
        btnActualizarVehiculo = new javax.swing.JButton();
        btnModuloVentas = new javax.swing.JButton();
        NUsuario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbInfo);

        comboMarca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione...", "TOYOTA", "AUDI", "BMW", "MERCEDES BENZ", "KIA", "CHEVROLET", "REANAULT", "VOLSKWAGEN", "MITSUBISHI", "MAZDA", "NISSAN", " " }));
        comboMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMarcaActionPerformed(evt);
            }
        });

        txtAño.setText("jTextField2");

        txtModelo.setText("jTextField3");
        txtModelo.setPreferredSize(new java.awt.Dimension(100, 22));
        txtModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtModeloActionPerformed(evt);
            }
        });

        txtPlaca.setText("jTextField4");

        txtValor.setText("jTextField5");

        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        jLabel2.setText("Marca del vehiculo :");

        jLabel3.setText("Año del modelo :");

        jLabel4.setText("Modelo del vehiculo :");

        jLabel5.setText("Placa del vehiculo :");

        jLabel6.setText("Valor del Vehiculo");

        jLabel7.setText("Estado");

        SelectEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nuevo", "Usado" }));

        tDetalles.setColumns(20);
        tDetalles.setRows(5);
        jScrollPane2.setViewportView(tDetalles);

        jLabel8.setText("Detalles");

        btnMenuCliente.setText("Menu Cliente");
        btnMenuCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuClienteActionPerformed(evt);
            }
        });

        btnBuscarVehiculo.setText("Buscar Vehiculo");
        btnBuscarVehiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarVehiculoActionPerformed(evt);
            }
        });

        btnActualizarVehiculo.setText("Actualizar Vehiculo");

        btnModuloVentas.setText("Ventas");
        btnModuloVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModuloVentasActionPerformed(evt);
            }
        });

        NUsuario.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        NUsuario.setText("Nombre del usuario actual");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtValor, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtPlaca))
                                    .addGap(182, 182, 182)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(186, 186, 186)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnActualizarVehiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMenuCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBuscarVehiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnModuloVentas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(23, 23, 23))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(54, 54, 54)
                        .addComponent(txtAño, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(NUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SelectEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboMarca, 0, 144, Short.MAX_VALUE))
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(NUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAño, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(SelectEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btnAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarVehiculo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnActualizarVehiculo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMenuCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModuloVentas)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        Guardar();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void comboMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMarcaActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_comboMarcaActionPerformed

    private void txtModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtModeloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtModeloActionPerformed

    private void btnBuscarVehiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarVehiculoActionPerformed
        String placaBuscar = JOptionPane.showInputDialog(this, "Ingrese la placa del vehículo a buscar:", "Buscar Vehículo", JOptionPane.QUESTION_MESSAGE);
        if (placaBuscar == null) return; // Cancelado

        placaBuscar = placaBuscar.toUpperCase().trim();
        String mensajeValidacion = controlador.validarPlacaBusqueda(placaBuscar);
        if (!mensajeValidacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensajeValidacion, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Usar el procedimiento almacenado sp_bucar_veghiculo_po_placa
            ResultSet rs = controlador.buscarVehiculoPorPlacaSP(placaBuscar);
            if (rs.next()) {
                // Cargar datos en el formulario
                comboMarca.setSelectedItem(rs.getString("marca"));
                txtModelo.setText(rs.getString("modelo"));
                txtAño.setText(String.valueOf(rs.getInt("anio")));
                txtPlaca.setText(rs.getString("placa"));
                tDetalles.setText(rs.getString("detalle"));
                SelectEstado.setSelectedIndex(rs.getInt("estado") - 1);

                // Deshabilitar campos que no deben modificarse
                txtPlaca.setEnabled(false);
                btnAgregar.setEnabled(false);
                btnActualizarVehiculo.setEnabled(true);

                JOptionPane.showMessageDialog(this,
                    "Vehículo encontrado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró un vehículo con la placa especificada",
                    "No encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar el vehículo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarVehiculoActionPerformed

    private void btnActualizarVehiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarVehiculoActionPerformed
        btnActualizarVehiculo.setText("Actualizando...");
        btnActualizarVehiculo.setEnabled(false);

        modelo = txtModelo.getText().toUpperCase();
        año = txtAño.getText();
        marca = comboMarca.getSelectedItem().toString();
        detalles = tDetalles.getText();

        if (!controlador.validarCampos(marca, modelo, año, txtPlaca.getText())) {
            String mensaje = controlador.mensajeValidacion(marca, modelo, año, txtPlaca.getText());
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            btnActualizarVehiculo.setText("Actualizar Vehiculo");
            btnActualizarVehiculo.setEnabled(true);
            return;
        }

        try {
            double costo = 0;
            double precio = 0;
            boolean costosValidos = false;
            int estado = SelectEstado.getSelectedIndex() + 1;

            while (!costosValidos) {
                try {
                    String costoStr = JOptionPane.showInputDialog(this, 
                        "Ingrese el costo de compra del vehículo:", 
                        "Costo", JOptionPane.QUESTION_MESSAGE);

                    if (costoStr == null) {
                        btnActualizarVehiculo.setText("Actualizar Vehiculo");
                        btnActualizarVehiculo.setEnabled(true);
                        return;
                    }

                    String precioStr = JOptionPane.showInputDialog(this,
                        "Ingrese el precio de venta esperado:",
                        "Precio", JOptionPane.QUESTION_MESSAGE);

                    if (precioStr == null) {
                        btnActualizarVehiculo.setText("Actualizar Vehiculo");
                        btnActualizarVehiculo.setEnabled(true);
                        return;
                    }

                    costo = Double.parseDouble(costoStr);
                    precio = Double.parseDouble(precioStr);

                    String mensajeCostos = controlador.validarCostos(costo, precio);
                    if (!mensajeCostos.isEmpty()) {
                        JOptionPane.showMessageDialog(this, mensajeCostos, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        costosValidos = true;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese valores numéricos válidos",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            int resultado = controlador.actualizarVehiculo(
                txtPlaca.getText(),
                marca,
                modelo,
                Integer.parseInt(año),
                costo,
                precio,
                estado,
                detalles
            );

            if (resultado > 0) {
                JOptionPane.showMessageDialog(this,
                    "Vehículo actualizado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Habilitar campos y botones
                txtPlaca.setEnabled(true);
                btnAgregar.setEnabled(true);
                btnActualizarVehiculo.setEnabled(false);

                limpiarCampos();
                cargarVehiculos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el vehículo",
                    "Error", JOptionPane.ERROR_MESSAGE);
                btnActualizarVehiculo.setText("Actualizar Vehiculo");
                btnActualizarVehiculo.setEnabled(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar el vehículo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            btnActualizarVehiculo.setText("Actualizar Vehiculo");
            btnActualizarVehiculo.setEnabled(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese valores numéricos válidos para el año",
                "Error", JOptionPane.ERROR_MESSAGE);
            btnActualizarVehiculo.setText("Actualizar Vehiculo");
            btnActualizarVehiculo.setEnabled(true);
        }

        btnActualizarVehiculo.setText("Actualizar Vehiculo");
        btnActualizarVehiculo.setEnabled(true);
        limpiarCampos();
        cargarVehiculos();
    }//GEN-LAST:event_btnActualizarVehiculoActionPerformed

    private void btnMenuClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuClienteActionPerformed
        javax.swing.JFrame frame = new javax.swing.JFrame("Registro de Cliente");
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        ModalCliente modalCliente = new ModalCliente();
        frame.getContentPane().add(modalCliente);
        frame.setSize(680, 420);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//GEN-LAST:event_btnMenuClienteActionPerformed

    private void btnModuloVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModuloVentasActionPerformed
        // Crear y mostrar el modal MenuVenta}
        System.err.println("Modulo de ventas");
        javax.swing.JFrame dialog = new javax.swing.JFrame("Módulo de Ventas");
        dialog.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        MenuVenta mv = new MenuVenta();
        dialog.getContentPane().add(mv);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this); // null
        dialog.setVisible(true);
    }//GEN-LAST:event_btnModuloVentasActionPerformed



    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NUsuario;
    private javax.swing.JComboBox<String> SelectEstado;
    private javax.swing.JButton btnActualizarVehiculo;
    private javax.swing.JToggleButton btnAgregar;
    private javax.swing.JButton btnBuscarVehiculo;
    private javax.swing.JButton btnMenuCliente;
    private javax.swing.JButton btnModuloVentas;
    private javax.swing.JComboBox<String> comboMarca;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea tDetalles;
    private javax.swing.JTable tbInfo;
    private javax.swing.JTextField txtAño;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables


}

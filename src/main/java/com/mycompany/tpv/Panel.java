package com.mycompany.tpv;

import com.itextpdf.html2pdf.HtmlConverter;
import componentes.PanelCategoria;
import componentes.Producto;
import componentes.Categoria;
import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Equipo
 */
public class Panel extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Panel.class.getName());

    /**
     * Creates new form NewJFrame
     */
    public static String[] columnas = {"Cantidad", "Producto", "Precio €", "Total €"};
    public static DefaultTableModel modelo = new DefaultTableModel(null, columnas);

    public Panel() {
        initComponents();
        
        // ═══════════════════════════════════════════════════════════════
        // CONFIGURACIÓN DE TAMAÑO INICIAL Y ADAPTABILIDAD
        // ═══════════════════════════════════════════════════════════════
        
        // Establecer tamaño inicial más grande y responsivo
        this.setSize(1200, 700);
        this.setMinimumSize(new Dimension(1000, 650));
        this.setLocationRelativeTo(null);
        
        // Hacer que la ventana sea maximizable y redimensionable
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        // ═══════════════════════════════════════════════════════════════
        // CONFIGURACIÓN DEL PANEL 8 CON LAYOUT RESPONSIVO
        // ═══════════════════════════════════════════════════════════════
        
        jPanel3.setPreferredSize(new Dimension(280, 402));
        jPanel8.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        
        // Configurar fuente más grande para mejor visualización
        java.awt.Font fuenteGrande = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28);
        jLabel1.setFont(fuenteGrande);
        jLabel2.setFont(fuenteGrande);
        
        // Panel contenedor para los labels (Total y valor)
        javax.swing.JPanel panelTotal = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        panelTotal.setOpaque(false);
        panelTotal.add(jLabel1);
        panelTotal.add(jLabel2);
        
        // Agregar panel de total
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(15, 10, 10, 10);
        jPanel8.add(panelTotal, gbc);
        
        // Configurar tamaño de botones más grandes
        java.awt.Dimension tamañoBoton = new java.awt.Dimension(160, 45);
        jButton3.setPreferredSize(tamañoBoton);
        jButton3.setMinimumSize(tamañoBoton);
        jButton4.setPreferredSize(tamañoBoton);
        jButton4.setMinimumSize(tamañoBoton);
        
        // Agregar botón Cobrar
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(10, 10, 15, 5);
        jPanel8.add(jButton3, gbc);
        
        // Agregar botón Ver Facturas
        gbc.gridx = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(10, 5, 15, 10);
        jPanel8.add(jButton4, gbc);
        
        // ═══════════════════════════════════════════════════════════════
        // LISTENER PARA ADAPTAR LA INTERFAZ AL REDIMENSIONAR
        // ═══════════════════════════════════════════════════════════════
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adaptarInterfazATamaño();
            }
        });
        jTable1.setModel(modelo);
        
        // ═══════════════════════════════════════════════════════════════
        // APLICAR PALETA DE COLORES PROFESIONAL
        // ═══════════════════════════════════════════════════════════════
        aplicarColoresProfesionales();
        
        // Cargar categorías
        List <Categoria> categorias = categoriaxml.cargarCategorias();
        for (int i = 0; i < categorias.size(); i++){
            jPanel7.add(categorias.get(i));
            jPanel7.add(Box.createRigidArea(new Dimension(0,10)));
        }
        Panel.jPanel7.revalidate();
        Panel.jPanel7.repaint();
        
        // Cargar productos
        productoxml.cargarProductos();
        cargarProductosEnUI();
    }
    
    /**
     * Adapta elementos de la interfaz según el tamaño de la ventana
     */
    private void adaptarInterfazATamaño() {
        int anchoVentana = this.getWidth();
        int altoVentana = this.getHeight();
        
        // Adaptar tamaño de fuentes según el tamaño de ventana
        int tamañoFuenteTotal = Math.max(20, Math.min(32, anchoVentana / 40));
        jLabel1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, tamañoFuenteTotal));
        jLabel2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, tamañoFuenteTotal));
        
        // Adaptar tamaño de botones
        int tamañoBotonAncho = Math.max(140, Math.min(180, anchoVentana / 8));
        int tamañoBotonAlto = Math.max(40, Math.min(50, altoVentana / 20));
        java.awt.Dimension nuevoTamañoBoton = new java.awt.Dimension(tamañoBotonAncho, tamañoBotonAlto);
        
        jButton3.setPreferredSize(nuevoTamañoBoton);
        jButton4.setPreferredSize(nuevoTamañoBoton);
        
        // Adaptar ancho del panel derecho proporcionalmente
        int anchoPanelDerecho = Math.max(250, Math.min(350, anchoVentana / 4));
        jPanel3.setPreferredSize(new Dimension(anchoPanelDerecho, jPanel3.getHeight()));
        
        // Revalidar para aplicar cambios
        jPanel8.revalidate();
        jPanel8.repaint();
        jPanel3.revalidate();
        jPanel3.repaint();
    }
    
    /**
     * Aplica una paleta de colores moderna y profesional a la interfaz
     * Paleta: Azul oscuro profesional con acentos verdes
     */
    private void aplicarColoresProfesionales() {
        // Colores profesionales
        java.awt.Color azulOscuro = new java.awt.Color(31, 41, 55);      // Fondo principal oscuro
        java.awt.Color azulMedio = new java.awt.Color(55, 65, 81);       // Fondo secundario
        java.awt.Color verdeAcento = new java.awt.Color(16, 185, 129);   // Verde acento moderno
        java.awt.Color grisClaro = new java.awt.Color(243, 244, 246);    // Fondo claro
        java.awt.Color textoOscuro = new java.awt.Color(17, 24, 39);     // Texto oscuro
        java.awt.Color blancoTexto = new java.awt.Color(255, 255, 255);  // Texto blanco
        
        // Panel izquierdo (categorías) - Azul oscuro
        jPanel2.setBackground(azulOscuro);
        
        // Panel de botones - Azul medio
        jPanel4.setBackground(azulMedio);
        
        // Botones con estilo moderno
        estilizarBoton(jButton1, verdeAcento, blancoTexto);
        estilizarBoton(jButton2, verdeAcento, blancoTexto);
        estilizarBoton(jButton3, verdeAcento, blancoTexto);
        estilizarBoton(jButton4, azulMedio, blancoTexto);
        
        // Panel derecho (resumen) - Gris claro
        jPanel3.setBackground(grisClaro);
        jPanel8.setBackground(grisClaro);
        
        // Labels de total con texto oscuro
        jLabel1.setForeground(textoOscuro);
        jLabel2.setForeground(textoOscuro);
        
        // Panel central (productos) - Blanco
        jPanel6.setBackground(java.awt.Color.WHITE);
        
        // Tabla con bordes y colores suaves
        jTable1.setGridColor(new java.awt.Color(229, 231, 235));
        jTable1.setSelectionBackground(new java.awt.Color(191, 219, 254));
        jTable1.getTableHeader().setBackground(azulMedio);
        jTable1.getTableHeader().setForeground(blancoTexto);
    }
    
    /**
     * Estiliza un botón con colores personalizados
     */
    private void estilizarBoton(javax.swing.JButton boton, java.awt.Color fondo, java.awt.Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    
    private void cargarProductosEnUI() {
        try {
            for (String categoria : productoxml.productosPorCategoria.keySet()) {
                List<productoxml.ProductoData> productos = productoxml.productosPorCategoria.get(categoria);
                
                // Buscar el PanelCategoria correspondiente
                for (int i = 0; i < jPanel6.getComponentCount(); i++) {
                    if (jPanel6.getComponent(i) instanceof PanelCategoria) {
                        PanelCategoria pc = (PanelCategoria) jPanel6.getComponent(i);
                        if (pc.getName() != null && pc.getName().equals(categoria)) {
                            // Añadir productos a este panel
                            for (productoxml.ProductoData prodData : productos) {
                                Producto p = new Producto(prodData.nombre, prodData.precio);
                                
                                // Cargar imagen si existe (resolviendo ruta relativa con AppRutas)
                                if (prodData.rutaImagen != null && !prodData.rutaImagen.isEmpty()) {
                                    try {
                                        String rutaCompleta = productoxml.obtenerRutaAbsoluta(prodData.rutaImagen);
                                        File imgFile = new File(rutaCompleta);
                                        if (imgFile.exists()) {
                                            ImageIcon imagen = new ImageIcon(rutaCompleta);
                                            // Usar tamaño fijo en vez de getWidth/getHeight que pueden ser 0
                                            Image imagenEscalada = imagen.getImage().getScaledInstance(
                                                120,  // Ancho fijo
                                                100,  // Alto fijo
                                                Image.SCALE_SMOOTH
                                            );
                                            p.jLabel1.setIcon(new ImageIcon(imagenEscalada));
                                            p.jLabel1.setText("");
                                        } else {
                                            System.err.println("Imagen no encontrada: " + rutaCompleta);
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Error al cargar imagen: " + e.getMessage());
                                    }
                                }
                                
                                pc.jPanel1.add(p);
                            }
                            pc.jPanel1.revalidate();
                            pc.jPanel1.repaint();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos en UI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static String categoriaActual;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: DO NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(0, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(112, 402));
        jPanel2.setMinimumSize(new java.awt.Dimension(112, 402));
        jPanel2.setPreferredSize(new java.awt.Dimension(130, 402));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 0, 51));
        jPanel4.setMaximumSize(new java.awt.Dimension(130, 100));
        jPanel4.setMinimumSize(new java.awt.Dimension(130, 100));

        jButton1.setText("Categoría");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Producto");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jPanel5.setLayout(new java.awt.CardLayout());

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel7);

        jPanel5.add(jScrollPane1, "card2");

        jPanel2.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        jPanel3.setBackground(new java.awt.Color(255, 255, 0));
        jPanel3.setMaximumSize(new java.awt.Dimension(150, 402));
        jPanel3.setMinimumSize(new java.awt.Dimension(150, 402));
        jPanel3.setPreferredSize(new java.awt.Dimension(150, 402));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(0, 255, 0));
        jPanel8.setMaximumSize(new java.awt.Dimension(150, 130));
        jPanel8.setMinimumSize(new java.awt.Dimension(150, 130));
        jPanel8.setPreferredSize(new java.awt.Dimension(150, 130));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Total:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("0");

        jButton3.setText("Cobrar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4 = new javax.swing.JButton();
        jButton4.setText("Historial");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(10, 10, 10)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jPanel3.add(jPanel8, java.awt.BorderLayout.PAGE_END);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, java.awt.BorderLayout.LINE_END);

        jPanel6.setMaximumSize(new java.awt.Dimension(311, 402));
        jPanel6.setMinimumSize(new java.awt.Dimension(311, 402));
        jPanel6.setPreferredSize(new java.awt.Dimension(311, 402));
        jPanel6.setLayout(new javax.swing.OverlayLayout(jPanel6));
        jPanel1.add(jPanel6, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        formulario f = new formulario(this, true);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (Panel.categoriaActual == null || Panel.categoriaActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona primero una categoría", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FormularioProducto fp = new FormularioProducto(this, true);
        fp.setLocationRelativeTo(null);
        fp.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la lista para cobrar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generar nombre único para la factura con timestamp
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new java.util.Date());
        String nombreFactura = "Factura_" + timestamp + ".pdf";
        
        String rutaHTML = AppRutas.ruta("temp_" + timestamp + ".html");
        String rutaPDF  = AppRutas.ruta(nombreFactura);

        InsertDataHTML(rutaHTML);
        ExportPDF(rutaHTML, rutaPDF);
        
        // Eliminar archivo HTML temporal
        new java.io.File(rutaHTML).delete();
        
        // Limpiar la tabla después de cobrar
        modelo.setRowCount(0);
        jLabel2.setText("0");
    }//GEN-LAST:event_jButton3ActionPerformed
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        // Abrir ventana de historial de facturas
        VisorFacturas visor = new VisorFacturas(this);
        visor.setVisible(true);
    }

    /**
     * Genera (o sobreescribe) el HTML de la factura con los datos actuales de la tabla.
     * El template se crea desde cero cada vez → nunca queda basura de pedidos anteriores.
     */
    public void InsertDataHTML(String htmlFilePath) {
        StringBuilder html = new StringBuilder(2048);
        
        // Plantilla HTML con estilos CSS
        html.append("<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>Factura de Pedido</title><style>")
            .append("*{box-sizing:border-box;margin:0;padding:0}")
            .append("body{font-family:Arial,Helvetica,sans-serif;margin:40px;color:#222}")
            .append("h1{text-align:center;margin-bottom:30px;font-size:24px;letter-spacing:2px;text-transform:uppercase}")
            .append("table{width:100%;border-collapse:collapse;margin-top:10px}")
            .append("th,td{border:1px solid #aaa;padding:10px 12px;text-align:center}")
            .append("th{background-color:#f2f2f2;font-weight:bold}")
            .append("tr:nth-child(even){background-color:#fafafa}")
            .append(".total-row{font-weight:bold;background-color:#d9ffd9!important}")
            .append("</style></head><body><h1>Factura de Pedido</h1><table><tr>");

        // Cabecera de tabla
        for (int j = 0; j < modelo.getColumnCount(); j++) {
            html.append("<th>").append(modelo.getColumnName(j)).append("</th>");
        }
        html.append("</tr>");

        // Filas de datos y cálculo de total
        double total = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            html.append("<tr>");
            for (int j = 0; j < modelo.getColumnCount(); j++) {
                String value = modelo.getValueAt(i, j).toString();
                html.append("<td>").append(value).append("</td>");
                if (j == 3) total += Double.parseDouble(value);
            }
            html.append("</tr>");
        }

        // Fila de total y cierre
        html.append("<tr class=\"total-row\"><td colspan=\"3\">TOTAL</td><td>")
            .append(String.format("%.2f", total))
            .append(" €</td></tr></table></body></html>");

        // Escribir archivo con try-with-resources
        try (FileWriter writer = new FileWriter(htmlFilePath)) {
            writer.write(html.toString());
            System.out.println("HTML generado correctamente en: " + htmlFilePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar HTML: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Error generando HTML", e);
        }
    }
    
    public void ExportPDF(String htmlFilePath, String pdfFilePath){
        try (FileInputStream inputStream = new FileInputStream(htmlFilePath);
             FileOutputStream outputStream = new FileOutputStream(pdfFilePath)) {
            HtmlConverter.convertToPdf(inputStream, outputStream);
            JOptionPane.showMessageDialog(null, "Factura generada correctamente en: " + pdfFilePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Error generando PDF", e);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public static javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public static javax.swing.JPanel jPanel5;
    public static javax.swing.JPanel jPanel6;
    public static javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

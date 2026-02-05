package com.mycompany.tpv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Ventana para visualizar el historial de facturas generadas
 * @author Equipo
 */
public class VisorFacturas extends JDialog {
    
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private static final Color AZUL_OSCURO = new Color(31, 41, 55);
    private static final Color AZUL_MEDIO = new Color(55, 65, 81);
    private static final Color VERDE_ACENTO = new Color(16, 185, 129);
    private static final Color GRIS_CLARO = new Color(243, 244, 246);
    private static final Color TEXTO_OSCURO = new Color(17, 24, 39);
    private static final Color BLANCO = new Color(255, 255, 255);
    
    public VisorFacturas(JFrame parent) {
        super(parent, "Historial de Facturas", true);
        initComponents();
        cargarFacturas();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(GRIS_CLARO);
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(AZUL_OSCURO);
        panelTitulo.setPreferredSize(new Dimension(800, 70));
        
        JLabel lblTitulo = new JLabel("Historial de Facturas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(BLANCO);
        panelTitulo.add(lblTitulo);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Tabla de facturas
        String[] columnas = {"Nombre Archivo", "Fecha de Creación", "Tamaño", "Ruta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        
        tablaFacturas = new JTable(modeloTabla);
        tablaFacturas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaFacturas.setRowHeight(40);
        tablaFacturas.setSelectionBackground(new Color(191, 219, 254));
        tablaFacturas.setSelectionForeground(TEXTO_OSCURO);
        tablaFacturas.setGridColor(new Color(229, 231, 235));
        tablaFacturas.setShowGrid(true);
        
        // Ocultar columna de ruta pero mantener los datos
        tablaFacturas.getColumnModel().getColumn(3).setMinWidth(0);
        tablaFacturas.getColumnModel().getColumn(3).setMaxWidth(0);
        tablaFacturas.getColumnModel().getColumn(3).setWidth(0);
        
        // Estilizar encabezado
        JTableHeader header = tablaFacturas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(AZUL_MEDIO);
        header.setForeground(BLANCO);
        header.setPreferredSize(new Dimension(100, 45));
        
        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 3; i++) {
            tablaFacturas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(BLANCO);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(GRIS_CLARO);
        panelBotones.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelBotones.setLayout(new GridLayout(1, 3, 10, 0));
        
        JButton btnAbrir = crearBoton("Abrir Factura", VERDE_ACENTO);
        btnAbrir.addActionListener(e -> abrirFacturaSeleccionada());
        
        JButton btnEliminar = crearBoton("Eliminar", new Color(239, 68, 68));
        btnEliminar.addActionListener(e -> eliminarFacturaSeleccionada());
        
        JButton btnCerrar = crearBoton("Cerrar", AZUL_MEDIO);
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnAbrir);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(BLANCO);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(150, 45));
        return boton;
    }
    
    private void cargarFacturas() {
        modeloTabla.setRowCount(0);
        
        File directorioFacturas = AppRutas.carpetaBase();
        if (!directorioFacturas.exists() || !directorioFacturas.isDirectory()) {
            return;
        }
        
        // Buscar y ordenar PDFs por fecha (más reciente primero)
        List<File> facturas = new ArrayList<>();
        buscarPDFs(directorioFacturas, facturas);
        facturas.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        // Añadir a la tabla
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (File factura : facturas) {
            String nombre = factura.getName();
            String fecha = sdf.format(new Date(factura.lastModified()));
            String tamano = formatearTamano(factura.length());
            String ruta = factura.getAbsolutePath();
            
            modeloTabla.addRow(new Object[]{nombre, fecha, tamano, ruta});
        }
    }
    
    private void buscarPDFs(File directorio, List<File> facturas) {
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().toLowerCase().endsWith(".pdf")) {
                    facturas.add(archivo);
                } else if (archivo.isDirectory()) {
                    buscarPDFs(archivo, facturas);
                }
            }
        }
    }
    
    private String formatearTamano(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private void abrirFacturaSeleccionada() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona una factura de la lista", 
                "Ninguna selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ruta = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
        File archivo = new File(ruta);
        
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, 
                "El archivo no existe o ha sido movido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            cargarFacturas(); // Recargar lista
            return;
        }
        
        try {
            Desktop.getDesktop().open(archivo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al abrir el archivo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarFacturaSeleccionada() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona una factura de la lista", 
                "Ninguna selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro de que quieres eliminar la factura '" + nombre + "'?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String ruta = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            File archivo = new File(ruta);
            
            if (archivo.delete()) {
                JOptionPane.showMessageDialog(this, 
                    "Factura eliminada correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarFacturas(); // Recargar lista
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo eliminar el archivo", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

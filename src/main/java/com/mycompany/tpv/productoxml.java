package com.mycompany.tpv;

import componentes.Producto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class productoxml {
    
    // Mapa para guardar productos por categoría
    public static Map<String, List<ProductoData>> productosPorCategoria = new HashMap<>();

    // ── Ruta estable: usa AppRutas en vez de user.dir ──────────────────────
    private static final String FILE_PATH         = AppRutas.ruta("productos.xml");
    private static final String CARPETA_IMAGENES  = "imagenes";   // nombre lógico (sin separador)
    
    // Clase interna para guardar datos del producto
    public static class ProductoData {
        public String nombre;
        public float precio;
        public String rutaImagen;   // siempre con '/' → portable entre equipos
        
        public ProductoData(String nombre, float precio, String rutaImagen) {
            this.nombre = nombre;
            this.precio = precio;
            this.rutaImagen = rutaImagen;
        }
    }

    /** Asegura que la carpeta "imagenes" exista dentro de la carpeta base de la app. */
    private static void crearCarpetaImagenes() {
        File carpeta = new File(AppRutas.carpetaBase(), CARPETA_IMAGENES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
    
    /**
     * Copia una imagen a la carpeta local de la aplicación y devuelve la ruta
     * RELATIVA con separador '/' (portable).
     *   ej: "imagenes/1706123456_foto.png"
     */
    public static String copiarImagenLocal(String rutaOriginal) {
        if (rutaOriginal == null || rutaOriginal.isEmpty()) {
            return "";
        }
        
        try {
            crearCarpetaImagenes();
            File archivoOriginal = new File(rutaOriginal);
            
            if (!archivoOriginal.exists()) {
                System.err.println("Archivo de imagen no existe: " + rutaOriginal);
                return "";
            }
            
            // Generar nombre único para evitar conflictos
            String nombreArchivo = System.currentTimeMillis() + "_" + archivoOriginal.getName();
            File archivoDestino  = new File(AppRutas.carpetaBase(), CARPETA_IMAGENES + File.separator + nombreArchivo);
            
            // Copiar el archivo
            Files.copy(archivoOriginal.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Imagen copiada a: " + archivoDestino.getAbsolutePath());

            // Devolver ruta relativa normalizada con '/'
            return AppRutas.toRelativa(archivoDestino);   // ej: "imagenes/12345_foto.png"
            
        } catch (IOException e) {
            System.err.println("Error al copiar imagen: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Elimina un producto de la estructura de datos, actualiza el XML y borra
     * su imagen asociada del disco.
     * 
     * @param nombreProducto  Nombre del producto a eliminar
     * @param categoria       Categoría a la que pertenece el producto
     * @return true si se eliminó correctamente, false si no se encontró
     */
    public static boolean eliminarProducto(String nombreProducto, String categoria) {
        List<ProductoData> productos = productosPorCategoria.get(categoria);
        if (productos == null) {
            System.err.println("Categoría no encontrada: " + categoria);
            return false;
        }
        
        // Buscar y eliminar el producto
        ProductoData productoAEliminar = productos.stream()
            .filter(prod -> prod.nombre.equals(nombreProducto))
            .findFirst()
            .orElse(null);
        
        if (productoAEliminar == null) {
            System.err.println("Producto no encontrado: " + nombreProducto);
            return false;
        }
        
        // Eliminar la imagen del disco si existe
        if (productoAEliminar.rutaImagen != null && !productoAEliminar.rutaImagen.isEmpty()) {
            try {
                File archivoImagen = new File(obtenerRutaAbsoluta(productoAEliminar.rutaImagen));
                if (archivoImagen.exists() && !archivoImagen.delete()) {
                    System.err.println("No se pudo eliminar la imagen: " + archivoImagen.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error al eliminar imagen: " + e.getMessage());
            }
        }
        
        // Eliminar de la estructura de datos
        productos.remove(productoAEliminar);
        
        // Si la categoría quedó vacía, eliminarla del mapa
        if (productos.isEmpty()) {
            productosPorCategoria.remove(categoria);
        }
        
        // Guardar cambios en el XML
        guardarProductos();
        
        System.out.println("Producto eliminado correctamente: " + nombreProducto);
        return true;
    }
    
    public static void guardarProductos() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            Element root = doc.createElement("productos");
            doc.appendChild(root);
            
            // Recorrer todas las categorías y sus productos
            for (Map.Entry<String, List<ProductoData>> entry : productosPorCategoria.entrySet()) {
                String categoria = entry.getKey();
                for (ProductoData prod : entry.getValue()) {
                    Element productoElement = doc.createElement("producto");
                    productoElement.setAttribute("categoria", categoria);
                    productoElement.setAttribute("nombre", prod.nombre);
                    productoElement.setAttribute("precio", String.valueOf(prod.precio));
                    if (prod.rutaImagen != null && !prod.rutaImagen.isEmpty()) {
                        productoElement.setAttribute("imagen", prod.rutaImagen);
                    }
                    root.appendChild(productoElement);
                }
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(FILE_PATH)));
            
            System.out.println("Productos guardados en: " + FILE_PATH);
        } catch (Exception e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void cargarProductos() {
        try {
            productosPorCategoria.clear();
            File file = new File(FILE_PATH);
            
            if (!file.exists()) {
                System.out.println("El archivo de productos no existe. Retornando mapa vacío.");
                return;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList productoNodes = doc.getElementsByTagName("producto");
            
            for (int i = 0; i < productoNodes.getLength(); i++) {
                Node node = productoNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element productoElement = (Element) node;
                    String categoria   = productoElement.getAttribute("categoria");
                    String nombre      = productoElement.getAttribute("nombre");
                    float  precio      = Float.parseFloat(productoElement.getAttribute("precio"));
                    String rutaImagen  = productoElement.getAttribute("imagen"); // con '/'

                    // Se guarda tal cual (con '/') en memoria; se resuelve al mostrar en UI
                    ProductoData prodData = new ProductoData(nombre, precio, rutaImagen);
                    
                    productosPorCategoria.computeIfAbsent(categoria, k -> new ArrayList<>()).add(prodData);
                }
            }
            
            System.out.println("Productos cargados: " + productosPorCategoria.size() + " categorías");
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convierte la ruta relativa (con '/') guardada en el XML a una ruta
     * absoluta correcta para el SO actual.
     */
    public static String obtenerRutaAbsoluta(String rutaRelativa) {
        return AppRutas.resolverRelativa(rutaRelativa);
    }
}

package com.mycompany.tpv;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase utilitaria centralizada para resolver la carpeta de datos de la aplicación.
 *
 * ¿Por qué no usar System.getProperty("user.dir")?
 *   "user.dir" es el directorio de trabajo del proceso, NO la ubicación del JAR.
 *   Si el usuario ejecuta el JAR desde otra carpeta (ej: arrastrarlo al escritorio
 *   y doble-clicar, o ejecutar desde cmd estando en C:\) "user.dir" apunta ahí,
 *   y los XMLs / imagenes se pierden o no se encuentran.
 *
 * Solución: usar %APPDATA%\tpv (Windows) → ej: C:\Users\Juan\AppData\Roaming\tpv
 *   - Existe en CUALQUIER Windows desde Vista en adelante.
 *   - Cada usuario tiene su propia copia (no necesita permisos de admin).
 *   - No cambia sin importar desde dónde se ejecuta el JAR.
 *   - Fallback para Linux/Mac: ~/.tpv  (System.getProperty("user.home"))
 */
public class AppRutas {

    /** Nombre de la subcarpeta dentro de APPDATA (o home en otros SO). */
    private static final String NOMBRE_APP = "tpv";

    /** Carpeta raíz donde se guardan categorias.xml, productos.xml e imagenes/ */
    private static final File CARPETA_BASE;

    static {
        String appdata = System.getenv("APPDATA");          // solo existe en Windows
        if (appdata != null && !appdata.trim().isEmpty()) {
            CARPETA_BASE = new File(appdata, NOMBRE_APP);   // Windows
        } else {
            // Fallback genérico (Linux / Mac)
            CARPETA_BASE = new File(System.getProperty("user.home"), "." + NOMBRE_APP);
        }
        // Crear la carpeta si no existe aún
        if (!CARPETA_BASE.exists()) {
            CARPETA_BASE.mkdirs();
        }
    }

    private AppRutas() { /* solo métodos estáticos */ }

    /**
     * Devuelve la ruta absoluta de un archivo dentro de la carpeta de datos.
     * @param nombreArchivo  ej: "categorias.xml"  o  "imagenes/foto.png"
     */
    public static String ruta(String nombreArchivo) {
        // Siempre usar File.separator para que funcione en el SO actual
        return new File(CARPETA_BASE, nombreArchivo).getAbsolutePath();
    }

    /**
     * Devuelve la carpeta raíz como File (útil para verificaciones).
     */
    public static File carpetaBase() {
        return CARPETA_BASE;
    }

    /**
     * Convierte una ruta relativa guardada en el XML (siempre con '/')
     * a una ruta absoluta correcta para el SO actual.
     *   ej: "imagenes/12345_foto.png"  →  C:\Users\Juan\AppData\Roaming\tpv\imagenes\foto.png
     */
    public static String resolverRelativa(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.trim().isEmpty()) {
            return null;
        }
        // Normalizar: reemplazar '/' por el separador del SO actual
        String normalizada = rutaRelativa.replace('/', File.separatorChar);
        return new File(CARPETA_BASE, normalizada).getAbsolutePath();
    }

    /**
     * Convierte una ruta absoluta a relativa respecto a la carpeta base,
     * usando siempre '/' como separador (portable entre equipos).
     *   ej: C:\Users\Juan\AppData\Roaming\tpv\imagenes\foto.png  →  imagenes/foto.png
     */
    public static String toRelativa(File archivo) {
        Path base = CARPETA_BASE.toPath();
        Path target = archivo.toPath();
        // torelativize devuelve la ruta relativa usando el separador del SO
        String relativa = base.relativize(target).toString();
        // Normalizar a '/' para que el XML sea portable
        return relativa.replace(File.separatorChar, '/');
    }
}

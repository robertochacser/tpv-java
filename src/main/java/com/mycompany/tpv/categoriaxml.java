package com.mycompany.tpv;

import componentes.PanelCategoria;
import componentes.Categoria;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

/**
 *
 * @author Equipo
 */
public class categoriaxml {

    public static List<Categoria> categorias = new ArrayList<>();

    // ── Ruta estable: usa AppRutas en vez de user.dir ──────────────────────
    public static final String FILE_PATH = AppRutas.ruta("categorias.xml");

    public static void guardarCategorias() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            Element root = doc.createElement("categorias");
            doc.appendChild(root);
            
            for (Categoria categoria : categorias) {
                Element categoriaElement = doc.createElement("categoria");
                categoriaElement.setAttribute("nombre", categoria.getName());
                root.appendChild(categoriaElement);
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(FILE_PATH)));
            
            System.out.println("Categorías guardadas en: " + FILE_PATH);
        } catch (Exception e) {
            System.err.println("Error al guardar categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Categoria> cargarCategorias() {
        categorias.clear();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("El archivo de categorías no existe. Retornando lista vacía.");
            return categorias;
        }
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList categoriaNodes = doc.getElementsByTagName("categoria");
            Random random = new Random();
            
            for (int i = 0; i < categoriaNodes.getLength(); i++) {
                Node node = categoriaNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element categoriaElement = (Element) node;
                    String nombre = categoriaElement.getAttribute("nombre");
                    Categoria nuevaCategoria = new Categoria(nombre);
                    categorias.add(nuevaCategoria);
                    
                    PanelCategoria pc = new PanelCategoria();
                    Panel.jPanel6.add(pc);
                    pc.setName(nombre);
                    pc.jPanel2.setBackground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    Panel.jPanel6.setComponentZOrder(pc, 0);
                    
                    // Actualizar visibilidad de paneles
                    for (int j = 0; j < Panel.jPanel6.getComponentCount(); j++) {
                        if (Panel.jPanel6.getComponent(j) instanceof PanelCategoria) {
                            PanelCategoria panelCat = (PanelCategoria) Panel.jPanel6.getComponent(j);
                            String compName = panelCat.getName();
                            panelCat.setVisible(compName != null && compName.equals(Panel.categoriaActual));
                        }
                    }
                }
            }
            
            Panel.jPanel6.revalidate();
            Panel.jPanel6.repaint();
            
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categorias;
    }
}

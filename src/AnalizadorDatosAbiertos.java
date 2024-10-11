import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorDatosAbiertos {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce la ruta del archivo a analizar:");
        String rutaArchivo = scanner.nextLine();
        if (rutaArchivo.endsWith(".csv")) {
            List<String[]> datosCSV = parsearCSV(rutaArchivo);
            mostrarResumenCSV(datosCSV);
        } else if (rutaArchivo.endsWith(".json")) {
            JsonObject datosJSON = parsearJSON(rutaArchivo);
            mostrarResumenJSON(datosJSON);
        } else if (rutaArchivo.endsWith(".xml")) {
            Document datosXML = parsearXML(rutaArchivo);
            mostrarResumenXML(datosXML);
        } else {
            System.out.println("Formato de archivo no soportado.");
        }
    }

    public static List<String[]> parsearCSV(String rutaArchivo) {
        List<String[]> registros = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new
                FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(",");
                registros.add(valores);
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo CSV: " +
                    e.getMessage());
        }
        return registros;
    }


    public static JsonObject parsearJSON(String rutaArchivo) {
        JsonObject jsonObject = null;
        try (FileReader reader = new FileReader(rutaArchivo)) {
            jsonObject = new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            System.out.println("Error al leer el archivo JSON: " +
                    e.getMessage());
        }
        return jsonObject;
    }

    public static Document parsearXML(String rutaArchivo) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(rutaArchivo);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo XML: " +
                    e.getMessage());
        }
        return doc;
    }

    public static void mostrarResumenCSV(List<String[]> datos) {
        if (datos.isEmpty()) {
            System.out.println("No se encontraron datos.");
            return;
        }
        System.out.println("Resumen del archivo CSV:");
        System.out.println("Número total de filas: " + datos.size());
        System.out.println("Número de columnas: " + datos.get(0).length);
        System.out.println("\nPrimeros 5 registros:");
        for (int i = 0; i < Math.min(5, datos.size()); i++) {
            System.out.println(String.join(" | ", datos.get(i)));
        }
    }

    public static void mostrarResumenJSON(JsonObject datos) {
        if (datos == null || datos.isEmpty()) {
            System.out.println("No se encontraron datos.");
        } else {
            System.out.println("Resumen del archivo JSON:");

            if (datos.has("datos") && datos.get("datos").isJsonArray()) {
                JsonArray array = datos.getAsJsonArray("datos");
                System.out.println("- Número total de elementos en 'datos': " + array.size());

                System.out.println("\n- Primeros 5 objetos en 'datos':");
                int count = 0;
                for (JsonElement elemento : array) {
                    JsonObject objeto = elemento.getAsJsonObject();
                    System.out.println("\n- Objeto nº " + (count + 1) + ":");

                    for (String clave : objeto.keySet()) {
                        System.out.println(clave + ": " + objeto.get(clave));
                    }

                    count++;
                    if (count >= 5) break;
                }
            } else {
                System.out.println("El JSON no contiene un array, mostraré las claves del JSON:");

                for (String clave : datos.keySet()) {
                    System.out.println(clave + ": " + datos.get(clave));
                }
            }
        }
    }


    public static void mostrarResumenXML(Document datos) {
        if (datos == null) {
            System.out.println("No se encontraron datos.");

        } else {
            System.out.println("Resumen del archivo XML:");

            String elementoRaiz = datos.getDocumentElement().getNodeName();
            System.out.println("Elemento raíz: " + elementoRaiz);

            var hijos = datos.getDocumentElement().getChildNodes();
            int numElementosHijos = 0;

            for (int i = 0; i < hijos.getLength(); i++) {
                if (hijos.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    numElementosHijos++;
                }
            }
            System.out.println("Número total de nodos hijos directos: " + numElementosHijos);

            System.out.println("\nPrimeros 5 nodos hijos:");
            int count = 0;
            for (int i = 0; i < hijos.getLength() && count < 5; i++) {
                if (hijos.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element hijoElemento = (org.w3c.dom.Element) hijos.item(i);
                    System.out.println("Nodo hijo: " + hijoElemento.getNodeName());
                    mostrarContenidoHijos(hijoElemento, "  ");
                    count++;
                }
            }
        }
    }

    public static void mostrarContenidoHijos(org.w3c.dom.Element elemento, String indentacion) {
        var hijos = elemento.getChildNodes();

        for (int i = 0; i < hijos.getLength(); i++) {
            if (hijos.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                org.w3c.dom.Element hijo = (org.w3c.dom.Element) hijos.item(i);
                System.out.println(indentacion + "Etiqueta: " + hijo.getNodeName() + " | Contenido: " + hijo.getTextContent());

                mostrarContenidoHijos(hijo, indentacion + "  ");
            }
        }
    }

}

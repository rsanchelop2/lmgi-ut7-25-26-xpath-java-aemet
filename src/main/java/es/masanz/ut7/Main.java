package es.masanz.ut7;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    private static final String URL_AEMET = "https://www.aemet.es/xml/municipios_h/localidad_h_31201.xml";
    private static final String OUTPUT_DIR = "doc/horas";

    public static void main(String[] args) {
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            System.out.println("Obteniendo datos meteorológicos para: " + date);

            Document doc = Jsoup.connect(URL_AEMET).get();

            // Obtener datos del día
            Elements estadosCielo = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/estado_cielo");
            Elements temperaturas = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/temperatura");
            Elements humedades = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/humedad_relativa");

            if (estadosCielo.isEmpty()) {
                System.err.println("No se encontraron datos para la fecha: " + date);
                return;
            }

            // Crear directorio de salida
            Path outputPath = Paths.get(OUTPUT_DIR);
            Files.createDirectories(outputPath);

            // Generar HTML para cada hora
            int count = 0;
            for (Element estado : estadosCielo) {
                String periodo = estado.attr("periodo");
                String descripcion = estado.attr("descripcion");
                String emoji = getWeatherEmoji(descripcion);

                String temperatura = obtenerValor(temperaturas, periodo);
                String humedad = obtenerValor(humedades, periodo);

                String htmlContent = generarHTML(date, periodo, emoji, descripcion, temperatura, humedad);
                String nombreArchivo = date + "_" + periodo + ".html";
                guardarArchivo(outputPath, nombreArchivo, htmlContent);
                System.out.println("✓ Generado: " + nombreArchivo);
                count++;
            }

            System.out.println("\n✓ Total: " + count + " archivos generados en " + OUTPUT_DIR + "/");

        } catch (IOException e) {
            System.err.println("Error al conectar con AEMET: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String obtenerValor(Elements elementos, String periodo) {
        for (Element elem : elementos) {
            if (elem.attr("periodo").equals(periodo)) {
                return elem.text();
            }
        }
        return "N/A";
    }

    private static String generarHTML(String fecha, String hora, String emoji, String descripcion, 
                                       String temperatura, String humedad) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "\t<title>Pronóstico - " + hora + ":00</title>\n" +
                "\t<style>\n" +
                "\t\tbody { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 40px; font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; }\n" +
                "\t\t.card { background: white; border-radius: 16px; padding: 40px; text-align: center; box-shadow: 0 10px 40px rgba(0,0,0,0.3); max-width: 400px; }\n" +
                "\t\t.fecha { font-size: 0.9rem; color: #999; margin-bottom: 10px; }\n" +
                "\t\t.hora { font-size: 2.5rem; font-weight: bold; color: #667eea; margin: 10px 0; }\n" +
                "\t\t.emoji { font-size: 5rem; margin: 20px 0; }\n" +
                "\t\t.temp { font-size: 3rem; font-weight: bold; color: #333; margin: 20px 0; }\n" +
                "\t\t.desc { font-size: 1.1rem; color: #666; margin: 15px 0; }\n" +
                "\t\t.humedad { font-size: 1rem; color: #007bff; margin-top: 20px; background: #f0f8ff; padding: 15px; border-radius: 8px; }\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<div class=\"card\">\n" +
                "\t\t<div class=\"fecha\">" + fecha + "</div>\n" +
                "\t\t<div class=\"hora\">" + hora + ":00</div>\n" +
                "\t\t<div class=\"emoji\">" + emoji + "</div>\n" +
                "\t\t<div class=\"temp\">" + temperatura + "°C</div>\n" +
                "\t\t<div class=\"desc\">" + descripcion + "</div>\n" +
                "\t\t<div class=\"humedad\">💧 Humedad: " + humedad + "%</div>\n" +
                "\t</div>\n" +
                "</body>\n" +
                "</html>";
    }

    private static void guardarArchivo(Path outputPath, String nombreArchivo, String contenido) {
        try {
            Path filePath = outputPath.resolve(nombreArchivo);
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath.toFile())))) {
                writer.print(contenido);
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar " + nombreArchivo + ": " + e.getMessage());
        }
    }

    private static String getWeatherEmoji(String descripcion) {
        descripcion = descripcion.toLowerCase();
        if (descripcion.contains("despejado")) return "☀️";
        if (descripcion.contains("soleado")) return "☀️";
        if (descripcion.contains("poco nuboso")) return "🌤️";
        if (descripcion.contains("nuboso")) return "⛅";
        if (descripcion.contains("muy nuboso")) return "☁️";
        if (descripcion.contains("cubierto")) return "☁️";
        if (descripcion.contains("lluvia") && descripcion.contains("tormenta")) return "⛈️";
        if (descripcion.contains("lluvia")) return "🌧️";
        if (descripcion.contains("nieve")) return "❄️";
        if (descripcion.contains("niebla")) return "🌫️";
        if (descripcion.contains("tormenta")) return "⛈️";
        return "🌤️";
    }

}
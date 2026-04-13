package es.masanz.ut7;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static final String BASE_PATH = "doc/data/";
    public static final String FILE_NAME = "ejemplo.txt";

    public static void main2(String[] args) throws IOException {
        String url = "https://www.aemet.es/xml/municipios_h/localidad_h_31201.xml";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.selectXpath("//div[@id='subtopnav']/a[text()][last()]");
        for (Element element : elements) {
            System.out.println(element.text());
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.aemet.es/xml/municipios_h/localidad_h_31201.xml";
        Document doc = Jsoup.connect(url).get();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int horaActual = LocalTime.now().getHour();
        String periodoHora = String.format("%02d", horaActual);

        // Obtener el estado del cielo para la hora actual
        Elements estadosCielo = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/estado_cielo[@periodo='"+periodoHora+"']");
        Elements temperaturas = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/temperatura[@periodo='"+periodoHora+"']");
        Elements humedades = doc.selectXpath("//prediccion/dia[@fecha='"+date+"']/humedad_relativa[@periodo='"+periodoHora+"']");

        StringBuilder info = new StringBuilder();
        info.append("********** INFO CLIMA PAMPLONA **********\n");
        info.append("Fecha: ").append(date).append(" | Hora: ").append(periodoHora).append(":00\n");

        if (!estadosCielo.isEmpty()) {
            String descripcion = estadosCielo.get(0).attr("descripcion");
            info.append("Estado: ").append(descripcion).append("\n");
        }

        if (!temperaturas.isEmpty()) {
            String temperatura = temperaturas.get(0).text();
            info.append("Temperatura: ").append(temperatura).append("°C\n");
        }

        if (!humedades.isEmpty()) {
            String humedad = humedades.get(0).text();
            info.append("Humedad: ").append(humedad).append("%\n");
        }

        info.append("*****************************************\n");

        System.out.println(info.toString());
        escribir(FILE_NAME, info.toString(), false);
    }

    private static void escribir(String nombreFichero, String texto, boolean mantenerTexto) {
        try {
            String rutaCompleta = BASE_PATH + nombreFichero;
            Files.createDirectories(Paths.get(BASE_PATH));
            try (PrintWriter file = new PrintWriter(new BufferedWriter(new FileWriter(rutaCompleta, mantenerTexto)))) {
                file.print(texto);
                file.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
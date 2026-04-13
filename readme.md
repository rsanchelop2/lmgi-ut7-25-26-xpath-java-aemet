# Ejemplo de Web Scraping con Java y XPath usando Jsoup

Este proyecto contiene ejemplos prácticos de cómo realizar _web scraping_ en Java para extraer información de páginas web y archivos XML utilizando la librería **Jsoup** y consultas **XPath**.

## 1. Configuración del Proyecto

Para poder utilizar Jsoup en un proyecto Maven, es necesario añadir la siguiente dependencia en el archivo `pom.xml`. Esto le indica a Maven que debe descargar y incluir la librería Jsoup para que podamos usar sus clases y métodos.

```xml
<dependencies>
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.22.1</version>
    </dependency>
</dependencies>
```

## 2. Ejemplos de Consultas XPath

La clase `Main.java` contiene dos ejemplos principales que muestran cómo conectarse a una URL, descargar su contenido y extraer datos específicos mediante expresiones XPath.

### Ejemplo 1: Extraer datos de un HTML

En el método `main2`, nos conectamos a una página de **W3Schools** para extraer el texto del último enlace `<a>` que se encuentra dentro de un `<div>` con el `id` "subtopnav".

**Código:**
```java
String url = "https://www.w3schools.com/html/default.asp";
Document doc = Jsoup.connect(url).get();
Elements elements = doc.selectXpath("//div[@id='subtopnav']/a[text()][last()]");
for (Element element : elements) {
    System.out.println(element.text());
}
```

**Análisis de la expresión XPath:** `//div[@id='subtopnav']/a[text()][last()]`
- `//div[@id='subtopnav']`: Selecciona todos los elementos `<div>` del documento que tengan un atributo `id` con el valor "subtopnav".
- `/a`: Selecciona los elementos `<a>` que son hijos directos del `<div>` anterior.
- `[text()]`: Filtra los `<a>` para quedarse solo con aquellos que contienen texto.
- `[last()]`: De los resultados anteriores, selecciona únicamente el último.

### Ejemplo 2: Extraer datos de un XML

En el método `main`, nos conectamos a un archivo XML de la **AEMET** que contiene una predicción meteorológica. El objetivo es obtener la predicción para una fecha específica.

**Código:**
```java
String url = "https://www.aemet.es/xml/municipios_h/localidad_h_31201.xml";
Document doc = Jsoup.connect(url).get();
Elements elements = doc.selectXpath("//prediccion/dia[@fecha='2026-03-30']");
for (Element element : elements) {
    System.out.println(element.text());
}
```

**Análisis de la expresión XPath:** `//prediccion/dia[@fecha='2026-03-30']`
- `//prediccion`: Selecciona todos los nodos `<prediccion>` en el documento.
- `/dia`: Selecciona los nodos `<dia>` que son hijos directos de `<prediccion>`.
- `[@fecha='2026-03-30']`: Filtra los nodos `<dia>` para seleccionar solo aquel cuyo atributo `fecha` sea igual a "2026-03-30".

## 3. Guardar los Resultados en un Archivo

Además de mostrar los datos por consola, el proyecto incluye un método `escribir()` que demuestra cómo guardar la información extraída en un archivo de texto.

**Código de uso:**
```java
escribir(FILE_NAME, elements.text(), true);
```
- El primer parámetro es el nombre del archivo (`ejemplo.txt`).
- El segundo es el contenido a guardar (el texto extraído por la consulta XPath).
- El tercero (`true`) indica que si el archivo ya existe, el nuevo contenido se añadirá al final en lugar de sobrescribirlo.

**Implementación del método:**
```java
private static void escribir(String nombreFichero, String texto, boolean mantenerTexto) {
    PrintWriter file = null;
    try {
        file = new PrintWriter(new BufferedWriter(new FileWriter(BASE_PATH+nombreFichero, mantenerTexto)));
        file.println(texto);
        file.close();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```
Este método gestiona la creación y escritura en un fichero de forma segura, utilizando `PrintWriter` y `BufferedWriter` para un rendimiento eficiente.
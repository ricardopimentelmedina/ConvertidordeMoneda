import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final String API_KEY = "f3996e6c4145dde663e0dd2e"; // Reemplaza con tu clave
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        // Obtener la lista de monedas disponibles (usando una moneda base temporal)
        String urlTemp = BASE_URL + "USD"; // Puedes usar cualquier moneda base aquí
        HttpClient cliente = HttpClient.newHttpClient(); // Declaración de cliente
        HttpRequest solicitudTemp = HttpRequest.newBuilder()
                .uri(URI.create(urlTemp))
                .build();
        HttpResponse<String> respuestaTemp = cliente.send(solicitudTemp, HttpResponse.BodyHandlers.ofString());

        if (respuestaTemp.statusCode() == 200) {
            Gson gson = new Gson();
            JsonObject jsonObjectTemp = gson.fromJson(respuestaTemp.body(), JsonObject.class);
            JsonObject conversionRates = jsonObjectTemp.getAsJsonObject("conversion_rates");
            Set<String> monedas = conversionRates.keySet();

            // Mostrar la lista de monedas al usuario
            System.out.println("Monedas disponibles:");
            for (String moneda : monedas) {
                System.out.println("- " + moneda);
            }
        } else {
            System.err.println("Error al obtener la lista de monedas.");
            return; // Salir del programa si no se pudo obtener la lista
        }

        System.out.print("Ingresa la moneda base (ej. USD): ");
        String monedaBase = scanner.nextLine().toUpperCase();

        System.out.print("Ingresa la moneda destino (ej. EUR): ");
        String monedaDestino = scanner.nextLine().toUpperCase();

        System.out.print("Ingresa la cantidad a convertir: ");
        double cantidad = scanner.nextDouble();

        // Construir la URL de la solicitud
        String url = BASE_URL + monedaBase;

        // Crear la solicitud
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Enviar la solicitud y obtener la respuesta (usando el mismo cliente)
        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        // Parsear la respuesta JSON
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(respuesta.body(), JsonObject.class);

        // Obtener la tasa de cambio
        double tasaCambio = jsonObject.getAsJsonObject("conversion_rates").get(monedaDestino).getAsDouble();

        // Calcular la conversión
        double cantidadConvertida = cantidad * tasaCambio;

        // Mostrar el resultado
        System.out.println(cantidad + " " + monedaBase + " = " + cantidadConvertida + " " + monedaDestino);

        scanner.close();
    }
}
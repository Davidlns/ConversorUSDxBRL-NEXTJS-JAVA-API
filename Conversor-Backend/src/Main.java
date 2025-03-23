import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class Main {
    private static final String API_URL = "https://api.currencylayer.com/live?access_key=45f59bc7cd5c488ed2eb6f852b4c24bf&format=1";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        System.out.println("Servidor iniciado na porta 8080");

        server.createContext("/convert", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Lê o valor enviado pelo frontend
                    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                    String valorReal = reader.readLine();
                    reader.close();

                    try {
                        // Faz requisição à API para obter a cotação
                        URL url = new URL(API_URL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        BufferedReader apiReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = apiReader.readLine()) != null) {
                            response.append(line);
                        }
                        apiReader.close();

                        // Processa a resposta da API
                        String jsonResponse = response.toString();
                        String[] splitResponse = jsonResponse.split("\"USDBRL\":");
                        if (splitResponse.length > 1) {
                            double cotacao = Double.parseDouble(splitResponse[1].split(",")[0]);

                            // Realiza o cálculo do valor em dólares
                            double valorConvertido = Double.parseDouble(valorReal) / cotacao;

                            // Retorna o resultado ao frontend
                            String result = String.format("%.2f", valorConvertido);
                            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                            exchange.sendResponseHeaders(200, result.getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(result.getBytes());
                            os.close();
                        } else {
                            System.out.println("Erro: 'USDBRL' não encontrado na resposta da API.");
                            String errorMessage = "Erro ao buscar cotação.";
                            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                            exchange.sendResponseHeaders(400, errorMessage.length());
                            OutputStream os = exchange.getResponseBody();
                            os.write(errorMessage.getBytes());
                            os.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = "Erro ao processar a requisição.";
                        exchange.sendResponseHeaders(500, errorMessage.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(errorMessage.getBytes());
                        os.close();
                    }
                } else {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(405, -1); // Método não permitido
                }
            }
        });

        server.start();
    }
}
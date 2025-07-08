package code;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class ServerHandler implements HttpHandler {


    private static final String SERVER_MESSAGE = 
        """
            Hello, Welcome to the Meme Database!
               
            The Instructions for the database usage can be found from \"/help\"
        """
    ;


    /**
    * Handles the method of the HTTP request (Available: POST, GET, DELETE)
    *
    * @param  exchange HTTP request hadler
    */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod().toUpperCase();
            
            switch (method) {
                
                // Handle POST requests here (users send this for sending messages)
                case "GET" -> getRequest(exchange);
                
                // Hande unsupported methods
                default -> errorResponse(exchange, 407, "Unsupported method");
            }
        }

		catch (Exception e) {
			errorResponse(exchange, 400, e.getMessage());
		}
	}


	/**
    * Handles the POST method
    *
    * @param  exchange HTTP request hadler
    */
	private void getRequest(HttpExchange exchange) throws IOException {
        byte[] bytes = SERVER_MESSAGE.getBytes(StandardCharsets.UTF_8);

		// Send response to the server
		exchange.sendResponseHeaders(200, bytes.length);
	}


	/**
    * Sends error message to the server
    *
    * @param  exchange HTTP request hadler
    * @param  statusCode HTTP status code of the error
    * @param  message Error message as string
    */
    private void errorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        // Transform message to bytes
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);

        // Send response
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        // Output the response
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
            outputStream.flush();
        }
    }
}

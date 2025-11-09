package code.backend.handlers;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import code.backend.Database;
import code.backend.HttpExchangeMethods;
import code.backend.user.UserAuthenticator;

public class LoginHandler implements HttpHandler {
    

    private final Database database;
    private final Map<String, String> sessions;



    /**
    * Handles users logins
    *
    * @param  authenticator Users authenticator for checking user's authentication
    */
    public LoginHandler(Database database, Map<String, String> sessions) {
        this.database = database;
        this.sessions = sessions;
    }


    
    /**
    * Handles the method of the HTTP request (Available: POST)
    *
    * @param  exchange HTTP request hadler
    */
    @Override
    public void handle(HttpExchange exchange) {
        HttpExchangeMethods exchangeMethods = new HttpExchangeMethods(exchange, "[ERROR] - LOGIN");
    
        try (exchange) {
            String method = exchange.getRequestMethod().toUpperCase();

            switch (method) {

                case "POST" -> postRequest(exchange, exchangeMethods);

                default -> exchangeMethods.errorResponse(405, ": Unsupported user method\n");
            }
        }

        // Internal server error
        catch (Exception e) {
            exchangeMethods.errorResponse(500, e.getMessage());
        }
    }



	/**
    * Handles the POST method
    *
    * @param  exchange HTTP request hadler
    */
	private void postRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods) {
        try {

            // Read user's input
            Headers headers = exchange.getRequestHeaders();
            JSONObject user = new JSONObject(exchangeMethods.getContent(headers));
            String username = user.getString("username");
            String password = user.getString("password");

            // Check user validity
            if (!new UserAuthenticator(database).checkCredentials(username, password)) {
                throw new SecurityException("Invalid credentials");
            }

            // Create credentials cookie
            String sessionId = UUID.randomUUID().toString();

            // Store the cookie
            sessions.put(sessionId, username);
            exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId + "; Path=/; HttpOnly");

            // Send success message
            exchange.sendResponseHeaders(200, -1);
            System.out.println("User authenticated");
        }

        // Invalid user
        catch (SecurityException e) {
            exchangeMethods.errorResponse(401, ": " + e.getMessage());
        }

        // Invalid content type
        catch (IOException e) {
            exchangeMethods.errorResponse(400, ": " + e.getMessage());
        }
    }


}


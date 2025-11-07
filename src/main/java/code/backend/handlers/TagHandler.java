package code.backend.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import code.backend.Database;
import code.backend.HttpExchangeMethods;
import code.backend.Tag;


public class TagHandler implements HttpHandler {    
    
    private final Database database;




    public TagHandler(Database database) {
        this.database = database;
    }




    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpExchangeMethods exchangeMethods = new HttpExchangeMethods(exchange, "[ERROR] - TAG");

        try (exchange) {
            String method = exchange.getRequestMethod().toUpperCase();

            switch (method) {
                case "GET" -> getRequest(exchange, exchangeMethods);

                case "POST" -> postRequest(exchange, exchangeMethods);

                case "DELETE" -> deleteRequest(exchange, exchangeMethods);
                
                default -> exchangeMethods.errorResponse(405, ": Unsupported post method\n");
            }
        }

        catch (Exception e) {
            exchangeMethods.errorResponse(500, e.getMessage());
        } 
    }




// ▛               ▜
//    GET Request 
// ▙               ▟


    private void getRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods) {
        try {
            JSONArray tags = database.getTagArray();

            // Send the meme paths
            byte[] memeBytes = tags.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, memeBytes.length);
        }

        catch (IOException | SQLException e) {
            exchangeMethods.errorResponse(406, e.getMessage());
        }

        catch (JSONException e) {
            exchangeMethods.errorResponse(405, ": " + e.getMessage());
        }

    }




// ▛               ▜
//    POST Request 
// ▙               ▟


    private void postRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods) {
        try {

            // Content validity check
            Headers headers = exchange.getRequestHeaders();
            String content = exchangeMethods.getContent(headers);

            // Get the tags from the content
            JSONArray tags = new JSONArray(content);

            // Iterate all tags
            for (int i = 0; i < tags.length(); i++) {
                // Parse the tag
                String title = tags.getString(i);
                Tag tag = new Tag(title, 0);

                // Add the tag (if new)
                database.addNewTag(tag);
            }

            // Send success message
            exchange.sendResponseHeaders(200, -1);
            System.out.println("Tags added succesfully\n");
        } 
        
        catch (IOException | IllegalArgumentException | SQLException e) {
            exchangeMethods.errorResponse(406, e.getMessage());
        } 
        
        catch (JSONException e) {
            exchangeMethods.errorResponse(405, ": " + e.getMessage());
        }
    }




// ▛                  ▜
//    DELETE Request 
// ▙                  ▟


    private void deleteRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods) {
        try {
             // Content validity check
            Headers headers = exchange.getRequestHeaders();
            String content = exchangeMethods.getContent(headers);

            // Get the tags from the content
            JSONArray tags = new JSONArray(content);

            // Iterate all tags
            for (int i = 0; i < tags.length(); i++) {
                // Parse the tag
                String title = tags.getString(i);
                Tag tag = new Tag(title, 0);

                // Delete the tag (if new)
                database.deleteTag(tag.getTitle());
            }
        }

        catch (IOException | SQLException e) {
            exchangeMethods.errorResponse(406, ": " + e.getMessage());
        }

        catch (JSONException e) {
            exchangeMethods.errorResponse(405, e.getMessage());
        }
    }

   
}

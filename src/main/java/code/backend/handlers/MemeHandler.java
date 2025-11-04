package code.backend.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.MultipartStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import code.backend.Database;
import code.backend.HttpExchangeMethods;
import code.backend.Meme;


public class MemeHandler implements HttpHandler {

    private final Database database;




    public MemeHandler(Database database) {
        this.database = database;
    }




    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpExchangeMethods exchangeMethods = new HttpExchangeMethods(exchange, "[ERROR] - POST");

        try (exchange) {
            String username = exchangeMethods.checkUserValidity();
            String method = exchange.getRequestMethod().toUpperCase();

            switch (method) {
                case "POST" -> postRequest(exchange, exchangeMethods, username);

                case "PUT" -> putRequest(exchange, exchangeMethods, username);

                case "DELETE" -> deleteRequest(exchange, exchangeMethods, username);
                
                default -> exchangeMethods.errorResponse(405, ": Unsupported post method\n");
            }
        }

        catch (Exception e) {
            exchangeMethods.errorResponse(500, e.getMessage());
        } 
    }





// ▛               ▜
//    POST Request 
// ▙               ▟



    private void postRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods, String username) {
        try {

            // Content validity check
            Headers headers = exchange.getRequestHeaders();
            MultipartStream multipartStream = exchangeMethods.getMultipartContent(headers);

            // Get the meme from the content
            Meme meme = parseMultipartStream(multipartStream);

            // Add meme if its new one
            database.addMeme(meme, username);

            // Send success message
            exchange.sendResponseHeaders(200, -1);
            System.out.println("Meme added succesfully\n");
        } 
        
        catch (IOException | IllegalArgumentException | SQLException e) {
            exchangeMethods.errorResponse(406, e.getMessage());
        } 
        
        catch (JSONException e) {
            exchangeMethods.errorResponse(405, ": " + e.getMessage());
        }
    }


    private Meme parseMultipartStream(MultipartStream multipartStream) throws IOException {
        Meme meme = null;
        boolean next = multipartStream.skipPreamble();

        while (next) {
            
            // Get the data
            String headers = multipartStream.readHeaders();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            multipartStream.readBodyData(output);
            byte[] data = output.toByteArray();

            // Get meme information
            if (headers.contains("application/json")) {
                meme = new Meme(getMemeJson(data));
            }

            // Get the meme file and save it
            else if (headers.contains("image/")) {
                saveMemeFile(data, meme);
            }

            next = multipartStream.readBoundary();
        }

        // Check the stream validity to prevent future errors
        if (meme == null) {
            throw new IllegalArgumentException("The stream must include meme information json and the meme image file.");
        }

        return meme;
    }


    private JSONObject getMemeJson(byte[] memeJsonBytes) {
        String memeJsonString = new String(memeJsonBytes, StandardCharsets.UTF_8);
        return new JSONObject(memeJsonString);
    }


    private void saveMemeFile(byte[] memeFileBytes, Meme meme) throws IOException {
        if (meme == null) {
            throw new IllegalArgumentException("The stream must have the meme information json given first.");
        }

        // Chech image validity
        if (!isValidImageFile(memeFileBytes)) {
            throw new IllegalArgumentException("The given file must be a image file");
        }

        // Save the meme
        try (FileOutputStream stream = new FileOutputStream(meme.getTitle())) {
            stream.write(memeFileBytes);
        }
    }


    private boolean isValidImageFile(byte[] file) throws IOException {
        try (InputStream stream = new ByteArrayInputStream(file)) {
            BufferedImage image = ImageIO.read(stream);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }




// ▛              ▜
//    PUT Request 
// ▙              ▟



    private void putRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods, String username) {
         try {
            // Content validity check
            Headers headers = exchange.getRequestHeaders();
            String content = exchangeMethods.getContent(headers);

            // Get the meme from the content
            JSONObject memeJson = new JSONObject(content);
            Meme meme = new Meme(memeJson);

            // Edit the meme if it exists
            database.editMeme(meme, memeJson.optString("newTitle", null), username);

            // Send success message
            exchange.sendResponseHeaders(200, -1);
            System.out.println("Meme added succesfully");
        } 
        
        catch (IOException | SQLException e) {
            exchangeMethods.errorResponse(406, ": " + e.getMessage());
        }

        catch (IllegalArgumentException e) {
            exchangeMethods.errorResponse(406, e.getMessage());
        }
        
        catch (JSONException e) {
            exchangeMethods.errorResponse(405, e.getMessage());
        }
    }




// ▛                  ▜
//    DELETE Request 
// ▙                  ▟



    private void deleteRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods, String username) {
        try {
            //Content validity check
            Headers headers = exchange.getRequestHeaders();
            String content = exchangeMethods.getContent(headers);

            // Get meme and/or tag to be deleted
            JSONObject contentJSON = new JSONObject(content);
            String memeTitle = contentJSON.optString("memeTitle");
            JSONArray tagArray = contentJSON.optJSONArray("tagTitle");

            if (memeTitle != null) {
                database.deleteMeme(memeTitle, username);
    
                for (int i = 0; i < tagArray.length(); i++) {

                }
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

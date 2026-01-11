package code.backend.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import code.backend.HttpExchangeMethods;

public class MemeDirectoryHandler implements HttpHandler {


    private final File memeDirectory;



    public MemeDirectoryHandler() {
        memeDirectory = new File(System.getProperty("user.dir"), "memes");
    }


    /**
    * Handles the method of the HTTP request (Available: GET)
    *
    * @param  exchange HTTP request hadler
    */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
        HttpExchangeMethods exchangeMethods = new HttpExchangeMethods(exchange, "[ERROR] - MEME_DIR :");

        try (exchange) {
            String method = exchange.getRequestMethod().toUpperCase();
            
            switch (method) {
                
                // Handle POST requests here (users send this for sending messages)
                case "GET" -> getRequest(exchange, exchangeMethods);
                
                // Hande unsupported methods
                default -> exchangeMethods.errorResponse(407, "Unsupported method");
            }
        }

		catch (Exception e) {
			exchangeMethods.errorResponse(400, e.getMessage());
		}
	}


	/**
    * Handles the GET method
    *
    * @param  exchange HTTP request hadler
    */
	private void getRequest(HttpExchange exchange, HttpExchangeMethods exchangeMethods) throws IOException {
        try {
            // Find the meme path
            String requestUrl = exchange.getRequestURI().getPath();
            String memePath = requestUrl.replaceFirst("^/api/meme/dir/?", "");

            // Get file type
            String memeType = "png";
            if (memePath.endsWith(".gif")){
                memeType = "gif";
            }

            // Find the meme file
            File meme = new File(memeDirectory, memePath);

            // Prevent path traversal
            if (!meme.getCanonicalPath().startsWith(memeDirectory.getCanonicalPath())) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            System.out.println(memePath + ", type: " + memeType);

            // Chech the file existance
            if (!meme.exists()) {
                throw new FileNotFoundException("Meme file not found");
            }

            // Add the file to the input
            exchange.getResponseHeaders().add("Content-Type", "image/" + memeType);
            exchange.sendResponseHeaders(200, meme.length());

            // Send meme file to the server
            try (OutputStream stream = exchange.getResponseBody()) {
                FileInputStream inputStream = new FileInputStream(meme);
                inputStream.transferTo(stream);
                inputStream.close();
            }

            System.out.println("Meme file send");
	    }

        catch (FileNotFoundException e) {
            exchangeMethods.errorResponse(404, e.getMessage());
        }
 
    }


}

package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.WrongMethodTypeException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import code.meme_comparators.MemeIdComparator;
import code.meme_comparators.MemeLikesComparator;
import code.meme_comparators.MemeTitleComparator;


public class SearchHandler implements HttpHandler {


    private final Database database;
    private Set<Tag> allTags;
    private final SortedMap<Integer, Meme> memes;
 
    private static final String ERROR_MESSAGE = "[ERROR] - SEARCH";




    /**
    * Handles all request for obseravtions
    *
    * @param database Database of the server
    */
    public SearchHandler(Database database, Set<Tag> allTags, SortedMap<Integer, Meme> memes) {
        this.database = database;
        this.allTags = allTags;
        this.memes = memes;
    }



    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {
            String method = exchange.getRequestMethod().toUpperCase();

            switch (method) {
                case "GET" -> getRequest(exchange);

                default -> errorResponse(exchange, 405, ERROR_MESSAGE + ": Unsupported search method\n");
            }
        }

        // Search items are invalid
        catch (IllegalArgumentException e) {
            errorResponse(exchange, 403, ERROR_MESSAGE + e.getMessage());
        }

        catch (Exception e) {
            errorResponse(exchange, 500, ERROR_MESSAGE + e.getMessage());
        }
    }

    


    private void getRequest(HttpExchange exchange) {

        try {

            // Content type validity chech
            Headers headers = exchange.getRequestHeaders();
            String content = getContent(exchange, headers);

            // Get the search details
            JSONObject contentJson = new JSONObject(content);
            String title = contentJson.optString("title", null);
            int id = contentJson.optInt("id", -1);
            JSONArray tagsArray = contentJson.optJSONArray("tags", null);
            SORT_TYPE sortingType = getMemeSortType(contentJson.optString("sorting_type"));

            // Corresponding memes array
            JSONArray memePaths = filterMemes(exchange, title, id, tagsArray, sortingType);

            // Send the meme paths
            byte[] memeBytes = memePaths.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, memeBytes.length);
        }

        catch (IOException e) {
            errorResponse(exchange, 406, ERROR_MESSAGE + e.getMessage());
        }

        catch (SQLException e) {
            errorResponse(exchange, 400, ERROR_MESSAGE + e.getMessage());
        }

    }


    private String getContent(HttpExchange exchange, Headers headers) throws IOException {

        if (!headerContent(headers).startsWith("application/json")) {
            throw new WrongMethodTypeException("Invalid method type");
        }

        try (InputStream inputStream = exchange.getRequestBody()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            return reader.lines().collect(Collectors.joining("\n"));
        }
    }


    /**
    * Gets the content from the header
    *
    * @param  headers HTTPS request and response header
    * @return Content as a string
    * @throws WrongMethodTypeException Content type is not "Content-Type"
    */
    private String headerContent(Headers headers) {
        // Set content type
        if (headers.containsKey("Content-Type")) {
            return headers.getFirst("Content-Type");
        }

        // Content type is incorrect
        else {
            throw new WrongMethodTypeException("Incorrect content type in request\n");
        }
    }


    private enum SORT_TYPE {
        ID,
        TITLE,
        LIKES,
        DEFAULT
    }

    private SORT_TYPE getMemeSortType(String sortType) {
        switch (sortType) {
            case "id" -> {return SORT_TYPE.ID;}
            case "title" -> {return SORT_TYPE.TITLE;}
            case "likes" -> {return SORT_TYPE.LIKES;}
            default -> {return SORT_TYPE.DEFAULT;}
        }
    }




    private JSONArray filterMemes(HttpExchange exchange, String title, int id, JSONArray tags, SORT_TYPE sortingType) throws SQLException {
        
        boolean isFiltered = false;
        List<Meme> filtererMemes = new ArrayList<>();
        
        // Filter ID
        if (id != -1) {
            filterId(filtererMemes, id);
            isFiltered = true;
        }

        // Filter title
        if (title != null) {
            filterTitle(filtererMemes, title);
            isFiltered = true;
        }

        // Filter with tags
        if (tags != null) {
            filterTags(filtererMemes, tags, isFiltered);
            isFiltered = true;
        }

        // Return the found memes as json array
        if (isFiltered) {
            return sortedMemeArray(exchange, filtererMemes, sortingType);
        } else {
            return sortedMemeArray(exchange, memes.values().stream().toList(), sortingType);
        }
    }


    private void filterId(List<Meme> currentMemes, int id) {

        // There aren't any memes
        if (currentMemes.isEmpty()) {
            return;
        }

        // There aren't any meme with the given ID
        if (!memes.containsKey(id)) {
            return;
        }

        // Add the corresponding meme to the map
        currentMemes.add(memes.get(id));
    }


    private void filterTitle(List<Meme> currentMemes, String title) {

        // Iterate the tree
        for (Meme meme: memes.values()) {

            // Add only the meme corresponding to the title
            if (title.equals(meme.getTitle())) {
                currentMemes.add(meme);
                return;
            }
        }
    }



    private void filterTags(List<Meme> currentMemes, JSONArray tagsArray, boolean isFiltered) {

        // Previous filters have filtered out everything
        if (currentMemes.isEmpty()) {
            return;
        }

        // Convert the tags json to set
        Set<Tag> tags = getTagSet(tagsArray);

        // This is first filtering, so all memes are filtered
        if (!isFiltered) {
            for (Meme meme: memes.values()) {

                // Add all corresponding memes
                if (meme.containsTags(tags)) {
                    currentMemes.add(meme);
                }
            }
        }

        // Only previously filtered memes are used
        else {
            for (Meme meme: currentMemes) {

                // Remove all non- corresponding memes
                if (!meme.containsTags(tags)) {
                    currentMemes.remove(meme);
                }
            }
        }
    }


    private Set<Tag> getTagSet(JSONArray tagsArray) {

        // Iterate all tags
        Set<Tag> tags = new HashSet<>();
        for (int i = 0; i < tagsArray.length(); i++) {

            // Get the title of the tag
            JSONObject tagJson = tagsArray.getJSONObject(i);
            String title = tagJson.optString("title", "Title not provided");

            // Only valid tags are used
            Tag tag = new Tag(title, 0);
            if (!allTags.contains(tag)) {
                continue;
            }

            // Add the valid tag to the set
            tags.add(new Tag(title, 0));
        }

        return tags;
    }


    @SuppressWarnings("incomplete-switch")
    private JSONArray sortedMemeArray(HttpExchange exchange, List<Meme> filteredMemes, SORT_TYPE sortingType) {

        // Sort the memes
        switch (sortingType) {
            case TITLE -> Collections.sort(filteredMemes, new MemeTitleComparator());
            case ID -> Collections.sort(filteredMemes, new MemeIdComparator());
            case LIKES -> Collections.sort(filteredMemes, new MemeLikesComparator());
        }

        // Add all meme paths to the array
        System.out.println("Searched memes:");
        JSONArray filteredMemesArray = new JSONArray();
        for (Meme meme: filteredMemes) {
            filteredMemesArray.put(getFullPath(exchange, meme.getTitle()));
            System.out.println(meme.toString());
        }

        return filteredMemesArray;
    }


    private String getFullPath(HttpExchange exchange, String title) {
        return exchange.getLocalAddress().getHostName()+"/memes/"+title;
    }




    /**
    * Sends error message to the server
    *
    * @param  exchange HTTPS request hadler
    * @param  statusCode HTTPS status code of the error
    * @param  message Error message as string
    */
    private void errorResponse(HttpExchange exchange, int statusCode, String message) {
        // Transform message to bytes
        byte[] responseBytes = ("[Error] OBSERVATION - "+message).getBytes(StandardCharsets.UTF_8);
    
        try (OutputStream outputStream = exchange.getResponseBody()) {

            // Send response
            exchange.sendResponseHeaders(statusCode, responseBytes.length);

            // Output the response
            outputStream.write(responseBytes);
            outputStream.flush();

        } catch (IOException e) {
            System.out.println("Error writing response: "+e.getMessage()+"\n");
        }
    }

    
}

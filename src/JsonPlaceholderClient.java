import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JsonPlaceholderClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public static String createUser(String userJson) throws IOException {
        return sendRequest(BASE_URL, "POST", userJson);
    }

    public static String updateUser(int id, String userJson) throws IOException {
        return sendRequest(BASE_URL + "/" + id, "PUT", userJson);
    }

    public static int deleteUser(int id) throws IOException {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode();
    }
    public static String getAllUsers() throws IOException {
        return sendRequest(BASE_URL, "GET", null);
    }

    public static String getUserById(int id) throws IOException {
        return sendRequest(BASE_URL + "/" + id, "GET", null);
    }

    public static String getUserByUsername(String username) throws IOException {
        return sendRequest(BASE_URL + "?username=" + username, "GET", null);
    }

    private static String sendRequest(String urlStr, String method, String body) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");

        if (body != null && !body.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("HTTP error code : " + responseCode);
        }
    }

    public static void main(String[] args) {
        try {
            String newUserJson = "{\"name\": \"Mike Set\", \"username\": \"mikes\", \"email\": \"Mikes@gmail.com\"}";
            String createdUser = createUser(newUserJson);
            System.out.println("Created User: " + createdUser);

            String updatedUserJson = "{\"name\": \"Mike Set Updated\", \"username\": \"mikes\", \"email\": \"Mikesupdated@gmail.com\"}";
            String updatedUser = updateUser(1, updatedUserJson);
            System.out.println("Updated User: " + updatedUser);

            int deleteStatus = deleteUser(1);
            System.out.println("Delete Status: " + deleteStatus);

            String allUsers = getAllUsers();
            System.out.println("All Users: " + allUsers);

            String userById = getUserById(1);
            System.out.println("User By ID: " + userById);

            String userByUsername = getUserByUsername("Bret");
            System.out.println("User By Username: " + userByUsername);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

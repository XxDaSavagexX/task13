import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserTodosFetcher {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static String getOpenTodos(int userId) throws IOException {

        String todosJson = sendRequest(BASE_URL + "/users/" + userId + "/todos", "GET", null);

        StringBuilder openTodos = new StringBuilder("[");
        String[] todos = todosJson.split("\\},\\{");
        for (String todo : todos) {
            if (todo.contains("\"completed\": false")) {
                openTodos.append("{").append(todo).append("},");
            }
        }
        if (openTodos.length() > 1) {
            openTodos.deleteCharAt(openTodos.length() - 1); 
        }
        openTodos.append("]");
        return openTodos.toString();
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
            String openTodos = getOpenTodos(1);
            System.out.println("Open Todos: " + openTodos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

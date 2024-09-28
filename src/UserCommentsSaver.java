import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserCommentsSaver {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static void saveCommentsOfLastPost(int userId) throws IOException {

        String postsJson = sendRequest(BASE_URL + "/users/" + userId + "/posts", "GET", null);

        int lastPostId = getLastPostId(postsJson);

        String commentsJson = sendRequest(BASE_URL + "/posts/" + lastPostId + "/comments", "GET", null);

        try (FileWriter file = new FileWriter("user-" + userId + "-post-" + lastPostId + "-comments.json")) {
            file.write(commentsJson);
        }
        System.out.println("Comments saved to user-" + userId + "-post-" + lastPostId + "-comments.json");
    }

    private static int getLastPostId(String postsJson) {

        String[] posts = postsJson.split("\\},\\{");
        int maxId = 0;
        for (String post : posts) {
            String idStr = post.split("\"id\":")[1].split(",")[0];
            int id = Integer.parseInt(idStr.trim());
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
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

            saveCommentsOfLastPost(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

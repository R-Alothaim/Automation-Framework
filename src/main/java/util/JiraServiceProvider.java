package util;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class JiraServiceProvider {
    private String baseUrl;
    private String auth;

    public JiraServiceProvider(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public String createIssue(String projectKey, String issueType, String summary, String description, String priority, String attachmentPath) {
        String url = baseUrl + "/rest/api/latest/issue/";
        String json = String.format(
            "{\"fields\": {" +
            "\"project\": {\"key\": \"%s\"}, " +
            "\"summary\": \"%s\", " +
            "\"description\": \"%s\", " +
            "\"issuetype\": {\"name\": \"%s\"}, " +
            "\"priority\": {\"name\": \"%s\"}" +
            "}}",
            projectKey, summary, description, issueType, priority
        );


        try (CloseableHttpClient client = getHttpClient()) {
            CloseableHttpResponse response = executePostRequest(client, url, json);
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode >= 200 && statusCode < 300) {
                return extractIssueKey(responseBody);
            } else {
                System.err.println("Failed to create issue. Status code: " + statusCode);
                System.err.println("Response body: " + responseBody);
                return null;
            }
        } catch (IOException | ParseException e) {
            System.err.println("IOException occurred while trying to create issue: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
	private CloseableHttpResponse executePostRequest(CloseableHttpClient client, String url, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Basic " + auth);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return client.execute(httpPost);
    }

    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
    public void addAttachment(String issueKey, String attachmentPath, CloseableHttpClient client) {
        String attachmentUrl = baseUrl + "/rest/api/latest/issue/" + issueKey + "/attachments";
        File file = new File(attachmentPath);
        
        if (!file.exists()) {
            System.err.println("Attachment file does not exist: " + attachmentPath);
            return;
        }

        try {
            HttpPost attachmentPost = new HttpPost(attachmentUrl);
            attachmentPost.setHeader("Authorization", "Basic " + auth);
            attachmentPost.setHeader("X-Atlassian-Token", "no-check");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
            attachmentPost.setEntity(builder.build());

            @SuppressWarnings("deprecation")
			CloseableHttpResponse response = client.execute(attachmentPost);
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode >= 200 && statusCode < 300) {
            } else {
                System.err.println("Failed to add attachment. Status code: " + statusCode);
                System.err.println("Response body: " + responseBody);
            }
        } catch (IOException | ParseException e) {
            System.err.println("IOException occurred while trying to add attachment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractIssueKey(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return jsonObject.get("key").getAsString();
        } catch (Exception e) {
            System.err.println("Failed to parse issue key from response: " + e.getMessage());
            return null;
        }
    }
}

package com.mindcoders.phial.jira;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by rost on 10/26/17.
 */

public class JiraApi {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String baseUrl;
    private final String projectKey;

    public JiraApi(OkHttpClient client, String baseUrl, String projectKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.projectKey = projectKey;
    }

    public CreatedIssueResponse createIssue(String summary, String description) throws IOException, JSONException {
        final String jsonBody = RestModelConverter.createIssueModel(projectKey, summary, description).toString();
        final RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        final Request request = new Request.Builder()
                .url(baseUrl + "rest/api/2/issue/")
                .post(requestBody)
                .build();

        final Response response = client.newCall(request).execute();

        final String responseBody = getResponseBodyAsText(response);
        if (!response.isSuccessful()) {
            throw new ApiException(response.code(), response.message(), responseBody);
        }

        if (responseBody == null) {
            throw new ApiException(-1, response.message(), "empty body");
        }

        return RestModelConverter.convertIssueCreatedResponse(responseBody);
    }

    private String getResponseBodyAsText(Response response) throws IOException {
        final ResponseBody responseBody = response.body();
        if (responseBody != null) {
            return responseBody.string();
        } else {
            return null;
        }
    }
}

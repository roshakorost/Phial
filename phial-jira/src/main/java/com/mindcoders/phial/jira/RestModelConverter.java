package com.mindcoders.phial.jira;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rost on 10/26/17.
 */

public class RestModelConverter {
    private static final String SUMMARY = "summary";
    private static final String DESCRIPTION = "description";
    private static final String KEY = "key";
    private static final String PROJECT = "project";
    private static final String ISSUE_TYPE = "issuetype";
    private static final String NAME = "name";

    private static final String DEFAULT_ISSUE_TYPE = "Bug";
    public static final String ID = "id";

    static JSONObject createIssueModel(String projectKey, String summary, String description) throws JSONException {
        final JSONObject projectObj = createJsonObj(KEY, projectKey);
        final JSONObject issueTypeObj = createJsonObj(NAME, DEFAULT_ISSUE_TYPE);

        final JSONObject fieldsObj = new JSONObject();
        fieldsObj.put(PROJECT, projectObj);
        fieldsObj.put(SUMMARY, summary);
        if (description != null) {
            fieldsObj.put(DESCRIPTION, description);
        }
        fieldsObj.put(ISSUE_TYPE, issueTypeObj);

        final JSONObject result = new JSONObject();
        result.put("fields", fieldsObj);
        return result;
    }

    static CreatedIssueResponse convertIssueCreatedResponse(String response) throws JSONException {
        final JSONObject jsonObject = new JSONObject(response);

        final String id = jsonObject.getString(ID);
        final String key = jsonObject.getString(KEY);
        return new CreatedIssueResponse(id, key);
    }

    private static JSONObject createJsonObj(String key, String value) throws JSONException {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }
}

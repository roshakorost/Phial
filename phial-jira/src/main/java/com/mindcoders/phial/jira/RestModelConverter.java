package com.mindcoders.phial.jira;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rost on 10/26/17.
 */

class RestModelConverter {
    private static final String SUMMARY = "summary";
    private static final String KEY = "key";
    private static final String PROJECT = "project";
    private static final String ISSUE_TYPE = "issuetype";
    private static final String NAME = "name";

    private static final String DEFAULT_ISSUE_TYPE = "Bug";
    private static final String ID = "id";

    static JSONObject createIssueModel(String projectKey, Map<String, Object> properties) throws JSONException {
        final JSONObject projectObj = createJsonObj(KEY, projectKey);
        final JSONObject issueTypeObj = createJsonObj(NAME, DEFAULT_ISSUE_TYPE);


        final JSONObject fieldsObj = new JSONObject(properties);
        fieldsObj.put(PROJECT, projectObj);
        fieldsObj.put(ISSUE_TYPE, issueTypeObj);

        final JSONObject result = new JSONObject();
        result.put("fields", fieldsObj);
        return result;
    }


    static void appendSummary(String message, HashMap<String, Object> extras) {
        extras.put(RestModelConverter.SUMMARY, message);
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

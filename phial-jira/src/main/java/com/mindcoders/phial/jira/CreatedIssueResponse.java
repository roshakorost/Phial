package com.mindcoders.phial.jira;

/**
 * Created by rost on 10/26/17.
 */

public class CreatedIssueResponse {
    private final String id;
    private final String key;

    public CreatedIssueResponse(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "CreatedIssueResponse{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

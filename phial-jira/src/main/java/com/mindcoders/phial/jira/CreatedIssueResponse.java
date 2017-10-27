package com.mindcoders.phial.jira;

/**
 * Created by rost on 10/26/17.
 */

class CreatedIssueResponse {
    private final String id;
    private final String key;

    CreatedIssueResponse(String id, String key) {
        this.id = id;
        this.key = key;
    }

    String getId() {
        return id;
    }

    String getKey() {
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

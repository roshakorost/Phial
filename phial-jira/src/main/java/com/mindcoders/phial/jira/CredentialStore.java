package com.mindcoders.phial.jira;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rost on 10/29/17.
 */

class CredentialStore {
    private static final String SP_NAME = "jira";
    private static final String CREDENTIALS_NAME = "credentials";
    private final SharedPreferences sharedPreferences;

    public CredentialStore(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    boolean hasCredentials() {
        return sharedPreferences.contains(CREDENTIALS_NAME);
    }

    String getUserCredential() {
        return sharedPreferences.getString(CREDENTIALS_NAME, null);
    }

    void storeCredential(String credential) {
        sharedPreferences.edit().putString(CREDENTIALS_NAME, credential).apply();
    }

    void drop() {
        sharedPreferences.edit().remove(CREDENTIALS_NAME).apply();
    }
}

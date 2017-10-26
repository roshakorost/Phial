package com.mindcoders.phial.jira;


import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rost on 10/26/17.
 */

public class OkHttpFactory {
    private final String userName;
    private final String password;

    public OkHttpFactory(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public OkHttpClient createAuthorizedClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(userName, password))
                .build();
    }

    private class AuthenticationInterceptor implements Interceptor {
        private final String credentials;

        public AuthenticationInterceptor(String userName, String password) {
            credentials = Credentials.basic(userName, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request original = chain.request();

            final Request request = original.newBuilder()
                    .header("Authorization", credentials).build();

            return chain.proceed(request);
        }
    }


}

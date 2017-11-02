package com.mindcoders.phial.jira;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rost on 10/26/17.
 */

class OkHttpFactory {

    OkHttpClient createAuthorizedClient(String credentials) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(credentials))
                .build();
    }

    private class AuthenticationInterceptor implements Interceptor {
        private final String credentials;

        AuthenticationInterceptor(String credentials) {
            this.credentials = credentials;
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

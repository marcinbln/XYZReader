package com.example.xyzreader.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceBuilder {
    // Base URL
    // https://go.udacity.com/xyz-reader-json
    // https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5d68f_xyz-reader/xyz-reader.json
    private static final String URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5d68f_xyz-reader/";

    // Create Retrofit Builder
    private static final Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create());

    // Create Retrofit Instance
    private static final Retrofit retrofit = builder.build();

    public static <T> T buildService(Class<T> type) {
        return retrofit.create(type);
    }


}

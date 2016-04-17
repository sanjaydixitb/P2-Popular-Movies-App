package com.bsdsolutions.sanjaydixit.p2_popular_movies_app;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sanjaydixit on 17/04/16.
 */
public class Tmdb {

    /**
     * Tmdb API URL.
     */
    public static final String API_URL = "https://api.themoviedb.org/3/";

    /**
     * API key query parameter name.
     */
    public static final String PARAM_API_KEY = "api_key";

    private static Tmdb instance;
    private Retrofit retrofit;
    private String apiKey;

    /**
     * Create a new manager instance.
     */
    private Tmdb() {
        setApiKey(BuildConfig.TMDB_API_KEY);
    }

    public static Tmdb getInstance() {
        if (instance != null)
            return instance;
        return new Tmdb();
    }

    /**
     * Set the TMDB API key. <p> The next service method call will trigger a rebuild of the {@link Retrofit} instance.
     * If you have cached any service instances, get a new one from its service method.
     *
     * @param value Your TMDB API key.
     */
    public Tmdb setApiKey(String value) {
        this.apiKey = value;
        retrofit = null;
        return this;
    }

    /**
     * Creates a {@link Retrofit.Builder} that sets the base URL, adds a Gson converter and sets {@link
     * #okHttpClientBuilder()} as its client. <p> Override this to for example set your own call executor.
     *
     * @see #okHttpClientBuilder()
     */
    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(TmdbHelper.getGsonBuilder().create()))
                .client(okHttpClientBuilder().build());
    }

    /**
     * Creates a {@link OkHttpClient.Builder} for usage with {@link #retrofitBuilder()}. Adds interceptors to add auth
     * headers and to log requests. <p> Override this to for example add your own interceptors.
     *
     * @see #retrofitBuilder()
     */
    protected OkHttpClient.Builder okHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl.Builder urlBuilder = request.url().newBuilder();
                urlBuilder.addEncodedQueryParameter(PARAM_API_KEY, apiKey);

                Request.Builder builder = request.newBuilder();
                builder.url(urlBuilder.build());

                return chain.proceed(builder.build());
            }
        });

        return builder;
    }

    /**
     * Return the current {@link Retrofit} instance. If none exists (first call, auth changed), builds a new one.
     * <p>When building, sets the base url and a custom client with an {@link Interceptor} which supplies authentication
     * data.
     */
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }

    public MovieListService movieListService() {
        return getRetrofit().create(MovieListService.class);
    }

}

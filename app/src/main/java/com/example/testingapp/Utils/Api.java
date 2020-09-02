package com.example.testingapp.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public class Api {
    public static String ROOT_URL = "https://api.stackexchange.com/";

    //1
    public interface RetrofitQuestions {
        @GET("2.2/questions")
        Call<String> putParam(@QueryMap HashMap<String, String> param);
    }
    //2
    public interface RetrofitAnswers {
        @GET("2.2/questions/{ids}/answers")
        Call<String> putParam(@Path("ids") String ids, @QueryMap HashMap<String, String> param);
    }
}

package com.example.testingapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingapp.Adapter.AdapterQuestions;
import com.example.testingapp.Model.ModelQuestion;
import com.example.testingapp.R;
import com.example.testingapp.Utils.Api;
import com.example.testingapp.Utils.ToolbarClass;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends ToolbarClass {

    AdapterQuestions adapterQuestions;
    ArrayList<ModelQuestion> arrayList;
    RecyclerView recyclerView;
    LinearLayout progressLay;
    RelativeLayout container;
    TextView notFound;

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(R.layout.activity_main, "Testing App");
        init();
        if (isOnline()){
            GetQuestion();
        }else{
            showSnackBar("No internet connection");
        }
    }

    private void init() {
        recyclerView = findViewById(R.id.recycler);
        progressLay = findViewById(R.id.progressLay);
        container = findViewById(R.id.container);
        notFound = findViewById(R.id.notFound);
    }

    private void GetQuestion() {
        arrayList = new ArrayList<>();
        progressLay.setVisibility(View.VISIBLE);
        notFound.setVisibility(View.GONE);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request.Builder ongoing = chain.request().newBuilder();
//                        ongoing.addHeader("Content-Type", "application/json;");
//                        ongoing.addHeader("Content-Type", "application/x-www-form-urlencoded");

                        return chain.proceed(ongoing.build());
                    }
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.ROOT_URL)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api.RetrofitQuestions service = retrofit.create(Api.RetrofitQuestions.class);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("fromdate", "1599004800");
        hashMap.put("order", "desc");
        hashMap.put("sort", "creation");
        hashMap.put("site", "stackoverflow");
        hashMap.put("pagesize", "50");

        Call<String> call = service.putParam(hashMap);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                try {
                    JSONObject objItems = new JSONObject(response.body());
                    JSONArray data = objItems.getJSONArray("items");
                    for (int i = 0; i <data.length() ; i++) {
                        JSONObject object= data.getJSONObject(i);
                        JSONObject ownerObj= object.getJSONObject("owner");

                        ModelQuestion item = new ModelQuestion();
                        item.setId(object.getString("question_id"));
                        item.setOwnerName(ownerObj.getString("display_name"));
                        item.setTitle(object.getString("title"));
                        item.setImgUrl(ownerObj.getString("profile_image"));

                        item.setAnswered(object.getBoolean("is_answered"));
                        item.setViews(object.getString("view_count"));
                        item.setAnswerCount(object.getString("answer_count"));
                        item.setCreatedAt(object.getString("creation_date"));

                        arrayList.add(item);
                    }
                    if (arrayList.size()>0){
                        progressLay.setVisibility(View.GONE);
                        adapterQuestions = new AdapterQuestions(MainActivity.this,arrayList);
                        recyclerView.setAdapter(adapterQuestions);
                        notFound.setVisibility(View.GONE);
                    }else{
                        progressLay.setVisibility(View.GONE);
                        notFound.setVisibility(View.VISIBLE);
                    }
                    progressLay.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    showSnackBar("تعذر الوصول لسجل الاسئلة حاول مجددا");
                    progressLay.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                showSnackBar("خطأ في الاتصال");
                progressLay.setVisibility(View.GONE);
            }
        });
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(container, msg, Snackbar.LENGTH_LONG);
        View snackview = snackbar.getView();
        snackview.setBackgroundColor(Color.GRAY);
        TextView masseage;
        masseage = snackview.findViewById(R.id.snackbar_text);
        masseage.setTextSize(14);
        masseage.setTextColor(Color.WHITE);
        snackbar.show();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
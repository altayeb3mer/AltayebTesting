package com.example.testingapp.Activity;


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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testingapp.Adapter.AdapterAnswer;
import com.example.testingapp.Adapter.AdapterQuestions;
import com.example.testingapp.Model.ModelAnswer;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class QuestionDetailsActivity extends ToolbarClass {

    TextView textViewOwner,textViewTitle,
    textViewViews,textViewAnswerCount,textViewCreatedAt;
    CircleImageView circleImageView;

    String id="",owner="",title="",views="",answerCount="",createdAt="",imgUrl="";

    boolean isAnswered = false;


    RecyclerView recyclerView;
    AdapterAnswer adapterAnswer;
    ArrayList<ModelAnswer> answerArrayList;


    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(R.layout.activity_question_details, "Question Details");
        init();

        Bundle args = getIntent().getExtras();
        if (args!=null){
            id = args.getString("id");
            owner = args.getString("name");
            title = args.getString("title");
            imgUrl = args.getString("img");

            views = args.getString("views");
            answerCount = args.getString("answerCount");
            createdAt = args.getString("creation");

            isAnswered = args.getBoolean("isAnswered");
//            Toast.makeText(this, "isAnswered = "+isAnswered, Toast.LENGTH_SHORT).show();

            initHeader();
        }

        if (isOnline()){
            if (isAnswered){
                GetAnswers();
            }else{
                textViewnoAnswer.setVisibility(View.VISIBLE);
            }
        }else{
            showSnackBar("No internet connection");
        }
    }

    TextView textViewnoAnswer;
    LinearLayout progressLay;
    private void GetAnswers() {
        answerArrayList = new ArrayList<>();
        progressLay.setVisibility(View.VISIBLE);
        textViewnoAnswer.setVisibility(View.GONE);
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

        Api.RetrofitAnswers service = retrofit.create(Api.RetrofitAnswers.class);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order", "desc");
        hashMap.put("sort", "activity");
        hashMap.put("site", "stackoverflow");
        hashMap.put("pagesize", "5");

        Call<String> call = service.putParam(id,hashMap);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                try {
                    JSONObject objItems = new JSONObject(response.body());
                    JSONArray data = objItems.getJSONArray("items");
                    for (int i = 0; i <data.length() ; i++) {
                        JSONObject object= data.getJSONObject(i);
                        JSONObject ownerObj= object.getJSONObject("owner");

                        ModelAnswer item = new ModelAnswer();
                        item.setId(object.getString("question_id"));
                        item.setName(ownerObj.getString("display_name"));
                        item.setImgUrl(ownerObj.getString("profile_image"));


                        answerArrayList.add(item);
                    }
                    if (answerArrayList.size()>0){
                        progressLay.setVisibility(View.GONE);
                        adapterAnswer = new AdapterAnswer(QuestionDetailsActivity.this,answerArrayList);
                        recyclerView.setAdapter(adapterAnswer);
                        textViewnoAnswer.setVisibility(View.GONE);
                    }else{
                        progressLay.setVisibility(View.GONE);
                        textViewnoAnswer.setVisibility(View.VISIBLE);
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
    private void initHeader() {
        textViewOwner.setText(owner);
        textViewTitle.setText(title);
        textViewViews.setText(views);
        textViewAnswerCount.setText(answerCount);
        textViewCreatedAt.setText(createdAt);

        try {
            Glide.with(this).load(imgUrl).into(circleImageView);
        }catch (Exception e){
            circleImageView.setBackgroundResource(R.drawable.account_circle);
        }


    }

    private void init() {
        textViewnoAnswer = findViewById(R.id.noAnswer);
        circleImageView = findViewById(R.id.img);
        textViewOwner = findViewById(R.id.owner);
        textViewTitle = findViewById(R.id.title);
        textViewViews = findViewById(R.id.viewsBy);
        textViewAnswerCount = findViewById(R.id.answerCount);
        textViewCreatedAt = findViewById(R.id.createdAt);

        progressLay = findViewById(R.id.progressLay);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        container = findViewById(R.id.container);

    }


    RelativeLayout container;
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
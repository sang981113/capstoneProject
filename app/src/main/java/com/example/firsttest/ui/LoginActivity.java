package com.example.firsttest.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.firsttest.databinding.ActivityLoginBinding;
import com.example.firsttest.request.LoginRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//import com.example.firsttest.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = new Intent(LoginActivity.this, UserListActivity.class);

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = binding.editTextTextPersonName.getText().toString();
                String password = binding.editTextTextPassword2.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                                String id = jsonResponse.getString("id");
                                String password = jsonResponse.getString("password");
                                // ????????? ????????? ????????? ?????? ?????????
                                intent.putExtra("id", id);
                                new BackgroundTask().execute();

                            } else {
                                Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(id, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    //????????? ?????????
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php??? ???????????? ????????? ????????????
            target = "http://210.117.128.200:8080/getjson.php";
        }


        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);//URL ?????? ??????

                //URL??? ???????????? ??????????????? ???????????? ??????
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //??????????????? ??????????????? ?????? ????????? httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //???????????? ???????????? ????????? ?????? ????????? ?????? ????????? ??? ?????????
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //????????? ????????? ??? ????????? ?????? ?????? StringBuilder???????????? ?????????
                StringBuilder stringBuilder = new StringBuilder();

                //????????? ????????? stringBuilder??? ?????????
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder??? ?????????
                }

                //???????????? ?????? ??? ?????????
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim??? ????????? ????????? ?????????

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            //intent.putExtra("userList", result);//????????? ?????? ?????????
            //?????? sharedpreference??? ?????????
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("userList", result);
            Log.d("userList", result + "??????????????? ??? sharedPreference??? ??????");
            editor.commit();
            LoginActivity.this.startActivity(intent);//ManagementActivity??? ?????????
        }

    }
}
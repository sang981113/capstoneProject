package com.example.firsttest.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.firsttest.databinding.ActivitySignUpBinding;
import com.example.firsttest.request.SignUpRequest;
import com.example.firsttest.request.ValidateRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivitySignUpBinding binding;
    private final String TAG = "MyFirebaseMsgService";
    RequestQueue queue;
    boolean canRegister;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queue = Volley.newRequestQueue(SignUpActivity.this);

        binding.imageValid.setVisibility(View.INVISIBLE);
        binding.imageInvalid.setVisibility(View.INVISIBLE);
        canRegister = false;

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
        binding.signupID.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            // String?????? ?????? ??? ???????????? JSON Object ????????? ???????????? ??????
                            // ?????? ???????????? ???????????? ?????? ????????? jsonResponse??? ??????
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isValid = jsonResponse.getBoolean("isValid");

                            if (isValid) {
                                binding.imageValid.setVisibility(View.VISIBLE);
                                binding.imageInvalid.setVisibility(View.INVISIBLE);
                                binding.signupID.setTextColor(Color.parseColor("#2C9C2A"));
                                canRegister = true;
                            } else {
                                binding.imageValid.setVisibility(View.INVISIBLE);
                                binding.imageInvalid.setVisibility(View.VISIBLE);
                                binding.signupID.setTextColor(Color.RED);
                                canRegister = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                String id = charSequence.toString();
                ValidateRequest validateRequest = new ValidateRequest(id, responseListener);
                queue.add(validateRequest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id = binding.signupID.getText().toString();
                String password = binding.signupPW.getText().toString();
                String confirm_password = binding.confirmPW.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            // String?????? ?????? ??? ???????????? JSON Object ????????? ???????????? ??????
                            // ?????? ???????????? ???????????? ?????? ????????? jsonResponse??? ??????
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) { // ??????????????? ???????????????
                                Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();//??????????????? ????????????(???????????? ?????? ??????)
                            } else {// ??????????????? ????????????
                                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????. ?????? ??? ??? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if(password.equals(confirm_password) && canRegister) {
                    SignUpRequest signupRequest = new SignUpRequest(id, password, token, responseListener);
                    queue.add(signupRequest);
                }
                else if(!password.equals(confirm_password)){
                    Toast.makeText(getApplicationContext(), "??????????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //????????????????????? ?????? ????????????
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();
                        Log.d(TAG, "token : "+ token);
                    }
                });
    }
}
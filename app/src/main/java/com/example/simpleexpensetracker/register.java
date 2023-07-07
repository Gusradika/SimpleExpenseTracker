package com.example.simpleexpensetracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class register extends AppCompatActivity {

    private TextView login;
    private Button btnRegister;
    private EditText inputNama, inputEmail, inputTelp, inputPassword, inputPassword2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        btnRegister = (Button) findViewById(R.id.btnRegister);
        inputNama = (EditText) findViewById(R.id.inputNama);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputTelp = (EditText) findViewById(R.id.inputTelp);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputPassword2 = (EditText) findViewById(R.id.inputPassword2);
        login = (TextView) findViewById(R.id.txtLogin);


        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Pass1", "Pass1: " + inputPassword.getText().toString());
                Log.d("Pass2", "Pass2: " + inputPassword2.getText().toString());

                Log.d("inputNama", inputNama.getText().toString());
                String username = inputNama.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String telp = inputTelp.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String password2 = inputPassword2.getText().toString().trim();

                if(!username.isEmpty()){
//                    Log.d("KOSONG!", "KOSONG! ");
//                    Toast.makeText(getApplicationContext(), "NAMA KOSONG!", Toast.LENGTH_SHORT).show();
                    if(!email.isEmpty() && !email.contains(" ")){
                        if(!telp.isEmpty()){
                            if(!password.isEmpty()){
                                if (!password2.isEmpty()){
                                    if(inputPassword.getText().toString().equals(inputPassword2.getText().toString())){
                                        Log.d("Betul!", "BETUL! ");
                                        Toast.makeText(getApplicationContext(), "Betul!", Toast.LENGTH_SHORT).show();
//                                        Do Logic Here

                                        new Konektor(register.this, "http://10.0.2.2/uas_mobile/api/user/createUser.php", new Uri.Builder().appendQueryParameter("nama", username).appendQueryParameter("email", email).appendQueryParameter("password", password).appendQueryParameter("telp", telp)).execute();

                                    }else{
                                        Log.d("Salah!", "SALAH! ");
                                        Toast.makeText(getApplicationContext(), "Salah!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{cetakError();}
                            }else {cetakError();}
                        }else {cetakError();}
                    }else{cetakError();}
                }else {cetakError();}


            }
        });

    }


    private void cetakError(){
        Toast.makeText(getApplicationContext(), "Inputan tidak boleh kosong! / Email tidak boleh mengandung spasi", Toast.LENGTH_SHORT).show();
    }

    private class Konektor extends AsyncTask<String, String, String> {
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEUT = 10000;
        Context context;
        ProgressDialog pdLoading;
        HttpURLConnection conn;
        URL url = null;
        String clientUrl;
        //        String p1, p2, p3;
        Uri.Builder builder;
        String result = null;


        public Konektor(Context context, String clientUrl, Uri.Builder builder) {
            this.context = context;
            this.clientUrl = clientUrl;
            this.builder = builder;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(clientUrl);

                System.out.println("try 1");
            } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
                e.printStackTrace();
                System.out.println("gagal 1");
                return e.getMessage();
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

//                Uri.Builder builder = new Uri.Builder().appendQueryParameter("nama", p1).appendQueryParameter("username", p2).appendQueryParameter("password", p3);
                String query = builder.build().getEncodedQuery();
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(query);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                conn.connect();
                System.out.println("try 2");
                System.out.println("yourLink?" + query);

            } catch (IOException e) {
                System.out.println("gagal 2");
                throw new RuntimeException(e);
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code != HttpURLConnection.HTTP_OK) {
                    System.out.println("gagal 3");
                    return ("Connection error");
                } else {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("try 3");
                    return (result.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
//            pdLoading.dismiss();
//            setResult(s);

            try {
                JSONObject result = new JSONObject(s);
                String success = result.getString("success");
                String errorMsg = result.getString("error");

                if (success.equals("1")) {
                    System.out.println("success");
                    System.out.println(result);
//                    JSONArray users = result.getJSONArray("users");
//                    JSONObject userData = null;
//                    for (int i = 0; i < users.length(); i++) {
//                        userData = users.getJSONObject(i);
////                        System.out.println(userData);
//                        String user_id = userData.getString("user_id");
//                        String fullname = userData.getString("fullname");
//                        String email = userData.getString("password");
//                        user = new User(Integer.parseInt(user_id), fullname, email);
//                    }
                    Toast.makeText(getApplicationContext(), "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();
//                    System.out.println(dataUser.toString());
                } else {
//                    txtMessageError.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
//                    txtMessageError.setText(msgGagal.toString());
//                    System.out.println(result.toString());
//                    System.out.println("gagal");
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
//


//            txtStatus.setText(s.toString());
//            txtStatusLogin.setText(s.toString());
        }
    }
}

package com.example.simpleexpensetracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Calendar;

public class tambahActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnTambah = findViewById(R.id.btnTambah);
        Button openCalendarButton = findViewById(R.id.openCalendarButton);
        EditText inputTanggal = findViewById(R.id.inputTanggal);
        Spinner spnrKategori = findViewById(R.id.spnrKategori);
        EditText inputJudul = findViewById(R.id.inputJudul);
        EditText inputJumlah = findViewById(R.id.inputJumlah);
//        EditText inputJumlah = findViewById(R.id.inputJumlah);

        inputTanggal.setFocusable(false);
        inputTanggal.setClickable(false);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        adapter2.add("All");
        adapter2.add("Primer");
        adapter2.add("Sekunder");
        adapter2.add("Tersier");

// Set adapter ke spinner
        spnrKategori.setAdapter(adapter2);

        Intent intent = getIntent();

        User user = (User) intent.getSerializableExtra("dataUser");

        // Gunakan objek yang diterima
        String nama = user.getNama();
        String email = user.getEmail();
        String userId = user.getUserId();


        btnTambah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String judul = inputJudul.getText().toString();
                String jumlah = inputJumlah.getText().toString();
                String tanggal = inputTanggal.getText().toString();
                String kategori = spnrKategori.getSelectedItem().toString();

                if(!judul.isEmpty()){
                    if(!jumlah.isEmpty()){
                        if(!tanggal.isEmpty()){

                                new Konektor(tambahActivity.this, "http://10.0.2.2/uas_mobile/api/pengeluaran/createPengeluaran.php", new Uri.Builder().appendQueryParameter("user_id", userId).appendQueryParameter("nama_pengeluaran", judul).appendQueryParameter("kategori_id", kategori).appendQueryParameter("date", tanggal).appendQueryParameter("value", jumlah)).execute();

                        }else{cetakError();}
                    }else {cetakError();}
                }else{
                    cetakError();
                }

                    }
        });

        openCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(tambahActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Tangkap tanggal yang dipilih
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                        // Set nilai tanggal ke dalam EditText
                        inputTanggal.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Tutup DatePicker setelah pengguna memilih tanggal
                        datePickerDialog.dismiss();
                    }
                });

                datePickerDialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
            setResult(s);

            try {
                JSONObject result = new JSONObject(s);
                String success = result.getString("success");
                String message = result.getString("message");


                if (success.equals("1")) {

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                    txtMessageError.setVisibility(View.VISIBLE);
//                    String msgGagal = result.getString("message");
//                    txtMessageError.setText(msgGagal.toString());
//                    System.out.println(result.toString());
//                    System.out.println("gagal");
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void cetakError(){
        Toast.makeText(getApplicationContext(), "Inputan tidak boleh kosong! / Email tidak boleh mengandung spasi", Toast.LENGTH_LONG).show();
    }
}

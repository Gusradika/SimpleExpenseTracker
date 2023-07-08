package com.example.simpleexpensetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView txtNama, txtEmail, txtTotalHari, txtTotalBulan, txtPrimer, txtSekunder, txtTersier, txtPrimerHarian, txtSekunderHarian, txtTersierHarian;


    Context context;

    Button btnLogout, btnDetail, btnRefresh, btnTambah;

    User user;
// ...

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        btnTambah = (Button) findViewById(R.id.btnTambah);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);

txtPrimer = (TextView) findViewById(R.id.txtPrimer);
txtSekunder = (TextView) findViewById(R.id.txtSekunder);
txtTersier = (TextView) findViewById(R.id.txtTersier);
txtPrimerHarian = (TextView) findViewById(R.id.txtPrimerHarian);
txtSekunderHarian = (TextView) findViewById(R.id.txtSekunderHarian);
txtTersierHarian = (TextView) findViewById(R.id.txtTersierHarian);

        txtNama = (TextView) findViewById(R.id.txtNama);
        txtTotalHari = (TextView) findViewById(R.id.txtTotalHari);
        txtTotalBulan = (TextView) findViewById(R.id.txtTotalBulan);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnDetail = (Button) findViewById(R.id.btnDetailPengeluaran);

        context = this.getApplicationContext();

        Intent intent = getIntent();


        // Mendapatkan objek Person dari Intent
        user = (User) intent.getSerializableExtra("dataUser");

        // Gunakan objek yang diterima
        String nama = user.getNama();
        String email = user.getEmail();
        String userId = user.getUserId();

        txtNama.setText(nama);
        txtEmail.setText(email);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, tambahActivity.class);
                intent.putExtra("dataUser", user);
                startActivity(intent);
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, detailPengeluaran.class);
                intent.putExtra("dataUser", user);
                startActivity(intent);
//                finish();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Konektor(HomeActivity.this, "http://10.0.2.2/uas_mobile/api/pengeluaran/readPengeluaran.php", new Uri.Builder().appendQueryParameter("user_id", userId)).execute();
//                finish();
            }
        });



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
//                intent.putExtra("dataUser", user);
                startActivity(intent);
                finish();
            }
        });

        new Konektor(HomeActivity.this, "http://10.0.2.2/uas_mobile/api/pengeluaran/readPengeluaran.php", new Uri.Builder().appendQueryParameter("user_id", userId)).execute();
    }


    private class Konektor extends AsyncTask<String, String, String> {
        ArrayList<HashMap<String, String>> listItems = new ArrayList<>();
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEUT = 10000;
        Context context;
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
                String totalPengeluaran = result.getString("jumlahPengeluaran");
                String totalPengeluaranHarian = result.getString("jumlahPengeluaranHarian");
                String totalPrimer = result.getString("jumlahPrimer");
                String totalPrimerHari = result.getString("jumlahPrimerHari");
                String totalSekunder = result.getString("jumlahSekunder");
                String totalSekunderHari = result.getString("jumlahSekunderHari");
                String totalTersier = result.getString("jumlahTersier");
                String totalTersierHari = result.getString("jumlahTersierHari");


                totalPengeluaran = "Rp. " + totalPengeluaran;
                txtTotalHari.setText("Rp. " + totalPengeluaranHarian);
                txtTotalBulan.setText(totalPengeluaran);
                txtPrimer.setText("Rp. " + totalPrimer);
                txtSekunder.setText("Rp. " + totalSekunder);
                txtTersier.setText("Rp. " + totalTersier);

                txtPrimerHarian.setText("Rp. " + totalPrimerHari);
                txtSekunderHarian.setText("Rp. " + totalSekunderHari);
                txtTersierHarian.setText("Rp. " + totalTersierHari);
//                String message = result.getString("message");




            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


//            txtStatus.setText(s.toString());
//            txtStatusLogin.setText(s.toString());
        }
    }



}

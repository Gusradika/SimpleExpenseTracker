package com.example.simpleexpensetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;

public class detailPengeluaran extends AppCompatActivity {

    ListView listView;

    Button btnBack, btnFilter;

    Spinner spnrKategori, spnrFilter;

    Context context;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        spnrKategori = (Spinner) findViewById(R.id.spnrKategori);
        spnrFilter = (Spinner) findViewById(R.id.spnrFilter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.add("All");
        adapter.add("Primer");
        adapter.add("Sekunder");
        adapter.add("Tersier");

// Set adapter ke spinner
        spnrKategori.setAdapter(adapter);

//        spnrCount = (Spinner) findViewById(R.id.spnrKategori);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2.add("All");
        adapter2.add("Terbesar");
        adapter2.add("Terkecil");
        adapter2.add("Date");

// Set adapter ke spinner
        spnrFilter.setAdapter(adapter2);


        listView = (ListView) findViewById(R.id.listView);
        context = this.getApplicationContext();
        btnBack = (Button) findViewById(R.id.btnBack);
        btnFilter = (Button) findViewById(R.id.btnFilter);

        Intent intent = getIntent();

        User user = (User) intent.getSerializableExtra("dataUser");

        // Gunakan objek yang diterima
        String nama = user.getNama();
        String email = user.getEmail();
        String userId = user.getUserId();

        new Konektor(detailPengeluaran.this, "http://10.0.2.2/uas_mobile/api/pengeluaran/readPengeluaran.php", new Uri.Builder().appendQueryParameter("user_id", userId)).execute();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Filter(detailPengeluaran.this, "http://10.0.2.2/uas_mobile/api/pengeluaran/filterPengeluaran.php", new Uri.Builder().appendQueryParameter("user_id", userId).appendQueryParameter("filterNama", spnrKategori.getSelectedItem().toString()).appendQueryParameter("filterCount", spnrFilter.getSelectedItem().toString())).execute();
            }
        });
    }

    private class Filter extends AsyncTask<String, String, String> {
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


        public Filter(Context context, String clientUrl, Uri.Builder builder) {
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
                totalPengeluaran = "Rp. " + totalPengeluaran;
//                String message = result.getString("message");


                if (success.equals("1")) {
                    System.out.println("success");
                    System.out.println(result);
                    JSONArray array = result.getJSONArray("pengeluaran");
//                    JSONObject userData = null;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
//                        System.out.println(userData);
                        String judul = obj.getString("nama_pengeluaran");
                        String value = obj.getString("value");
                        String date = obj.getString("date");
                        String kategori = obj.getString("kategori_id");
                        System.out.println(kategori);

                        value = "Rp. " + value;

                        System.out.println(kategori);

                        HashMap<String, String> item = new HashMap<>();
                        item.put("judul", judul);
                        item.put("value", value);
                        item.put("date", date);
                        item.put("kategori", kategori);
                        listItems.add(item);





                        // nama, email, telp, user_id
//                        user = new User(nama, email, telp, user_id);
//                        Log.d("Debug", user.getUserId());
//                        Log.d("Debug", user.getEmail());
//                        Toast.makeText(getApplicationContext(), "Berhasil ambil data", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                        intent.putExtra("dataUser", user);
//                        startActivity(intent);
//                        finish();
                    }
                    ListAdapter adapter = new SimpleAdapter(context, listItems, R.layout.layout_adapter_pengeluaran, new String[]{"judul", "value", "date", "kategori"}, new int[]{R.id.txtKeterangan, R.id.txtJumlah, R.id.txtTanggal, R.id.txtKategori});
                    listView.setAdapter(adapter);

//                    System.out.println(dataUser.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak Berhasil AMbil Data", Toast.LENGTH_SHORT).show();
//                    txtMessageError.setVisibility(View.VISIBLE);
//                    String msgGagal = result.getString("message");
//                    txtMessageError.setText(msgGagal.toString());
//                    System.out.println(result.toString());
//                    System.out.println("gagal");
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


//            txtStatus.setText(s.toString());
//            txtStatusLogin.setText(s.toString());
        }
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
                totalPengeluaran = "Rp. " + totalPengeluaran;
//                String message = result.getString("message");


                if (success.equals("1")) {
                    System.out.println("success");
                    System.out.println(result);
                    JSONArray array = result.getJSONArray("pengeluaran");
//                    JSONObject userData = null;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
//                        System.out.println(userData);
                        String judul = obj.getString("nama_pengeluaran");
                        String value = obj.getString("value");
                        String date = obj.getString("date");
                        String kategori = obj.getString("kategori_id");
                        System.out.println(kategori);

                        value = "Rp. " + value;

                        System.out.println(kategori);

                        HashMap<String, String> item = new HashMap<>();
                        item.put("judul", judul);
                        item.put("value", value);
                        item.put("date", date);
                        item.put("kategori", kategori);
                        listItems.add(item);





                        // nama, email, telp, user_id
//                        user = new User(nama, email, telp, user_id);
//                        Log.d("Debug", user.getUserId());
//                        Log.d("Debug", user.getEmail());
//                        Toast.makeText(getApplicationContext(), "Berhasil ambil data", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                        intent.putExtra("dataUser", user);
//                        startActivity(intent);
//                        finish();
                    }
                    ListAdapter adapter = new SimpleAdapter(context, listItems, R.layout.layout_adapter_pengeluaran, new String[]{"judul", "value", "date", "kategori"}, new int[]{R.id.txtKeterangan, R.id.txtJumlah, R.id.txtTanggal, R.id.txtKategori});
                    listView.setAdapter(adapter);

//                    System.out.println(dataUser.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak Berhasil AMbil Data", Toast.LENGTH_SHORT).show();
//                    txtMessageError.setVisibility(View.VISIBLE);
//                    String msgGagal = result.getString("message");
//                    txtMessageError.setText(msgGagal.toString());
//                    System.out.println(result.toString());
//                    System.out.println("gagal");
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


//            txtStatus.setText(s.toString());
//            txtStatusLogin.setText(s.toString());
        }
    }
}

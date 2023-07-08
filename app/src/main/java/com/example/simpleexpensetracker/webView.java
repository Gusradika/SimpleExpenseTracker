package com.example.simpleexpensetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class webView extends AppCompatActivity {
    WebView wvCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();

        User user = (User) intent.getSerializableExtra("dataUser");

        // Gunakan objek yang diterima
        String nama = user.getNama();
        String email = user.getEmail();
        String userId = user.getUserId();

        wvCanvas = findViewById(R.id.wvCanvas);
        Button btnBack = findViewById(R.id.btnBack);
        Button printButton = findViewById(R.id.printButton);

        WebSettings webSettings = wvCanvas.getSettings();
        wvCanvas.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        wvCanvas.loadUrl("http://192.168.1.4/uas_mobile/api/pengeluaran/loadData.php?user_id=" + userId);
//        wvCanvas.loadUrl("https://www.facebook.com");

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWebPrintJob();
            }
        });
    }

    private void createWebPrintJob() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = wvCanvas.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Print Job";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
}

package com.example.controlecartao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class InfoComprasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_compras);
        Toolbar toolbar = findViewById(R.id.toolbar_infocomprasactivity);
        setSupportActionBar(toolbar);
    }
}
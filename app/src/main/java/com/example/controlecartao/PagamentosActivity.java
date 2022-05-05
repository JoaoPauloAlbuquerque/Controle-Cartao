package com.example.controlecartao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class PagamentosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamentos);
        Toolbar toolbar = findViewById(R.id.toolbar_pagamentosactivity);
        setSupportActionBar(toolbar);
    }
}
package com.example.firebasemessages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etNom;
    EditText etPrenom;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        createComponents();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Configuration");
        actionBar.setDisplayHomeAsUpEnabled(true);

        etPrenom.setHint(getIntent().getStringExtra(getString(R.string.nav_header_prenom)));
        etNom.setHint(getIntent().getStringExtra(getString(R.string.nav_header_nom)));

        btnSave.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void createComponents() {
        etPrenom = findViewById(R.id.et_prenom);
        etNom = findViewById(R.id.et_nom);
        btnSave = findViewById(R.id.btn_save);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            Intent i = new Intent();
            String nom = etNom.getText().toString();
            String prenom = etPrenom.getText().toString();
            i.putExtra(getString(R.string.nav_header_prenom), prenom.substring(0, 1).toUpperCase() + prenom.substring(1).toLowerCase());
            i.putExtra(getString(R.string.nav_header_nom), nom.substring(0, 1).toUpperCase() + nom.substring(1).toLowerCase());
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
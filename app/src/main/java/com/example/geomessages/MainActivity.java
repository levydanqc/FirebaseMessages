package com.example.geomessages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.geomessages.data.AppExecutors;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.databinding.ActivityMainBinding;
import com.example.geomessages.http.VolleyUtils;
import com.example.geomessages.model.Message;
import com.example.geomessages.ui.liste.ListeViewModel;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MessagesRoomDatabase mDb;
    private TextView tvNom;
    private TextView tvPrenom;
    private final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = MessagesRoomDatabase.getDatabase(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_liste, R.id.nav_maps)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ListeViewModel listeViewModel = new ViewModelProvider(this).get(ListeViewModel.class);

        if (listeViewModel.getMessages().getValue() == null || listeViewModel.getMessages().getValue().size() < 1) {
            new VolleyUtils().getMessages(this,
                    messagesArrayList -> AppExecutors.getInstance().diskIO().execute(() -> {
                        mDb.messageDao().deleteAll();
                        for (Message article : messagesArrayList) {
                            mDb.messageDao().insert(article);
                        }
                    }));
        }

        View header = navigationView.getHeaderView(0);
        tvPrenom = (TextView) header.findViewById(R.id.tv_prenom);
        tvNom = (TextView) header.findViewById(R.id.tv_nom);
        createView();
    }

    void writeSharePreferences(String prenom, String nom) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.nav_header_prenom), prenom);
        editor.putString(getString(R.string.nav_header_nom), nom);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AppExecutors.getInstance().diskIO().execute(() -> {
                    mDb.messageDao().deleteAll();
                });
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this, ConfigActivity.class);
                settings.putExtra(getString(R.string.nav_header_prenom), tvPrenom.getText().toString());
                settings.putExtra(getString(R.string.nav_header_nom), tvNom.getText().toString());
                startActivityForResult(settings, REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            writeSharePreferences(
                    data.getStringExtra(getString(R.string.nav_header_prenom)),
                    data.getStringExtra(getString(R.string.nav_header_nom))
            );
            createView();
        }
    }

    private void createView() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        tvPrenom.setText(sharedPref.getString(getString(R.string.nav_header_prenom), "Cegep"));
        tvNom.setText(sharedPref.getString(getString(R.string.nav_header_nom), "Garneau"));

    }
}

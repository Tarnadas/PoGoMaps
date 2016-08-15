package com.pokemongomap.pokemongomap;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pokemongomap.helpers.BitmapHelper;
import com.pokemongomap.helpers.Constants;
import com.pokemongomap.permissions.EasyPermissions;
import com.pokemongomap.pokemon.PokemonData;
import com.pokemongomap.helpers.PokemonHelper;
import com.pokemongomap.services.LocationService;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private static String TAG = "fragment";

    private Fragment mFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        Intent database = new Intent(this, DatabaseConnection.class);
        startService(database);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        // Handle negative button on click listener. Pass null if you don't want to handle it.
        DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Let's show a toast
                Toast.makeText(getApplicationContext(), R.string.settings_dialog_canceled, Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        };

        // (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
        // This will display a dialog directing them to enable the permission in app settings.
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again), R.string.setting, R.string.cancel,
                cancelButtonListener, perms);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Do something after user returned from app settings screen. User may be
        // changed/updated the permissions. Let's check whether the user has some permissions or not
        // after returned from settings screen
        if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
            String[] perms;
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                perms = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            } else {
                perms = new String[]{ Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            }
            boolean hasPermissions = EasyPermissions.hasPermissions(getApplicationContext(), perms);
            if (!hasPermissions) {
                System.exit(0);
            } else {
                Intent serviceIntent = new Intent(this, LocationService.class);
                startService(serviceIntent);
                Intent database = new Intent(this, DatabaseConnection.class);
                startService(database);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseConnection.init(this);
        PokemonHelper.init(getApplicationContext());
        PokemonData.init(getApplicationContext());
        BitmapHelper.init();

        if (savedInstanceState == null) {
            DatabaseConnection.getInstance().purge();
            mFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, mFragment, TAG).commit();
        } else {
            mFragment = getSupportFragmentManager().findFragmentByTag(TAG);
        }


        String[] perms;
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            perms = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        } else {
            perms = new String[]{ Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        }

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_permissions), Constants.PERMISSION_REQUEST_CODE, perms);
        } else {
            Intent serviceIntent = new Intent(this, LocationService.class);
            startService(serviceIntent);
            Intent database = new Intent(this, DatabaseConnection.class);
            startService(database);
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Highlight the selected item has been done by NavigationView
        MenuItem item = navigationView.getMenu().getItem(0);
        item.setChecked(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_map:
                mFragment = MapFragment.newInstance();
                break;
            case R.id.nav_pokemon:
                mFragment = PokemonFragment.newInstance();
            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainContent, mFragment, TAG).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

package com.ravenioet.ravenqr.tools;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ravenioet.ravenqr.Main;
import com.ravenioet.ravenqr.R;

public class Boot extends AppCompatActivity {

    private static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode
                (AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_boot);
        checkPermissions();

    }

    public boolean checkAllPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int Storage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int External = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int SixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        int Camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return /*FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&*/
                Storage == PackageManager.PERMISSION_GRANTED &&
                Camera == PackageManager.PERMISSION_GRANTED &&
                External == PackageManager.PERMISSION_GRANTED /*&&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SixthPermissionResult == PackageManager.PERMISSION_GRANTED*/;
    }

    private void checkPermissions() {
        //SessionManager.getInstance(this).log_session("core","android api. "+ Build.VERSION.SDK_INT);
        //SessionManager.getInstance(this).log_session("core","android v. > 6");
        if (!checkAllPermission()) {
            requestPermission();
        } else {
            start_qr();
        }
    }
    public void start_qr(){
        finish();
        startActivity(new Intent(this, Main.class));
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadExternalStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean Camera = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (ReadExternalStatePermission && Camera && WriteExternalStatePermission) {
                        start_qr();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "One or more permissions denied!! Restart app and allow required permission",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

}
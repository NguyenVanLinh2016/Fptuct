package dev.linhnv.fptuct;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dev.linhnv.fptuct.model.GPS_Service;

public class AdminActivity extends AppCompatActivity {

    private Button btn_startPosition;
    private TextView txt_getPosition;
    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btn_startPosition = (Button) findViewById(R.id.btn_getPosition);
        txt_getPosition = (TextView) findViewById(R.id.txt_getPosition);
        if (!runtime_permissions()){
            enable_button();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //txtGetposition.setText(intent.getExtras().get("getPosition")+"");
                    //txtGetposition.setText(intent.getExtras().get("getLatitude")+"");
                    //Toast.makeText(MainActivity.this, ""+intent.getExtras().get("getPosition"), Toast.LENGTH_SHORT).show();
                    final double latitude = (double) intent.getExtras().get("getLatitude");
                    final double longtitude = (double) intent.getExtras().get("getLongtitude");
                    txt_getPosition.setText(latitude +"l"+ longtitude +"");
                    Toast.makeText(AdminActivity.this, "la: "+latitude + " long: " +longtitude, Toast.LENGTH_SHORT).show();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }
    private void writePosition(String id, double latitude, double longtitude) {
       
    }
    private void enable_button() {
        btn_startPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
            }
        });
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
//                stopService(i);
//            }
//        });
    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_button();
            }else{
                runtime_permissions();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}

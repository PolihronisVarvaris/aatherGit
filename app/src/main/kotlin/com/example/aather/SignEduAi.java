package com.example.aather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class SignEduAi extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edusign);

        checkPermissionAndOpenApp();
    }

    private void checkPermissionAndOpenApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.example.nyx","com.example.nyx.MainActivity"));
        startActivity(intent);
    }


}

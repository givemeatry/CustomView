package com.zefengsysu.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zefengsysu.lib.ripplecirclebutton.RippleCircleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RippleCircleButton rippleCircleButton = (RippleCircleButton) findViewById(R.id.btn1);
        rippleCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

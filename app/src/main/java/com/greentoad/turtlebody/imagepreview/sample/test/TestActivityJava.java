package com.greentoad.turtlebody.imagepreview.sample.test;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.greentoad.turtlebody.imagepreview.sample.R;

public class TestActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_java);

        TextView t2 = findViewById(R.id.text2);
        //t2.setMovementMethod(LinkMovementMethod.getInstance());
    }



}

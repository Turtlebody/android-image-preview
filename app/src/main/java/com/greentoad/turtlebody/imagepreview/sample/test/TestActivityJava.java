package com.greentoad.turtlebody.imagepreview.sample.test;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.greentoad.turtlebody.imagepreview.ImagePreview;
import com.greentoad.turtlebody.imagepreview.core.ImagePreviewConfig;
import com.greentoad.turtlebody.imagepreview.sample.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TestActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_java);

        TextView t2 = findViewById(R.id.text2);
        //t2.setMovementMethod(LinkMovementMethod.getInstance());


    }


    void startPreview(ArrayList<Uri> arrayList){

        ImagePreviewConfig config = new ImagePreviewConfig().setAllowAddButton(true).setUris(arrayList);

        ImagePreview.with(this)
                .setConfig(config)
                .setListener(new ImagePreview.ImagePreviewImpl.OnImagePreviewListener() {
                    @Override
                    public void onDone(@NotNull ArrayList<Uri> data) {
                        //after done all uri is sent back
                    }

                    @Override
                    public void onAddBtnClicked() {
                        //trigger when button clicked
                    }
                })
                .start();

        ImagePreview.ImagePreviewImpl imagePreview = ImagePreview.with(this);
        imagePreview.dismissImagePreview();

    }
}

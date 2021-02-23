package com.alf.preciosaspromessas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class FavoriteItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_item);

        TextView mVerse = findViewById(R.id.textView_fav_item);
        ImageButton mShare = findViewById(R.id.item_btn_share);
        ImageButton mBack = findViewById(R.id.item_btn_back);

        String verse = getIntent().getStringExtra("verse");
        mVerse.setText(verse);

        mBack.setOnClickListener(v -> onBackPressed());

        mShare.setOnClickListener(v -> share(verse));

    }

    private void share(String verse) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, verse);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

}
package com.alf.preciosaspromessas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

        mVerse.setOnLongClickListener(v -> {
            copy(verse);
            return false;
        });

    }

    private void share(String verse) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, verse);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void copy(String verse){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null , verse);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(FavoriteItemActivity.this, R.string.text_copied, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

}
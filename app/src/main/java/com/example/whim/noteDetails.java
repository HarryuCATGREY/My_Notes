package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class noteDetails extends AppCompatActivity {


    private TextView existTitleDetail, existNoteDetail;
    ImageView editNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        existTitleDetail = findViewById(R.id.existTitle1);
        existNoteDetail = findViewById(R.id.existNote1);
        editNote = findViewById(R.id.noteedit);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                view.getContext().startActivity(intent);
            }
        });
        ImageView imageBacknote = findViewById(R.id.imageBack1);
        imageBacknote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        existTitleDetail.setText(data.getStringExtra("title"));
        existNoteDetail.setText(data.getStringExtra("content"));

    }
}
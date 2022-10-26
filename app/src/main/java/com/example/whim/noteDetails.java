package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class noteDetails extends AppCompatActivity {


    private TextView existTitleDetail, existNoteDetail;
    ImageView editNote;
    TextView existTextDateTime;
    TextView existLocationText;
    ImageView existSelectedImage;
    StorageReference imgStorageReference;
    StorageReference storageReference;

    String currentPhotoPath;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        existTitleDetail = findViewById(R.id.existTitle1);
        existNoteDetail = findViewById(R.id.existNote1);

        editNote = findViewById(R.id.noteedit);
        existTextDateTime = findViewById(R.id.existDateTime);
        existSelectedImage = findViewById(R.id.imageExist);
        existLocationText = findViewById(R.id.locationText1);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        //imgStorageReference = storageReference.child("photos/" + name);


        Intent data = getIntent();

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("image", data.getStringExtra("image"));
                intent.putExtra("time", data.getStringExtra("time"));
                intent.putExtra("location", data.getStringExtra("location"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                view.getContext().startActivity(intent);
            }
        });
        ImageView imageBacknote = findViewById(R.id.imageBack1);
        imageBacknote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(noteDetails.this, ExistUserMainPage.class));;
            }
        });
        existTitleDetail.setText(data.getStringExtra("title"));
        existNoteDetail.setText(data.getStringExtra("content"));
        existTextDateTime.setText(data.getStringExtra("time"));
        existLocationText.setText(data.getStringExtra("location"));

        if(data.getStringExtra("image") != null){
            Picasso.get().load(Uri.parse(data.getStringExtra("image"))).into(existSelectedImage);
        }
    }


}
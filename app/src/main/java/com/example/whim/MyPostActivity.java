package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPostActivity extends AppCompatActivity {
    private TextView postTitle, postcontent, numlikefrommypost;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    String postImgUri, postImgName;
    Button postLocationText;
    TextView postTextDateTime;
    ImageView postImage, deletepost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        getSupportActionBar().hide();

        postTitle = findViewById(R.id.posttitle);
        postcontent = findViewById(R.id.postexist);

        postTextDateTime = findViewById(R.id.postDateTime);
        postImage = findViewById(R.id.postimage);
        postLocationText = findViewById(R.id.locationpost);
        numlikefrommypost = findViewById(R.id.numlikefrommypost);

        deletepost = findViewById(R.id.deletepost);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent data = getIntent();


        String titlepost = data.getStringExtra("title");
        String contentpost = data.getStringExtra("content");
        String locationpost = data.getStringExtra("location");
        String timepost = data.getStringExtra("time");
        String imgpost = data.getStringExtra("image");
        String imageNamepost = data.getStringExtra("imagename");
        String postID = data.getStringExtra("postId");
        ArrayList<String> curlikedusers = data.getStringArrayListExtra("likedusers");

        int numlikes = data.getExtras().getInt("numlikes");
//        final Integer[] numlikes = {data.getExtras().getInt("numlikes")};

        postTitle.setText(data.getStringExtra("title"));
        postcontent.setText(data.getStringExtra("content"));
        postTextDateTime.setText(data.getStringExtra("time"));
        postLocationText.setText(data.getStringExtra("location"));
        numlikefrommypost.setText(String.valueOf(numlikes));

        if (data.getStringExtra("image") != null) {
            StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(postImage);
                }
            });

            ImageView backpost = findViewById(R.id.backpost);
            backpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MyPostActivity.this, ProfileActivity.class));

                }
            });

        }

        deletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = firebaseFirestore.collection("posts").document(postID);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(view.getContext(), "Your post is deleted.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MyPostActivity.this, ProfileActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Your post failed to be deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ImageView imageBacknote = findViewById(R.id.backpost);
        imageBacknote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //startActivity(new Intent(noteDetails.this, ExistUserMainPage.class));;
            }
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(MyPostActivity.this, ProfileActivity.class));
    }



}
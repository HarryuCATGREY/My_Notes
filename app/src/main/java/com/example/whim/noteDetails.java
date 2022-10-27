package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import io.grpc.Context;

public class noteDetails extends AppCompatActivity {


    private TextView existTitleDetail, existNoteDetail;
    ImageView editNote;
    Button existLocationText;
    TextView existTextDateTime;
    //TextView existLocationText;
    ImageView existSelectedImage, existdeletenote;
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
        existTextDateTime = findViewById(R.id.textDateTime);
        existSelectedImage = findViewById(R.id.imageExist);
        existLocationText = findViewById(R.id.location1);
        existdeletenote = findViewById(R.id.deletenote);



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
                intent.putExtra("timestamp", data.getStringExtra("timestamp"));
                intent.putExtra("imagename", data.getStringExtra("imagename"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                view.getContext().startActivity(intent);
            }
        });

        existdeletenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reminder to delete note
                AlertDialog.Builder builder = new AlertDialog.Builder(noteDetails.this);

                builder.setCancelable(true);
                builder.setTitle("Are you sure to delete the note?");
                builder.setMessage("The process can't revert!");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(), "Your whim is deleted.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(noteDetails.this, ExistUserMainPage.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Your whim failed to be deleted.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.show();

//                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
//                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(view.getContext(), "Your whim is deleted.", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(noteDetails.this, ExistUserMainPage.class));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(view.getContext(), "Your whim failed to be deleted.", Toast.LENGTH_SHORT).show();
//                    }
//                });
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
            StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(existSelectedImage);
                }
            });

//            addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()) {
//                        Uri downUri = task.getResult();
//                        String imageUrl = downUri.toString();
//                        Picasso.get().load(imageUrl).into(existSelectedImage);
//                    }
//                }
//            });
        }
    }


}
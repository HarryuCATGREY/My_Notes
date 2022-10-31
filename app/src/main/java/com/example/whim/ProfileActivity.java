package com.example.whim;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.whim.Models.DrawableUtil;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageButton coverImage;
    ImageButton editProfile;
    ImageView profilePic;
    ImageView postImage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    ImageButton like;
    ImageButton home;
    ImageButton profile;
    ImageButton community;
    ImageButton logOut;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_PERM_CODE = 1;
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final int LOCATION_REQUEST_CODE = 100;
    private static final String SHARED_PREFS = "sharedPrefs";

    private String imageUri;
    private String imageName;
    private TextView nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        getSupportActionBar().hide();
        editProfile = findViewById(R.id.changeprofile);
        profilePic = findViewById(R.id.profilepic);
        nameText = findViewById(R.id.textName);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        like = findViewById(R.id.like);
        profile = findViewById(R.id.profile);
        logOut = findViewById(R.id.logOut);

        Intent data = getIntent();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name","");
                editor.apply();

                startActivity(new Intent(getApplicationContext(), ExistLoginActivity.class));
                finish();
            }
        });


        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PostActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ExistUserMainPage.class));
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
//                intent.putExtra("image", data.getStringExtra("image"));
//                v.getContext().startActivity(intent);
                Toast.makeText(getApplicationContext(), "Camera button clicked.", Toast.LENGTH_SHORT).show();
                askGalleryPermissions();
                editUpload();
            }
        });

        if(data.getStringExtra("image") != null) {
            Toast.makeText(getApplicationContext(), "yes profile", Toast.LENGTH_SHORT).show();
            StorageReference imgReference = storageReference.child("profile/").child(data.getStringExtra("imagename"));
            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePic);
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "No profile", Toast.LENGTH_SHORT).show();
        }



        }

    private void editUpload() {
        String content = nameText.getText().toString();
        String imgUri = imageUri;
        String imgName = imageName;

        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile").document();
        Map<String, Object> note = new HashMap<>();

        note.put("content", content);
        note.put("image",imgUri);
        note.put("imagename", imgName);

        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Intent data = getIntent();
                if(data.getStringExtra("image") != null) {
                    Toast.makeText(getApplicationContext(), "profile pic present", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getApplicationContext(), "Your whim is safely stored :)", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ExistNewNoteActivity.this, ExistUserMainPage.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to store whim, please try again later :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askGalleryPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
        else {
            getGallery();
        }
    }

    private void getGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                //selectedImage.setImageURI(contentUri);
                imageUri = contentUri.toString();
                imageName = imageFileName;
                uploadImageToFirebase(imageFileName, contentUri);

            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("profile/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
                        Log.d("tag", "onSuccess: Upload image URL is: " + uri.toString());
                        //DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile").document();
                    }
                });
                Toast.makeText(getApplicationContext(), "Photo is uploaded! :) ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed :( ", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

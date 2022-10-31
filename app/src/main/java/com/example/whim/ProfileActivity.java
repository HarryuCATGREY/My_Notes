package com.example.whim;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

//import com.example.whim.Models.DrawableUtil;
//import com.google.android.gms.cast.framework.media.ImagePicker;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    ImageButton editbtn;

    RecyclerView mypostrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirestoreRecyclerAdapter<postmodel, MyPostViewHolder> mypostAdapter;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    ImageButton like;
    ImageButton home;
    ImageButton profile;
    ImageButton community;
    Button logOut;
    ArrayList<String> idlist = new ArrayList<String>();
    ArrayList<String> thisid = new ArrayList<String>();


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
        editbtn = findViewById(R.id.edit);

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


        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                builder.setCancelable(true);
                builder.setTitle("Edit Profile Name");
                builder.setMessage("Please input your new profile name");

                final EditText input = new EditText(ProfileActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        nameText.setText(input.getText().toString());
                        editUpload();
                    }
                });
                builder.show();

            }
        });


        editProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Camera button clicked.", Toast.LENGTH_SHORT).show();
                askGalleryPermissions();
            }
        });

        // check if the profile collection exists
        CollectionReference currprofile = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile");
        currprofile.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    nameText.setText("Whim User");
                    Toast.makeText(getApplicationContext(),"Collection is Empty",Toast.LENGTH_SHORT).show();
                }

                if(!queryDocumentSnapshots.isEmpty()){
                    currprofile.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    String s = document.getId();
                                    thisid.add(s);
                                }
                                DocumentReference currprofile = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile").document(thisid.get(0));
                                currprofile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            nameText.setText(doc.get("content").toString());

                                            if (doc.get("image") != null) {
                                                StorageReference profilepicReference = storageReference.child("profile/" + doc.get("imagename"));
                                                profilepicReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Picasso.get().load(uri).into(profilePic);
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            }
        });


        Query mypostquery = firebaseFirestore.collection("posts").whereEqualTo("uid", firebaseUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<postmodel> allmyposts = new FirestoreRecyclerOptions.Builder<postmodel>().setQuery(mypostquery, postmodel.class).build();

        mypostAdapter = new FirestoreRecyclerAdapter<postmodel, MyPostViewHolder>(allmyposts){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull MyPostViewHolder mypostViewHolder, int i, @NonNull postmodel postmodel) {

                mypostViewHolder.posttitle.setText(postmodel.getTitle());
                mypostViewHolder.postcontent.setText(postmodel.getContent());

                if (postmodel.getImage() != null){
                    StorageReference imgReference = storageReference.child("photos/").child(postmodel.getImagename());
                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(mypostViewHolder.postimgview);
                        }
                    });
                }
                String postId = mypostAdapter.getSnapshots().getSnapshot(i).getId();
                mypostViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), MyPostActivity.class);
                        intent.putExtra("title",postmodel.getTitle());
                        intent.putExtra("content",postmodel.getContent());
                        intent.putExtra("image", postmodel.getImage());
                        intent.putExtra("time", postmodel.getTime());
                        intent.putExtra("location", postmodel.getLocation());
                        intent.putExtra("likedusers", postmodel.getLikedusers());
                        intent.putExtra("timestamp", postmodel.getTimestamp());
                        intent.putExtra("imagename", postmodel.getImagename());
                        intent.putExtra("uid", postmodel.getUid());
                        intent.putExtra("postId", postId);
                        view.getContext().startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_notes_list_pic,parent, false);
                return new MyPostViewHolder(view);
            }
        };

        mypostrecyclerview = findViewById(R.id.recyclerViewmypost);
        mypostrecyclerview.setHasFixedSize(false);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mypostrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mypostrecyclerview.setAdapter(mypostAdapter);
        mypostAdapter.notifyDataSetChanged();
    }

    private void editUpload() {
        String content = nameText.getText().toString();
        CollectionReference profileRef = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile");

        profileRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile").document();
                    Map<String, Object> note = new HashMap<>();

                    note.put("content", content);
                    note.put("image",imageUri);
                    note.put("imagename", imageName);

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
                }else{
                    profileRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    String s = document.getId();
                                    idlist.add(s);
                                }
                            }
                            DocumentReference profileReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("profile").document(idlist.get(0));
                            Map<String, Object> note = new HashMap<>();

                            note.put("content", content);
                            note.put("image",imageUri);
                            note.put("imagename", imageName);

                            profileReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Your whim is safely stored :)", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to store whim, please try again later :(", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
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
                editUpload();
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
                    }
                });
                Toast.makeText(getApplicationContext(), "Profile updated! :) ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Update failed :( ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {

    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder{

        private TextView posttitle;
        private TextView postcontent;
        private TextView posttime;
        private ImageView postimgview;
        private ConstraintLayout postcolour;


        LinearLayout mpost;
        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            posttitle = itemView.findViewById(R.id.exist_title);
            postcontent = itemView.findViewById(R.id.note_content);
            postimgview = itemView.findViewById(R.id.postimgview);
            mpost = itemView.findViewById(R.id.whim);
            postcolour = itemView.findViewById(R.id.post_colour);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mypostAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mypostAdapter != null){
            mypostAdapter.stopListening();
        }
    }
}
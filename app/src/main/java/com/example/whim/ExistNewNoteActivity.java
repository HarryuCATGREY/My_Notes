package com.example.whim;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
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
import android.content.Intent;


import com.example.whim.Models.Notes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ExistNewNoteActivity extends AppCompatActivity {
    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;
    private ImageView selectedImage;
    private String imageUri;
    private String imageName;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    Notes notes;
    boolean isOldNote = false;

    TextView textProgress;
    ProgressBar progressBar;

    ImageButton cameraBtn, galleryBtn, paletteBtn;
    Button locationBtn;
    String currentPhotoPath;

    TextView locationText;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_PERM_CODE = 1;
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final int LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_acticity);

        textProgress = findViewById(R.id.textProgress);
        progressBar = findViewById(R.id.progress_loader);

        progressBar.setVisibility(View.GONE);
        textProgress.setVisibility(View.GONE);


        inputNoteTitle = findViewById(R.id.storedTitle);
        inputNoteText = findViewById(R.id.storedNote);
        textDateTime = findViewById(R.id.textDateTime1);
        selectedImage = findViewById(R.id.imageExist1);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cameraBtn = findViewById(R.id.camera11);
        galleryBtn = findViewById(R.id.gallery11);
        paletteBtn = findViewById(R.id.exist_palette);
        locationBtn =findViewById(R.id.location2);

        SimpleDateFormat formatterTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a");

        Date notesDate = Calendar.getInstance().getTime();

        storageReference = FirebaseStorage.getInstance().getReference();

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            inputNoteTitle.setText(notes.getTitle());
            inputNoteText.setText(notes.getNotes());
            isOldNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Note save button
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ExistNewNoteActivity.this);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera button clicked.", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askGalleryPermissions();
            }
        });

        paletteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPalette();
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askLocationPermissions();
            }
        });


        // Note save button

        ImageView imageBackedit2 = findViewById(R.id.imageBack2);
        imageBackedit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistNewNoteActivity.this, ExistUserMainPage.class));
            }
        });

        ImageView imageSave = findViewById(R.id.editSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputNoteTitle.getText().toString();
                String content = inputNoteText.getText().toString();
                String imgUri = imageUri;
                String imgName = imageName;
                String time = textDateTime.getText().toString();
                String location = locationBtn.getText().toString();
                ArrayList<String> searchkeyword = generateKeyword(title);

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please add title and content before save.", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    try {
                        Date realStamp = formatterTime.parse(time);
                        note.put("timestamp", realStamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    note.put("title", title);
                    note.put("content", content);
                    note.put("image",imgUri);
                    note.put("time",time);
                    note.put("location", location);
                    note.put("searchkeyword", searchkeyword);
                    note.put("imagename", imgName);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(getApplicationContext(), "Your whim is safely stored :)", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ExistNewNoteActivity.this, ExistUserMainPage.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to store whim, please try again later :(", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void getPalette() {
        startActivity(new Intent(ExistNewNoteActivity.this, drawController.class));
    }

    private void askGalleryPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ExistNewNoteActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
        else {
            getGallery();
        }
    }

    private void getGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    private void askLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else {
            getLocation();
        }
    }

    // Get Location part
    @SuppressLint("MissingPermission")
    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override

            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    // Initialize geoCoder
                    Geocoder geocoder = new Geocoder(ExistNewNoteActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        // Set address
                        Log.d("address", addresses.get(0).getAddressLine(0));
                        locationBtn.setText(addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(ExistNewNoteActivity.this, "Location null error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Ask camera to take photo permission
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }


    // Check the permission of camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGallery();
            }
            else {
                Toast.makeText(this, "Gallery Permission is Required to Use photo.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
            else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1]== PackageManager.PERMISSION_GRANTED)) {
                getLocation();
            }
            else {
                Toast.makeText(this, "Location Permission is Required to Use location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                textProgress.setVisibility(View.VISIBLE);

                File f = new File(currentPhotoPath);
                //selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                // 能不能吧image的uri存成string再之后转换
                imageUri = Uri.fromFile(f).toString();
                imageName = f.getName();
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                progressBar.setVisibility(View.VISIBLE);
                textProgress.setVisibility(View.VISIBLE);

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
        StorageReference image = storageReference.child("photos/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(selectedImage);
                        Log.d("tag", "onSuccess: Upload image URL is: " + uri.toString());
                    }
                });
                progressBar.setProgress(0);
                textProgress.setText("0.0 %");
                Toast.makeText(getApplicationContext(), "Photo is uploaded! :) ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                textProgress.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed :( ", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressBar.setProgress((int)progress);
                textProgress.setText(progress+" %");
            }
        });


    }

    // we will see this in storage:  images/image.jpeg....


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.whim.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    public ArrayList<String> generateKeyword(String title){
        ArrayList<String> keywords = new ArrayList<String>();
        for(int i = 0; i < title.length() - 1; i++){
            for(int j = i+1; j <= title.length(); j++){
                keywords.add(title.substring(i,j).toLowerCase(Locale.ROOT).trim());
            }
        }
        return keywords;
    }
}
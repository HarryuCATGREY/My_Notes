package com.example.whim;

import static com.example.whim.ExistNewNoteActivity.CAMERA_PERM_CODE;
import static com.example.whim.ExistNewNoteActivity.CAMERA_REQUEST_CODE;
import static com.example.whim.ExistNewNoteActivity.GALLERY_REQUEST_CODE;
import static com.example.whim.ExistNewNoteActivity.LOCATION_REQUEST_CODE;

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
import android.content.Intent;
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
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private static final int GALLERY_PERM_CODE = 1;
    ImageButton cameraBtnedit, galleryBtnedit, locationBtnedit;
    Intent data;
    EditText editTitle, editContent;
    TextView editDate;
    Button editLocation;
    ImageView editImg;
    ImageView saveUpdate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String newUri;
    String currentPhotoPath;
    StorageReference storageReference;
    FusedLocationProviderClient fusedLocationProviderClient2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_acticity);
        editTitle = findViewById(R.id.storedTitle);
        editContent = findViewById(R.id.storedNote);
        editDate = findViewById(R.id.textDateTime1);
        editLocation = findViewById(R.id.location2);
        editImg = findViewById(R.id.imageExist1);

        saveUpdate = findViewById(R.id.editSave);
        data = getIntent();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        // Camera, gallery and location button
        cameraBtnedit = findViewById(R.id.camera11);
        galleryBtnedit = findViewById(R.id.gallery11);
        TextView inputNoteText2 = (TextView)findViewById(R.id.storedNote);

        String img = getColoredSpanned("images", "#67B1F9");
        String txt = getColoredSpanned("text","#FFCA3A");
        String photos = getColoredSpanned("photos","#6E80FA");
        inputNoteText2.setHint(Html.fromHtml("What is on your mind today? You can insert "+img+", "+txt+", or upload "+photos+"."));


        String currTitle  = data.getStringExtra("title");
        String currNote = data.getStringExtra("content");
        String currLocation = data.getStringExtra("location");
        String currTime = data.getStringExtra("time");
        String currImg = data.getStringExtra("image");

        newUri = currImg;

        editTitle.setText(currTitle);
        editContent.setText(currNote);

        editDate.setText(currTime);

        editLocation.setText(currLocation);
        // 可能有问题
        if(currImg != null){
            Picasso.get().load(Uri.parse(currImg)).into(editImg);
        }

        fusedLocationProviderClient2 = LocationServices.getFusedLocationProviderClient(EditNoteActivity.this);

        cameraBtnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera button clicked.", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });

        galleryBtnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askGalleryPermissions();
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askLocationPermissions();
            }
        });


        ImageView imageBackedit = findViewById(R.id.imageBack2);
        imageBackedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newtitle = editTitle.getText().toString();
                String newcontent = editContent.getText().toString();
                String newimg = newUri;
                String newlocation = editLocation.getText().toString();


                // String newImg = currImg;
                // image 要不别改了

                if(newtitle.isEmpty() ||newcontent.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Whim sections cannot be empty!",Toast.LENGTH_SHORT).show();

                }else{
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newtitle);
                    note.put("content",newcontent);
                    note.put("image", newimg);
                    note.put("location", newlocation);
                    note.put("time", currTime);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Whim updated :)",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNoteActivity.this, ExistUserMainPage.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Whim update failed :(",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });




    }
    private void askGalleryPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditNoteActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
        else {
            getGallery();
        }
    }

    private void getGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                //selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                // 能不能吧image的uri存成string再之后转换
                newUri = Uri.fromFile(f).toString();

                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                //selectedImage.setImageURI(contentUri);
                newUri = contentUri.toString();

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
                        Picasso.get().load(uri).into(editImg);
                        Log.d("tag", "onSuccess: Upload image URL is: " + uri.toString());
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

    // we will see this in storage:  images/image.jpeg....


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    // Get Location part
    @SuppressLint("MissingPermission")
    private void getLocation() {

        fusedLocationProviderClient2.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override

            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    // Initialize geoCoder
                    Geocoder geocoder = new Geocoder(EditNoteActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        // Set address
                        Log.d("address", addresses.get(0).getAddressLine(0));
                        editLocation.setText(addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(EditNoteActivity.this, "Location null error", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void askLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else {
            getLocation();
        }
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
    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;


    }
}
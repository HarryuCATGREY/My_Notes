package com.example.whim;

import static com.example.whim.ExistNewNoteActivity.CAMERA_PERM_CODE;
import static com.example.whim.ExistNewNoteActivity.CAMERA_REQUEST_CODE;
import static com.example.whim.ExistNewNoteActivity.GALLERY_REQUEST_CODE;
import static com.example.whim.ExistNewNoteActivity.LOCATION_REQUEST_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// text recog supporters
import android.widget.PopupWindow;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.IOException;

public class EditNoteActivity<Login> extends AppCompatActivity {

    private static final int GALLERY_PERM_CODE = 1;
    ImageButton cameraBtnedit, galleryBtnedit, locationBtnedit, paletteBtnexdit;
    Intent data;
    EditText editTitle, editContent;
    TextView editDate;
    Button editLocation;
    ImageView editImg;
    ImageView saveUpdate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    TextView textViewProgress;
    Date prevDate;

    FirebaseUser firebaseUser;
    String newUri, newImagename;
    String currentPhotoPath;
    StorageReference storageReference;
    FusedLocationProviderClient fusedLocationProviderClient2;

    // Text recog
    ImageView IVPreviewImage;  // preview for debug
    Button test_recog, image_input;  //  trigger button
    Bitmap picture_recog;  // Image for recog
    EditText vedt=null,edPop; // output text window
    Button btOk=null;
    //Button cancelbtn=null;
    String recog_text;  // output string
    View popup_view;  // view for popup



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_acticity);
        editTitle = findViewById(R.id.storedTitle);
        editContent = findViewById(R.id.storedNote);
        editDate = findViewById(R.id.textDateTime1);
        editLocation = findViewById(R.id.location2);
        editImg = findViewById(R.id.imageExist1);



        //IVPreviewImage = findViewById(R.id.IVPreviewImage);  // debug preview

        progressBar = findViewById(R.id.progress_loader);
        textViewProgress = findViewById(R.id.textProgress);

        progressBar.setVisibility(View.GONE);
        textViewProgress.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();

        saveUpdate = findViewById(R.id.editSave);
        data = getIntent();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        // Camera, gallery and location button
        cameraBtnedit = findViewById(R.id.camera11);
        galleryBtnedit = findViewById(R.id.gallery11);
        paletteBtnexdit = findViewById(R.id.exist_palette);

        TextView inputNoteText = (TextView)findViewById(R.id.storedNote);

        String img = getColoredSpanned("images", "#67B1F9");
        String txt = getColoredSpanned("text","#FFCA3A");
        String photos = getColoredSpanned("doodles","#6E80FA");
        inputNoteText.setHint(Html.fromHtml("What is on your mind today? You can insert "+img+", "+txt+", or draw "+photos+"."));

        SimpleDateFormat formatterTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a");


        String currTitle  = data.getStringExtra("title");
        String currNote = data.getStringExtra("content");
        String currLocation = data.getStringExtra("location");
        String currTime = data.getStringExtra("time");
        String currImg = data.getStringExtra("image");
        String currImgName = data.getStringExtra("imagename");
        try {
            prevDate = formatterTime.parse(data.getStringExtra("time"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newUri = currImg;
        newImagename = currImgName;

        editTitle.setText(currTitle);
        editContent.setText(currNote);

        editDate.setText(currTime);

        editLocation.setText(currLocation);
        // 可能有问题
        if(currImg != null){
            if(data.getStringExtra("image") != null){
                StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
                //StorageReference imgReference = storageReference.child("photos/").child(data.getStringExtra("imagename"));
                imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(editImg);
                    }

//                imgReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if(task.isSuccessful()) {
//                            Uri downUri = task.getResult();
//                            String imageUrl = downUri.toString();
//                            Picasso.get().load(imageUrl).into(editImg);
//                        }
//                    }
                });
           // Picasso.get().load(Uri.parse(currImg)).into(editImg);
        }
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

        paletteBtnexdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPalette();
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
                String newimgname = newImagename;
                String newlocation = editLocation.getText().toString();
                ArrayList<String> newsearchkeyword = generateKeyword(newtitle);

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
                    note.put("timestamp",prevDate);
                    note.put("imagename", newimgname);
                    note.put("searchkeyword", newsearchkeyword);

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

    private void getPalette() {
        startActivity(new Intent(EditNoteActivity.this, drawController.class));
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

                progressBar.setVisibility(View.VISIBLE);
                textViewProgress.setVisibility(View.VISIBLE);

                Log.d("happy", "progressbar visibility before is" + progressBar.getVisibility());
                progressBar.setVisibility(View.VISIBLE);
                textViewProgress.setVisibility(View.VISIBLE);
                Log.d("happy", "progressbar visibility  after is" + progressBar.getVisibility());


                File f = new File(currentPhotoPath);
                //selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                // 能不能吧image的uri存成string再之后转换
                newUri = Uri.fromFile(f).toString();
                newImagename = f.getName();

                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                progressBar.setVisibility(View.VISIBLE);
                textViewProgress.setVisibility(View.VISIBLE);

                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                //selectedImage.setImageURI(contentUri);
                newUri = contentUri.toString();
                newImagename = imageFileName;

                Log.d("imagefile", imageFileName);
                uploadImageToFirebase(imageFileName, contentUri);

            }
        }
    }


    private void uploadImageToFirebase(String name, Uri contentUri){
        Log.d("photo", "123");
        Log.d("photoname", name);
        StorageReference image = storageReference.child("photos/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressBar.setProgress((0));
                        textViewProgress.setText("Uploaded 100%");
                        Picasso.get().load(uri).into(editImg);
                        Log.d("tag", "onSuccess: Upload image URL is: " + uri.toString());
                    }
                });

                Toast.makeText(getApplicationContext(), "Photo is uploaded! :) ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                textViewProgress.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed :( ", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot snapshot) {
                double progress = (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
                textViewProgress.setText(progress+" %");
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
    public ArrayList<String> generateKeyword(String title){
        ArrayList<String> keywords = new ArrayList<String>();
        for(int i = 0; i < title.length() - 1; i++){
            for(int j = i+1; j <=title.length(); j++){
                keywords.add(title.substring(i,j).toLowerCase(Locale.ROOT).trim());
            }
        }
        return keywords;
    }


    // choose input image
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }


    // analyze the image
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();

                        }
                        //IVPreviewImage.setImageBitmap(selectedImageBitmap);

                        recognizeText(InputImage.fromBitmap(selectedImageBitmap, 0));

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Recognization", recog_text);
                        clipboard.setPrimaryClip(clip);

                        if(recog_text == null){
                            Toast.makeText(getApplicationContext(), "no text detected", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Content copied to clipboard", Toast.LENGTH_SHORT).show();
                        }

                        //editContent.setText(editContent.getText()+recog_text);
                    }
                }
            });

    // text recog
    private void recognizeText(InputImage image) {
        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END get_detector_default]
        // [START run_detector]
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(visionText -> {
                            // Task completed successfully
                            // [START_EXCLUDE]
                            // [START get_text]
                            // [START mlkit_process_text_block]
                            recog_text = "";
                            for (Text.TextBlock block : visionText.getTextBlocks()) {
                                String blockText = block.getText();
                                recog_text += blockText + "\n";
                                Point[] blockCornerPoints = block.getCornerPoints();
                                Rect blockFrame = block.getBoundingBox();
                                for (Text.Line line : block.getLines()) {
                                    String lineText = line.getText();
                                    Point[] lineCornerPoints = line.getCornerPoints();
                                    Rect lineFrame = line.getBoundingBox();
                                    for (Text.Element element : line.getElements()) {
                                        String elementText = element.getText();
                                        Point[] elementCornerPoints = element.getCornerPoints();
                                        Rect elementFrame = element.getBoundingBox();
                                        for (Text.Symbol symbol : element.getSymbols()) {
                                            String symbolText = symbol.getText();
                                            Point[] symbolCornerPoints = symbol.getCornerPoints();
                                            Rect symbolFrame = symbol.getBoundingBox();
                                        }
                                    }
                                }
                            }
                            // [END get_text]
                            // [END_EXCLUDE]
                        })
                        .addOnFailureListener(e -> {});
        // [END run_detector]
    }

    // popup window
    public void onButtonShowPopupWindowClick(View view) {
        popup_view = view;
        imageChooser();
    }
}
package com.example.whim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    Intent data;
    EditText editTitle, editContent;
    ImageView saveUpdate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note_acticity);
        editTitle = findViewById(R.id.storedTitle);
        editContent = findViewById(R.id.storedNote);
        saveUpdate = findViewById(R.id.editSave);
        data = getIntent();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        TextView inputNoteText2 = (TextView)findViewById(R.id.storedNote);

        String img = getColoredSpanned("images", "#67B1F9");
        String txt = getColoredSpanned("text","#FFCA3A");
        String photos = getColoredSpanned("photos","#6E80FA");
        inputNoteText2.setHint(Html.fromHtml("What is on your mind today? You can insert "+img+", "+txt+", or upload "+photos+"."));





        String currTitle  = data.getStringExtra("title");
        String currNote = data.getStringExtra("content");
        editTitle.setText(currTitle);
        editContent.setText(currNote);

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

                if(newtitle.isEmpty() ||newcontent.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Whim sections cannot be empty!",Toast.LENGTH_SHORT).show();

                }else{
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newtitle);
                    note.put("content",newcontent);
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

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

}
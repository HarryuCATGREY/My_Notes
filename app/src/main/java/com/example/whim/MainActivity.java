package com.example.whim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whim.Adapters.NoteListAdapter;
import com.example.whim.Database.RoomDB;
import com.example.whim.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchView_home;
    Notes selectedNote;
    ImageButton like, community, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("101", "!!!");
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle_home);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);
        community = findViewById(R.id.community);
        profile = findViewById(R.id.buttonMe);
        like = findViewById(R.id.like);

        // setting database
        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();
        updateRecycler(notes);


        TextView home_title = (TextView)findViewById(R.id.textView);

        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i","#6E80FA");
        String dot = getColoredSpanned(".","#FFCA3A");
        home_title.setText(Html.fromHtml("Today's W"+h+i+"m"+dot));



        // add new notes button listener
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);

            }
        });
        // the search function
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminder();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminder();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminder();
            }
        });
    }

    public void onBackPressed() {

    }

    private void reminder() {

        ImageView cancel;
        Button signUp;
        //will create a view of our custom dialog layout
        View alertCustomdialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_reminder,null);
        //initialize alert builder.
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(alertCustomdialog);
        cancel = (ImageView)alertCustomdialog.findViewById(R.id.cancel_button);
        signUp = alertCustomdialog.findViewById(R.id.signUp_button);
        final AlertDialog dialog = alert.create();
        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //finally show the dialog box in android all
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }


    // Goto Personal Information
    public void goToPersonalInformation(View view) {
        // Personal information
        Intent myIntent = new Intent(MainActivity.this, GuestPersonalInformation.class);
        //myIntent.putExtra("key", 1); //Optional parameters
        MainActivity.this.startActivity(myIntent);

        ImageButton buttonMe = findViewById(R.id.buttonMe);
        buttonMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GuestPersonalInformation.class);
                view.getContext().startActivity(intent);}
        });
    }


    // click on old notes
    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }
        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }


    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())
            || singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        noteListAdapter.filterList((filteredList));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();

            }
        }

        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra(("note"));
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delet:
                Log.d("101", "!!!!");
                database.mainDAO().delet(selectedNote);
                notes.remove(selectedNote);
                noteListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
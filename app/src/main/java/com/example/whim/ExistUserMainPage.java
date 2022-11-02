package com.example.whim;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whim.Adapters.NoteListAdapter;
import com.example.whim.Models.Notes;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExistUserMainPage extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    //RecyclerView recyclerView_exist;
    NoteListAdapter noteListAdapterExist;
    List<Notes> notes = new ArrayList<>();
    Button fab_add_exist;
    SearchView search_home_exist;
    Notes selectedNote;
    Date notesDate;
    ImageButton like;
    ImageButton home;
    ImageButton profile;
    ImageButton community;
    Query query;
    FirestoreRecyclerOptions<firebasemodel> allusernotes;

    TextView todayDate;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_exist_user_main_page);

//        initialise button and view
        fab_add_exist = findViewById(R.id.fab_add_exist);
        search_home_exist = findViewById(R.id.search_home_exist);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        community, home, like, profile button
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        like = findViewById(R.id.like);
        profile = findViewById(R.id.profile);

//        set date
        todayDate = findViewById(R.id.todayDate);
        todayDate.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

//        set home title
        TextView home_title = (TextView) findViewById(R.id.home_title);
//        change whim color
        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i", "#6E80FA");
        String dot = getColoredSpanned(".", "#FFCA3A");
        home_title.setText(Html.fromHtml("Today's W" + h + i + "m" + dot));

        notesDate = Calendar.getInstance().getTime();

        // add new notes button listener
        fab_add_exist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistUserMainPage.this, ExistNewNoteActivity.class));
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExistUserMainPage.this, PostActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExistUserMainPage.this, ProfileActivity.class));
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistUserMainPage.this, WhatYouLikedActivity.class));

            }
        });

        query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("timestamp", Query.Direction.DESCENDING);

        allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {
                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());
                noteViewHolder.notetime.setText(firebasemodel.getTime());

                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), noteDetails.class);
                        intent.putExtra("title", firebasemodel.getTitle());
                        intent.putExtra("content", firebasemodel.getContent());
                        intent.putExtra("image", firebasemodel.getImage());
                        intent.putExtra("time", firebasemodel.getTime());
                        intent.putExtra("location", firebasemodel.getLocation());
                        intent.putExtra("searchkeyword", firebasemodel.getSearchkeyword());
                        intent.putExtra("timestamp", firebasemodel.getTimestamp());
                        intent.putExtra("imagename", firebasemodel.getImagename());
                        intent.putExtra("noteId", docId);

                        view.getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exist_notes_list, parent, false);
                return new NoteViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();


            }
        };

//        set recycle view
        mrecyclerview = findViewById(R.id.recycle_home_exist);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

        search_home_exist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //String currText;
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }

        });
    }

//    change text color
    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView notetitle;
        private final TextView notecontent;
        private final TextView notetime;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.exist_title);
            notecontent = itemView.findViewById(R.id.note_content);
            notetime = itemView.findViewById(R.id.textView3);
            mnote = itemView.findViewById(R.id.whim);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delet) {
            notes.remove(selectedNote);
            noteListAdapterExist.notifyDataSetChanged();
            Toast.makeText(ExistUserMainPage.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delet) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(ExistUserMainPage.this, ExistLoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

//    search for note's title
    private void search(String newText) {
        query = firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes")
                .orderBy("title", Query.Direction.ASCENDING)
                .startAt(newText);

        allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>()
                .setQuery(query, firebasemodel.class)
                .build();

        noteAdapter.updateOptions(allusernotes);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }
}


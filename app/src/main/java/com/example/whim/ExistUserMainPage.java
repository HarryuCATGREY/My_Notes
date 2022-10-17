package com.example.whim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.whim.Adapters.NoteListAdapter;
import com.example.whim.Database.RoomDB;
import com.example.whim.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExistUserMainPage extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView_exist;
    NoteListAdapter noteListAdapterExist;
    List<Notes> notes = new ArrayList<>();
    FloatingActionButton fab_add_exist;
    SearchView searchView_home_exist;
    Notes selectedNote;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_user_main_page);

        recyclerView_exist = findViewById(R.id.recycle_home_exist);
        fab_add_exist = findViewById(R.id.fab_add_exist);
        searchView_home_exist = findViewById(R.id.searchView_home_exist);

        //firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All Notes");
        // setting database


        // add new notes button listener
        fab_add_exist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistUserMainPage.this, ExistNewNoteActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.popup_menu,menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delet:
                //database.mainDAO().delet(selectedNote);
                notes.remove(selectedNote);
                noteListAdapterExist.notifyDataSetChanged();
                Toast.makeText(ExistUserMainPage.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.delet:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ExistUserMainPage.this, ExistLoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

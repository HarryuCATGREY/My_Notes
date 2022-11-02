package com.example.whim;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    RecyclerView postrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    TextView posttodayDate;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    ImageButton like;
    ImageButton home;
    ImageButton profile;
    ImageButton community;

    SearchView searchPost;

    FirestoreRecyclerAdapter<postmodel, PostViewHolder> postAdapter;

    Query postquery;
    FirestoreRecyclerOptions<postmodel> allposts;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().hide();

//        initialise button and view
        posttodayDate = findViewById(R.id.todaypostDate);
        searchPost = findViewById(R.id.search_post);
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        like = findViewById(R.id.like);
        profile = findViewById(R.id.profile);

//        initialise firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

//        bottom button setting
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, ExistUserMainPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, ProfileActivity.class));
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, WhatYouLikedActivity.class));

            }
        });

//        show whim title and change its color
        TextView login_title = (TextView) findViewById(R.id.textView5);
        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i", "#6E80FA");
        String dot = getColoredSpanned(".", "#FFCA3A");
        login_title.setText(Html.fromHtml("Community W" + h + i + "m" + dot));

//        set date
        posttodayDate.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

//        query for firebase to get post
        postquery = firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        allposts = new FirestoreRecyclerOptions.Builder<postmodel>().setQuery(postquery, postmodel.class).build();
        postAdapter = new FirestoreRecyclerAdapter<postmodel, PostViewHolder>(allposts) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull postmodel postmodel) {

                postViewHolder.posttitle.setText(postmodel.getTitle());
                postViewHolder.postcontent.setText(postmodel.getContent());

                if (postmodel.getImage() != null) {
                    StorageReference imgReference = storageReference.child("photos/").child(postmodel.getImagename());
                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(postViewHolder.postimgview);
                        }
                    });
                }
                String postId = postAdapter.getSnapshots().getSnapshot(i).getId();
                postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), postDetails.class);
                        intent.putExtra("title", postmodel.getTitle());
                        intent.putExtra("content", postmodel.getContent());
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
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_notes_list_pic, parent, false);
                return new PostViewHolder(view);
            }
        };

//        recycle view for posts
        postrecyclerview = findViewById(R.id.recycle_post);
        postrecyclerview.setHasFixedSize(false);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        postrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        postrecyclerview.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();

//        search post
        searchPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

    public void onBackPressed() {

    }

//    showing post
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView posttitle;
        private final TextView postcontent;
        private final ImageView postimgview;

        LinearLayout mpost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            posttitle = itemView.findViewById(R.id.exist_title);
            postcontent = itemView.findViewById(R.id.note_content);
            postimgview = itemView.findViewById(R.id.postimgview);
            mpost = itemView.findViewById(R.id.whim);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (postAdapter != null) {
            postAdapter.stopListening();
        }
    }

//    change text color
    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

//    search post
    @SuppressLint("NotifyDataSetChanged")
    private void search(String newText) {
        postquery = firebaseFirestore.collection("posts")
                .orderBy("title", Query.Direction.ASCENDING)
                .startAt(newText);

        allposts = new FirestoreRecyclerOptions.Builder<postmodel>()
                .setQuery(postquery, postmodel.class)
                .build();

        postAdapter.notifyDataSetChanged();
        postAdapter.updateOptions(allposts);
        postrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        postrecyclerview.setAdapter(postAdapter);
    }

}

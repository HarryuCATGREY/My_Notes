package com.example.whim;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class WhatYouLikedActivity extends AppCompatActivity {
    RecyclerView likerecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    TextView posttodayDate;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

//    button for the bottom
    ImageButton home;
    ImageButton profile;
    ImageButton community;

    FirestoreRecyclerAdapter<postmodel, LikeViewHolder> likeAdapter;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_you_liked);

        getSupportActionBar().hide();

//        initialise firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        initialise button and view
        posttodayDate = findViewById(R.id.todaypostDate);
        storageReference = FirebaseStorage.getInstance().getReference();

//        community, home, profile button
        community = findViewById(R.id.communityfromlike);
        home = findViewById(R.id.homefromlike);
        profile = findViewById(R.id.profilefromlike);

//        bottom button click setting
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WhatYouLikedActivity.this, ExistUserMainPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WhatYouLikedActivity.this, ProfileActivity.class));
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WhatYouLikedActivity.this, PostActivity.class));
            }
        });

//        change color for whim
        TextView login_title = (TextView)findViewById(R.id.textView5);
        String h = getColoredSpanned("h", "#67B1F9");
        String i = getColoredSpanned("i","#6E80FA");
        String dot = getColoredSpanned(".","#FFCA3A");
        login_title.setText(Html.fromHtml("My Favorite W"+h+i+"m"+dot));

//        show date
        posttodayDate.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

//        set query for liked list
        Query likequery = firebaseFirestore.collection("posts").whereArrayContains("likedusers", firebaseUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<postmodel> alllikes = new FirestoreRecyclerOptions.Builder<postmodel>().setQuery(likequery, postmodel.class).build();

        likeAdapter = new FirestoreRecyclerAdapter<postmodel, LikeViewHolder>(alllikes){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull LikeViewHolder likeViewHolder, int i, @NonNull postmodel postmodel) {

                likeViewHolder.liketitle.setText(postmodel.getTitle());
                likeViewHolder.likecontent.setText(postmodel.getContent());

                if (postmodel.getImage() != null){
                    StorageReference imgReference = storageReference.child("photos/").child(postmodel.getImagename());
                    imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(likeViewHolder.likeimgview);
                        }
                    });
                }
                String postId = likeAdapter.getSnapshots().getSnapshot(i).getId();
                likeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), LikedDetails.class);
                        intent.putExtra("title",postmodel.getTitle());
                        intent.putExtra("content",postmodel.getContent());
                        intent.putExtra("image", postmodel.getImage());
                        intent.putExtra("time", postmodel.getTime());
                        intent.putExtra("location", postmodel.getLocation());
                        intent.putExtra("likedusers", postmodel.getLikedusers());
                        intent.putExtra("timestamp", postmodel.getTimestamp());
                        intent.putExtra("imagename", postmodel.getImagename());
                        intent.putExtra("numlikes", postmodel.getNumlikes());
                        intent.putExtra("uid", postmodel.getUid());
                        intent.putExtra("postId", postId);
                        view.getContext().startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_notes_list_pic,parent, false);
                return new LikeViewHolder(view);
            }
        };

        likerecyclerview=findViewById(R.id.recycle_liked);
        likerecyclerview.setHasFixedSize(false);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        likerecyclerview.setLayoutManager(staggeredGridLayoutManager);
        likerecyclerview.setAdapter(likeAdapter);
        likeAdapter.notifyDataSetChanged();

    }

//    set viewHolder
    public static class LikeViewHolder extends RecyclerView.ViewHolder{

//        show liked title, content and image
        private final TextView liketitle;
        private final TextView likecontent;
        private final ImageView likeimgview;

        LinearLayout mpost;

        public LikeViewHolder(@NonNull View itemView) {
            super(itemView);
            liketitle = itemView.findViewById(R.id.exist_title);
            likecontent = itemView.findViewById(R.id.note_content);
            likeimgview = itemView.findViewById(R.id.postimgview);
            mpost = itemView.findViewById(R.id.whim);
            ConstraintLayout likecolour = itemView.findViewById(R.id.post_colour);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        likeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(likeAdapter != null){
            likeAdapter.stopListening();
        }
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    public void onBackPressed() {

    }
}
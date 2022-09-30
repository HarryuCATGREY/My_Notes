package com.example.whim;

import androidx.cardview.widget.CardView;

import com.example.whim.Models.Notes;

public interface NotesClickListener {
    void onClick(Notes notes);

    void onLongClick(Notes notes, CardView cardView);
}

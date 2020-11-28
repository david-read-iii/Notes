package com.davidread.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<Note> {

    /**
     * Defines how note objects from an array list should be adapted to be displayed in a list view.
     * @param notes The array list containing the note objects.
     */
    public NotesAdapter(@NonNull Context context, @NonNull ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    /**
     * Specifies how a single view of the list view should appear and what text should be filled.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the ith note object.
        Note note = getItem(position);

        // Specify the layout file.
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_note, parent, false);

        // Initialize layout elements.
        TextView textViewTitle = convertView.findViewById(R.id.text_view_title);
        TextView textViewDescription = convertView.findViewById(R.id.text_view_description);

        // Set text as attributes of the note object.
        textViewTitle.setText(note.getTitle());
        textViewDescription.setText(note.getDescription());

        return convertView;
    }
}
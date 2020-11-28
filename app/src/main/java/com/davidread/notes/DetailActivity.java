package com.davidread.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The detail activity is started by the main activity when they must specify attributes of a note.
 * This activity sets up a form where they can specify the attributes and operations they want
 * executed on the note when they return to the main activity.
 */
public class DetailActivity extends AppCompatActivity {

    private Note selectedNote;
    private EditText editTextTitle, editTextDescription;
    private Button buttonDelete, buttonSave;

    /**
     * First function called when the activity executes. It sets up a form for the user to fill out
     * for a new or existing note. It also provides a delete and a save button. Both buttons finish
     * the activity, but each finishes with a different result code.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Set action bar title as empty string.
        getSupportActionBar().setTitle("");

        // Get attributes of the selected note.
        Intent intent = getIntent();
        selectedNote = new Note(
                intent.getLongExtra(MainActivity.INTENT_EXTRA_KEY, -1),
                intent.getStringExtra(MainActivity.INTENT_EXTRA_TITLE),
                intent.getStringExtra(MainActivity.INTENT_EXTRA_DESCRIPTION)
        );

        // Initialize layout elements.
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonDelete = findViewById(R.id.button_delete_note);
        buttonSave = findViewById(R.id.button_save_note);

        // Set text as attributes of the selected note object.
        editTextTitle.setText(selectedNote.getTitle());
        editTextDescription.setText(selectedNote.getDescription());

        /* Specify delete button clicks to finish the activity with result code 1. Only pass key of
         * the selected note back. */
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.INTENT_EXTRA_KEY, selectedNote.getKey());
                setResult(MainActivity.RESULT_DELETE_NOTE, intent);
                finish();
            }
        });

        /* Specify save button clicks to finish the activity with result code 0. Pass key of
         * selected note back, in addition to the title and description the user specified. */
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.INTENT_EXTRA_KEY, selectedNote.getKey());
                intent.putExtra(MainActivity.INTENT_EXTRA_TITLE, editTextTitle.getText().toString());
                intent.putExtra(MainActivity.INTENT_EXTRA_DESCRIPTION, editTextDescription.getText().toString());
                setResult(MainActivity.RESULT_SAVE_NOTE, intent);
                finish();
            }
        });
    }

    /**
     * This function is called when the back button is pressed. When this happens, we return result
     * code 2.
     */
    @Override
    public void onBackPressed() {
        setResult(MainActivity.RESULT_DISCARD_NOTE);
        super.onBackPressed();
    }

    /**
     * This function is called when the back button in the action bar is pressed. When this happens,
     * we do the same thing as back button presses.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
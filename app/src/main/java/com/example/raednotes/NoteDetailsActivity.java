package com.example.raednotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titlEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titlEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn= findViewById(R.id.delete_note_text_view_btn);


        //recieve data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titlEditText.setText(title);
        contentEditText.setText(content);

        if (isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE); // if its edit mode make delete note visible

                    }

        saveNoteBtn.setOnClickListener((v)-> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());


    }

    void saveNote(){
        String notTitle = titlEditText.getText().toString();
        String notContent = contentEditText.getText().toString();
        if (notTitle==null || notTitle.isEmpty()){
            titlEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(notTitle);
        note.setContent(notContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            //we will pass the id if it is edited note
            //update the note
            documentReference = Utility.getCollectionRefrenceForNotes().document(docId);

        }else {
            //create new note
            documentReference = Utility.getCollectionRefrenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();

                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");



                }
            }
        });

    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionRefrenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();

                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");



                }
            }
        });


    }


}
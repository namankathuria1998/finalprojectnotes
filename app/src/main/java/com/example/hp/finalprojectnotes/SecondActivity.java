package com.example.hp.finalprojectnotes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondactivity);

        Intent intent=getIntent();
        Notes curnote= (Notes) intent.getSerializableExtra("key");

        EditText tv=findViewById(R.id.noteinfo);
        tv.setText(curnote.getActualcontent());


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference rootref=firebaseDatabase.getReference();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference uidref=rootref.child(uid);


        FloatingActionButton fabsave=findViewById(R.id.fabsave);
        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String changedcontent=tv.getText().toString();
               curnote.setActualcontent(changedcontent);
               DatabaseReference reftobeupdated=uidref.child(curnote.getKey());
               reftobeupdated.setValue(curnote);
            }
        });
    }
}

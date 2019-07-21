package com.example.hp.finalprojectnotes;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MainActivity extends AppCompatActivity
        implements/* NavigationView.OnNavigationItemSelectedListener,*/ValueEventListener,ChildEventListener {

    int count = 0;
//    DatabaseReference rootnoderef;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    View view;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rootnoderef = firebaseDatabase.getReference();

    ArrayList<Notes> arrayList = new ArrayList<>();
    ArrayList<Notes> previousarrayList = new ArrayList<>();
    ArrayList<Notes>thirdarrayList=new ArrayList<>();

    public static final String CHANNEL_ID = "12345";
    NotificationManager notificationManager;
    Notification notification;
    AlertDialog datealertDialog;
    String datetosearch = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e("TAG", "onCreate: "+ date);

        rootnoderef.child("sharingnotes").addChildEventListener(this);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "Ur Notes", NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(notificationChannel);
        }


        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Note Added")
                .setContentText("You Have Added A Note")
                //.setStyle(new NotificationCompat.BigTextStyle().bigText())
                .build();




        LayoutInflater li = LayoutInflater.from(this);
        view = li.inflate(R.layout.myalertdialog, null);

//
//        LayoutInflater lif=LayoutInflater.from(this);
//        View myview=li.inflate(R.layout.myalertdialog,null);
//

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Post Your Note")
                .setView(view)
//                .setView(myview)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String title = et.getText().toString();
                        EditText actcon = view.findViewById(R.id.addtitle);
                        String actualcontent = actcon.getText().toString();
                        String mydate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()).format(new Date());

                        //uidnoderef.push().setValue(notes);


                        DatabaseReference delnode = uidnoderef.push();
                        String key = delnode.getKey();

                        Notes curnote = new Notes(firebaseUser.getPhotoUrl().toString(), title, actualcontent, mydate, key);
                        delnode.setValue(curnote);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();


        final View dateview = li.inflate(R.layout.datealertdialog, null);
        datealertDialog = new AlertDialog.Builder(this)
                .setTitle("Enter the Date")
                .setView(dateview)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText etdate = dateview.findViewById(R.id.editdate);
                        datetosearch = etdate.getText().toString();

                        /*



                        thirdarrayList.clear();

                        thirdarrayList.addAll(arrayList);

                        arrayList.clear();
                        for(Notes curnote:thirdarrayList)
                        {

                            if(curnote.getDate().equals(datetosearch))
                            {
                                arrayList.add(curnote);
                            }

                        }

                        myAdapter.notifyDataSetChanged();


                        arrayList.clear();
                        arrayList.addAll(thirdarrayList);

                        */
                        ArrayList<Notes> thirdarrayList = new ArrayList<>();

                        for (Notes curnote : arrayList) {
                            if (curnote.getDate().equals(datetosearch)) {
                                thirdarrayList.add(curnote);
                            }
                        }
                        myAdapter.setArrayList(thirdarrayList);
                        myAdapter.notifyDataSetChanged();
                        // WHY NOTES ARE NOT SEARCHED IF THESE BELOW LINES ARE INCLUDED
//                        arrayList.clear();
//                        arrayList.addAll(thirdarrayList);

                    }
                })
                .create();


        FloatingActionButton fabutton = findViewById(R.id.fab);
        fabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        recyclerView = findViewById(R.id.recycle);
        myAdapter = new MyAdapter(arrayList);
        // important line for setting recycler view
//        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

//        MyViewmodel androidViewModel=ViewModelProviders.of(this).get(MyViewmodel.class);
//        androidViewModel.getarraylist()
    }

    FirebaseUser firebaseUser;
    Intent loginintent;
    DatabaseReference uidnoderef;
    EditText et;



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null) {
                    Log.e("TAG", "onAuthStateChanged: " );
                    loginintent = AuthUI.getInstance().createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build())
                            )
                            .build();
                    startActivity(loginintent);
                } else {
                    uidnoderef = rootnoderef.child(firebaseUser.getUid());
                    uidnoderef.addValueEventListener(MainActivity.this);

                    et = view.findViewById(R.id.edit);

                    String email=firebaseAuth.getCurrentUser().getEmail();
                    String uid=firebaseAuth.getCurrentUser().getUid();
//
                    assert email != null;
                    int pos=email.indexOf(".");
                        char[] myNameChars = email.toCharArray();
                        myNameChars[pos] = 'x';
                        email = String.valueOf(myNameChars);

                    rootnoderef.child("uuid").child(email).setValue(uid);

//
                    //Button btn=findViewById(R.id.button);
//                        btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                String title=et.getText().toString();
//                                Notes notes=new Notes(firebaseUser.getPhotoUrl().toString(),title);
//                                uidnoderef.push().setValue(notes);
//                            }
//                        });
                }
            }
        });
    }
//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.alpha:
                ArrayList<Notes> secondarraylist = new ArrayList<>();
                secondarraylist.addAll(arrayList);
                Collections.sort(secondarraylist, new Comparator<Notes>() {
                    public int compare(Notes v1, Notes v2) {
                        return v1.getTitle().compareTo(v2.getTitle());
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(secondarraylist, Comparator.comparing(Notes::getTitle));
                }

                myAdapter.setArrayList(secondarraylist);
                myAdapter.notifyDataSetChanged();

//                arrayList.clear();
//                arrayList.addAll(secondarrayList);
                break;

            case R.id.title:
                break;

            case R.id.refr:
                // set the adapter to the orignal arraylist
                myAdapter.setArrayList(arrayList);
                myAdapter.notifyDataSetChanged();
                break;

            case R.id.search:
//                ArrayList<Notes>thirdarrayList=new ArrayList<>();
//                thirdarrayList.addAll(arrayList);
//                 arrayList.clear()
                datealertDialog.show();
                break;
        }
        return true;
    }

    Notes mynotes;

    //    @Override
//    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//          mynotes=dataSnapshot.getValue(Notes.class);
//        Log.e("TAG", "onChildAdded: "+mynotes.getDate());
//          arrayList.add(mynotes);
//          myAdapter.notifyDataSetChanged();
//          count++;
//
//          notificationManager.notify(count,notification);
//    }
//
//    @Override
//    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//    }
//
//    @Override
//    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//    }
//
//    @Override
//    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//    }
//
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> DataSnapshots = dataSnapshot.getChildren();
        arrayList.clear();
        for (DataSnapshot ds : DataSnapshots) {
            Notes curnotes = ds.getValue(Notes.class);
            arrayList.add(curnotes);
        }
        myAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(arrayList.size() - 1);

        if (previousarrayList.size() < arrayList.size() && previousarrayList.size() != 0) {
            count++;
            notificationManager.notify(count, notification);
        }
        previousarrayList.clear();
        previousarrayList.addAll(arrayList);
    }




    // attaching listener to sharingnotes



    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Notes curnote=dataSnapshot.getValue(Notes.class);
            arrayList.add(curnote);
            myAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(arrayList.size()-1);

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {



    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==123){
//            startActivity(new Intent(this,MainActivity.class));
//            finish();
//        }
//
//    }
}

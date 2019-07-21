package com.example.hp.finalprojectnotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewholder> {

    private ArrayList<Notes>arrayList;
    private Context context;



    public void setArrayList(ArrayList<Notes> arrayList) {
        this.arrayList = arrayList;

    }

    public MyAdapter(ArrayList<Notes> arrayList)
    {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
       LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view=li.inflate(R.layout.layoutforone,parent,false);
       return new viewholder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

      Notes curnotes=arrayList.get(position);
      holder.title.setText(curnotes.getTitle());
        Picasso.get().load(curnotes.getPhoto()).into(holder.iv);
       holder.exactdatetime.setText(curnotes.getDate());
       holder.explanation.setText(curnotes.getActualcontent());
       holder.mytoggle.setChecked(false);
       holder.mytoggle.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_gey));



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewholder extends RecyclerView.ViewHolder
    {
        ImageView iv;
        TextView exactdatetime;
        TextView title;
        TextView delete,explanation;
        ToggleButton mytoggle;

        public viewholder(View itemView)
        {
            super(itemView);
            iv=itemView.findViewById(R.id.image);
            title=itemView.findViewById(R.id.title);
            exactdatetime=itemView.findViewById(R.id.exactdatetime);
            delete=itemView.findViewById(R.id.delete);
            explanation=itemView.findViewById(R.id.explanation);
            mytoggle=itemView.findViewById(R.id.mytoggle);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Notes clickednote=arrayList.get(getAdapterPosition());
                    Intent intent=new Intent(context, SecondActivity.class);
                    intent.putExtra("key",clickednote);
                    context.startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   Notes tobedeleted=arrayList.get(getAdapterPosition());
                    Log.e("TAG", "onLongClick: "+tobedeleted.getKey());
//

                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference rootnoderef =firebaseDatabase.getReference();
                    DatabaseReference uidnoderef =rootnoderef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    uidnoderef.child(tobedeleted.getKey()).removeValue();

//                    return true;

                }
            });


            mytoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                       mytoggle.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_star_yellow));
                    }
                    else mytoggle.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_gey));
                }
            });


            LayoutInflater lif=LayoutInflater.from(context);
            View sharenoteview=lif.inflate(R.layout.sharenoteview,null);

            AlertDialog alertDialog=new AlertDialog.Builder(context)
                    .setTitle("Send this note")
                    .setView(sharenoteview)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText et=sharenoteview.findViewById(R.id.email);
                            String curuseremail=et.getText().toString();
                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference rootnoderef=firebaseDatabase.getReference();

                            int pos=curuseremail.indexOf(".");

                            char[] myNameChars = curuseremail.toCharArray();
                            myNameChars[pos] = 'x';
                            curuseremail = String.valueOf(myNameChars);


                            rootnoderef.child("uuid").child(curuseremail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                      String uid=dataSnapshot.getValue(String.class);
                                      Notes mysendnote=arrayList.get(getAdapterPosition());
                                      rootnoderef.child(uid).push().setValue(mysendnote);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    })
                    .create();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    alertDialog.show();
                    return true;
                }
             });

        }


    }

}

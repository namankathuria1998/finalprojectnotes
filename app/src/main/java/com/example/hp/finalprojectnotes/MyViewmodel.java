package com.example.hp.finalprojectnotes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class MyViewmodel extends AndroidViewModel {
    public MyViewmodel(@NonNull Application application) {
        super(application);

        Context context=application.getApplicationContext();

    }

    ArrayList<Notes>arrayname=new ArrayList<>();

    ArrayList<Notes>getarraylist()
    {
        if(arrayname==null)
        {

        }
        return arrayname;
    }
}

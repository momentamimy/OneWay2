package com.example.eltimmy.oneway2;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText email,pass;


    private DatabaseReference databaseReference;


    public static final int REQUEST_LOCATION_CODE=99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userName = sharedPref.getString("UserName","");
        String email2 = sharedPref.getString("Email","");
        String phone = sharedPref.getString("Phone","");
        String password = sharedPref.getString("Password","");

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        email=(EditText) findViewById(R.id.Email);
        pass=(EditText) findViewById(R.id.Password);
        databaseReference = database.getInstance().getReference();              // D firebase Auth,DB,Storage
        mAuth = FirebaseAuth.getInstance();

        if (userName!=""&&password!="")
        {
            mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MapsActivity2.class));
                    }
                }
            });
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkLocationPermmission();
        }



        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null)
                {
                    //user signed in
                   // Toast.makeText(getApplicationContext(),"signed in",Toast.LENGTH_LONG).show();


                }
                else
                {
                    //user signed out
                    //Toast.makeText(getApplicationContext(),"signed out",Toast.LENGTH_LONG).show();
                }
            }
        };
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    public void Sign_In(View view) {
        final String Em,Pa;
        Em=email.getText().toString();
        Pa=pass.getText().toString();
        if (!Em.equals("")&&!Pa.equals(""))
        {
            mAuth.signInWithEmailAndPassword(Em,Pa)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        //String parent;
                        FirebaseUser user =mAuth.getCurrentUser();
                        //Toast.makeText(getApplicationContext(),"Sign in"+User.getUid(),Toast.LENGTH_LONG).show();

                        databaseReference.child("drivers").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    SharedPreferences sharedPref=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPref.edit();
                                    String Email= dataSnapshot.child("Email").getValue(String.class);
                                    String Phone= dataSnapshot.child("Phone_Number").getValue(String.class);
                                    editor.putString("UserName",Em);
                                    editor.putString("Password",Pa);
                                    editor.putString("Email",Email);
                                    editor.putString("Phone",Phone);
                                    editor.apply();
                                    startActivity(new Intent(getApplicationContext(), AppActivity.class));
                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    finish();

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"You Are a Passenger",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                     /*   if (UserDataType=="Driver") {
                         finish();
                         startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                     }*/
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Sign in failed",Toast.LENGTH_LONG).show();

                    }
                }
            });


        }
        else
        {
            Toast.makeText(getApplicationContext(),"please fill your data",Toast.LENGTH_LONG).show();
        }
    }



    public void Sign_Up(View view) {
       finish();
        startActivity(new Intent(this,Signup.class));

    }
    public boolean checkLocationPermmission()
    {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                        REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                        REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;

    }
}

package com.example.eltimmy.oneway2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class Splash_Screen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String userName;
    String password;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        context=this;
        SharedPreferences sharedPref=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        userName = sharedPref.getString("UserName","");
        String email2 = sharedPref.getString("Email","");
        String phone = sharedPref.getString("Phone","");
        password = sharedPref.getString("Password","");
        mAuth = FirebaseAuth.getInstance();
        Animation fade,rotate;
        rotate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        fade= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        TextView text=findViewById(R.id.textic);
        TextView icon=findViewById(R.id.icon);
        text.setAnimation(fade);
        icon.setAnimation(fade);
        Thread timer= new Thread(){
            public void run(){

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (userName!=""&&password!="")
                    {
                        mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    startActivity(new Intent(getApplicationContext(), AppActivity.class));
                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        startActivity(new Intent(getApplication(),MainActivity.class));
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }
}

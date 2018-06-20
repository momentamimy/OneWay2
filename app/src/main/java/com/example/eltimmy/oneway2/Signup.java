package com.example.eltimmy.oneway2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE=100;       //image
    Uri imageUri;


    private ProgressBar progressbar;
    private Button BRegister;
    private ImageView UserPhoto;
    private EditText ETName;
    private EditText ETEmail;                   //GUI
    private EditText ETPasswrod;
    private EditText ETConfirmPw;
    private EditText ETPhone;
    private ImageView Back ;
    private SegmentedButtonGroup type ;
    public String  UserDataType = "Driver";



    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;              //firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();              // D firebase Auth,DB,Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        Back = (ImageView) findViewById(R.id.back);
        BRegister = (Button) findViewById(R.id.SignUp);
        ETName = (EditText) findViewById(R.id.EditTextName);
        ETEmail = (EditText) findViewById(R.id.EditTextEmail);
        ETPasswrod = (EditText) findViewById(R.id.EditTextPw);                  // D GUI
        ETConfirmPw = (EditText) findViewById(R.id.EditTextPwConfirm);
        ETPhone = (EditText) findViewById(R.id.EditTextPhone);
        UserPhoto =(ImageView) findViewById(R.id.photo);
        type = (SegmentedButtonGroup)findViewById(R.id.Type);


        UserPhoto.setOnClickListener(this);
        BRegister.setOnClickListener(this);                         //D GUI Buttons
        Back.setOnClickListener(this);


    }
    private void RegisterUser() {     //Register Button

        String Email = ETEmail.getText().toString().trim();
        String Password = ETPasswrod.getText().toString().trim();
        String ConfirmPassword = ETConfirmPw.getText().toString().trim();
        String Name = ETName.getText().toString().trim();
        String Phone = ETPhone.getText().toString().trim();



        if(UserPhoto.getDrawable().getConstantState() == getResources().getDrawable( R.drawable.empty).getConstantState() ){
            Toast.makeText(this, "Please enter your Profile Picture", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "Please enter your Name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(ConfirmPassword)) {
            Toast.makeText(this, "Please enter The same Password", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Phone)) {
            Toast.makeText(this, "Please enter your Phone number ", Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.equals(Password, ConfirmPassword)) {
            Toast.makeText(this, "Your passwords do not match.", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Registering User ....");     //Depend on Internet connection
        progressDialog.show();



        firebaseAuth.createUserWithEmailAndPassword(Email,Password)    //Create Email and Password in Auth
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ // if this Email Work

                            FirebaseUser User =firebaseAuth.getCurrentUser();

                            String Name = ETName.getText().toString().trim();
                            String Phone = ETPhone.getText().toString().trim();
                            UserInfo RegistereduserInfo = new UserInfo(Name,Phone);


                            type.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
                                //Check User Type
                                @Override
                                public void onClickedButtonPosition(int position) {
                                    if(position == 0)
                                        UserDataType = "Passenger";
                                    else if(position == 1)
                                        UserDataType = "Driver";
                                }
                            });

                            databaseReference.child(UserDataType).child(User.getUid()).setValue(RegistereduserInfo); //Enter information to database
                            storageReference.child(UserDataType).child(imageUri.getLastPathSegment()).putFile(imageUri);
                            //Enter Photo to storage

                            Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }else{
                            Toast.makeText(getApplicationContext(),"Registered Faild .. Try Again ",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }
                    }
                });




    }


    @Override
    public void onClick(View view) {

        if(view == BRegister)
        {
            RegisterUser();
        }
       if(view == Back){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        if(view == UserPhoto){
            OpenGallery();
        }

    }

    private void OpenGallery() { // To Browse image from gallery
        Intent gallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode , Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK && requestCode == PICK_IMAGE){
            imageUri =data.getData();
            UserPhoto.setImageURI(imageUri);
        }
    }
}

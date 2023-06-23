package com.example.tourbud5.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tourbud5.R;
import com.example.tourbud5.SharedPreferenceManager;
import com.example.tourbud5.model.User;
import com.example.tourbud5.model.UserBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity {
    private static String TAG="SignUpView";


    EditText password_create ;
    EditText username_create;
    EditText phone_create;
    EditText description_create;
    Button createBTN;
    private CollectionReference dbRef= FirebaseFirestore.getInstance().collection("Users");;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        createBTN = findViewById(R.id.acc_sign_up_button);
        password_create = findViewById(R.id.password_create);
        phone_create = findViewById(R.id.phone_create);
        username_create = findViewById(R.id.userName_create_ET);
        description_create = findViewById(R.id.description_create_ET);

        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(form_validation() ) {
                    String phone = "+65"+phone_create.getText().toString().trim();
                    String password = password_create.getText().toString().trim();
                    String username = username_create.getText().toString().trim();
                    String description = description_create.getText().toString().trim();
                    CreateUserPhoneAccount(phone,password,username,description);
                }
            }
        });

    }
    private boolean form_validation() {
        String toast_message = "";
        boolean validated = true;

        //(phone_number.charAt(0)==8 || phone_number.charAt(0)=
        String phone_number = phone_create.getText().toString().replaceAll(" ","");
        if( !( (phone_number.length()==8)&&(phone_number.charAt(0)=='8'||phone_number.charAt(0)=='9') ) ) {
            toast_message+="Please enter a valid phone number.\n";
            validated = false;
        }

        String password = password_create.getText().toString().replaceAll(" ","");
        if(password.length()<6) {
            toast_message+="Passwords must contain at least 6 characters.\n";
            validated = false;
        }

        String username = username_create.getText().toString().replaceAll(" ","");
        if(username.length()==0) {
            toast_message+="Username cannot be empty.\n";
            validated = false;
        }

        String description = description_create.getText().toString().replaceAll(" ","");
        if(description.length()==0) {
            toast_message+="Description cannot be empty.\n";
            validated = false;
        }

        if(toast_message=="") {
            Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, toast_message, Toast.LENGTH_LONG).show();
        }

        return validated;

    }

    private void CreateUserPhoneAccount(String phone, String password,String username,String description) {



        Log.d(TAG,"check if user exist"); //if no data means doesn't exist


        dbRef.whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"success query");


                                if(task.getResult().isEmpty()){
                                    Log.d(TAG,"user doesnt exist");

                                    //create class
                                    User userObj=new UserBuilder.userBuilder()
                                          .setDescription(description)
                                            .setFirstName(username)
                                            .setPassword(password)
                                            .setPhone(phone)
                                            .build();
                                    CreateUserInDatabase(userObj);

                                }else{
                                    Toast.makeText(SignUpActivity.this,
                                            "user exists, please log in", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUpActivity.this, TourProfileActivity.class);
                                    startActivity(i);

                                }




                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });






    }
    private void CreateUserInDatabase(User userObj){
        //create in database
        dbRef.add(userObj)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {


                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        SharedPreferenceManager.storeUserId(getApplicationContext(),documentReference.getId());
                        Toast.makeText(SignUpActivity.this,
                                "created user", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUpActivity.this, TourProfileActivity.class);
                        startActivity(i);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
}
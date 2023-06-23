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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private String TAG="Login";
    // Widgets
    Button loginBTN;
    private EditText phoneET;
    private EditText passET;
    private CollectionReference dbRef= FirebaseFirestore.getInstance().collection("Users");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        loginBTN = findViewById(R.id.login_btn);
        phoneET = findViewById(R.id.phone_sign_in);
        passET  = findViewById(R.id.password_sign_in);


        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(form_validation() ) {
                    String phone = "+65"+phoneET.getText().toString().trim();
                    String password = passET.getText().toString().trim();
                    loginUser(phone,password);
                }
            }
        });
    }

    private void loginUser(String phone,String password){
        Log.d(TAG,"check if user exist"); //if no data means doesn't exist

        dbRef.whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.d(TAG,"user doesnt exist");
                                Toast.makeText(LoginActivity.this,
                                        "No account registered, redirecting to sign up page", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                                startActivity(i);
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //user Exists, check pw
                                    User userObj=document.toObject(User.class);

                                    if(userObj.getPassword().equals(password)){
                                        Toast.makeText(LoginActivity.this,
                                                "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(LoginActivity.this, TourProfileActivity.class);
                                        startActivity(i);
                                        SharedPreferenceManager.storeUserId(getApplicationContext(),document.getId());
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this,
                                                "Wrong password", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private boolean form_validation() {
        String toast_message = "";
        boolean validated = true;

        //(phone_number.charAt(0)==8 || phone_number.charAt(0)=
        String phone_number = phoneET.getText().toString().replaceAll(" ","");
        if( !( (phone_number.length()==8)&&(phone_number.charAt(0)=='8'||phone_number.charAt(0)=='9') ) ) {
            toast_message+="Please enter a valid phone number.\n";
            validated = false;
        }

        String password = passET.getText().toString().replaceAll(" ","");
        if(password.length()<6) {
            toast_message+="Passwords must contain at least 6 characters.\n";
            validated = false;
        }

        if(toast_message!="") {
            Toast.makeText(this, toast_message, Toast.LENGTH_LONG).show();
        }

        return validated;

    }
}
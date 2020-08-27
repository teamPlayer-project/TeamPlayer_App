package com.project.teamplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class activity_details extends AppCompatActivity {
    //members
    private static final String ACTIVITIES_COLLECTION = "Activities";
    private String activity_name;
    private String manager_email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String TAG = "PostDetailActivity";
    private String user_name;
    private String user_email;
    private DatabaseReference root ;
    private DatabaseReference userRoot ;
    private String temp_key;
    ArrayList<String> detailsList;
    ArrayList<String> activitiesNameList;
    ArrayList<String> descriptionsList;
    String ageRange;
    private ArrayList<String> managerList;
    private boolean isRequestSend;
    private Button locationButton;
    private String lat, lon;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView groupImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsList = getIntent().getStringArrayListExtra("Details");
        activitiesNameList = getIntent().getStringArrayListExtra("ACTIVITIES_NAME_LIST");
        descriptionsList = getIntent().getStringArrayListExtra("DESCRIPTIONS_LIST");
        managerList = getIntent().getStringArrayListExtra("MANAGER_LIST");
        locationButton = (Button) findViewById(R.id.location);
        activity_name = detailsList.get(0);
        downloadImage();
        getAgeRange();
        //Show action bar
        isRequestSend=false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        setTitle(activity_name);
        //get the manager email
        DocumentReference userNAme = db.collection("Activities").document(activity_name);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        manager_email= document.getString("manager_email");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        root = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity_name);
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        final Button requestButton = (Button) findViewById(R.id.request);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRequestSend){
                    request(view);
                    isRequestSend=true;
                }

            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = root.orderByChild("user_email").equalTo(user_email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    requestButton.setClickable(false);
                    requestButton.setText("Request Sent");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * show the detail of the activity: name, description, participants number, age range.
     */
    public void showDetails(){
        TextView descriptionToShow = (TextView) findViewById(R.id.description_details_1);
        descriptionToShow.setText(detailsList.get(1));
        TextView participantsNumToShow = (TextView) findViewById(R.id.number_of_participations);
        String particNum = "";
        if (detailsList.get(2).equals("1")){
            particNum = detailsList.get(2) + " Participant";
        } else {
            particNum = detailsList.get(2) + " Participants";
        }
        participantsNumToShow.setText(particNum);
        TextView ageRangeToShow = (TextView)findViewById(R.id.age_Range_textView);
        String ageRangeDetails = "Age Range: " + ageRange;
        ageRangeToShow.setText(ageRangeDetails);


    }

    /**
     * get the age range of the activity from the DB
     */
    private void getAgeRange() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(ACTIVITIES_COLLECTION).document(detailsList.get(0));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ageRange = document.getString("ageRange");
                        lat = (String)document.get("lat");
                        lon = (String)document.get("lon");
                        if (lat.equals("")){
                            locationButton.setClickable(false);
                            locationButton.setText("No Location");
                        }
                        showDetails();
                    } else {
                        Log.d(TAG, "No such document");
                        showDetails();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    showDetails();
                }
            }
        });

    }



    /**
     * The function send request to manager to join group
     * @param view
     */
    public void request(View view){
        //Get current user email
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user_email = user.getEmail();
        //Check if user is already in the group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DocumentReference userNAme = db.collection("Users").document(user_email);
        userNAme.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        //Get user name and send request notification to maneger
                        temp_key = root.push().getKey();
                        user_name= document.getString("Name");
                        String message = user_name +" send request to join group "+activity_name;
                        NotificationSender sender=new NotificationSender(manager_email,message);
                        sender.sendNotification();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getString("Name"));
                        //sendNotification();
                        //Add request to DB
                        userRoot= root.child(temp_key);
                        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", user_name);
                        map.put("user_email", user_email);
                        userRoot.updateChildren(map);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    /**
     * The function go back to the previous screen when arrow bar is pressed
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent=new Intent(this, search_result.class);
        intent.putStringArrayListExtra("ACTIVITIES_NAME_LIST", activitiesNameList);
        intent.putStringArrayListExtra("DESCRIPTIONS_LIST", descriptionsList);
        intent.putStringArrayListExtra("MANAGER_LIST", managerList);
        intent.putStringArrayListExtra("Details", detailsList);
        startActivity(intent);
        return true;
    }

    public void mapView(View view) {
        Intent intent = new Intent(this, MapsActivityGroup.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        intent.putExtra("Back_TO", "Details");
        intent.putExtra("Details", detailsList);
        intent.putExtra("ACTIVITIES_NAME_LIST", activitiesNameList);
        intent.putExtra("DESCRIPTIONS_LIST", descriptionsList);
        intent.putExtra("MANAGER_LIST", managerList);
        startActivity(intent);
    }

    /**
     * download the image of the activity
     */
    public void downloadImage(){
        final Context context= this;
        groupImage = (ImageView) findViewById(R.id.profile_image);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final StorageReference storageReference = storage.getReference("uploads/" + activity_name);
        storage.getReference("uploads/" + activity_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context /* context */)
                        .load(storageReference)
                        .into(groupImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });

    }

}

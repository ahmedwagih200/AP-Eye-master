package com.example.apeye.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apeye.R;
import com.example.apeye.adapters.Comment_Adapter;
import com.example.apeye.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private List<Comment> commentList;
    private Comment_Adapter comment_adapter;
    private String current_user_id;
    private String postID;
    private EditText comment_field;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        final Button write_comment = findViewById(R.id.write_comment);
        comment_field =findViewById(R.id.comment_text);
        RecyclerView recyclerView = findViewById(R.id.comment_list);
        circleImageView = findViewById(R.id.comment_image);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        postID = getIntent().getStringExtra("Post_ID");

        commentList = new ArrayList<>();
        comment_adapter =new Comment_Adapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(comment_adapter);
        recyclerView.setHasFixedSize(true);

        firebaseFirestore.collection("Posts/" + postID + "/Comments").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e == null){

                    if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty()){

                        for(DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){

                                Comment comment = doc.getDocument().toObject(Comment.class);
                                commentList.add(comment);
                                comment_adapter.notifyDataSetChanged();
                            }

                        }

                    }

                }

            }
        });

        firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userImage = Objects.requireNonNull(task.getResult()).getString("image");

                    Picasso.get().load(userImage).placeholder(R.drawable.defualtuser)
                            .error(R.drawable.defualtuser).into(circleImageView);
                }
            }
        });

        write_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String commentText = comment_field.getText().toString();
                if(!commentText.isEmpty()){

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd MMMM yyyy");
                    String currentDateandTime = sdf.format(new Date());
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment",commentText );
                    commentsMap.put("user_id", current_user_id);
                    commentsMap.put("date",currentDateandTime);

                    firebaseFirestore.collection("Posts/" + postID + "/Comments").add(commentsMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    comment_field.setText("");
                                }
                            });
                }else{
                    Toast.makeText(CommentActivity.this, "Please Enter a text" , Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}


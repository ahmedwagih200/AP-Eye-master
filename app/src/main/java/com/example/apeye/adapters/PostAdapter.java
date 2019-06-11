package com.example.apeye.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apeye.R;
import com.example.apeye.model.Post;
import com.example.apeye.ui.CommentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class    PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

     private static final String TAG = "PostAdapter";
     private List<Post> postList;
     private FirebaseFirestore firebaseFirestore;
     public Context context;

    public PostAdapter(List<Post> posts){
         postList = posts;
     }

    @NonNull
    @Override
    public PostAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);

        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.PostHolder holder, int position) {

        holder.setIsRecyclable(false);
        Post current_post = postList.get(position);
        final String postID = postList.get(position).PostID;
        if(current_post.getImage_url().equals("null"))
        {
            holder.postImage.setVisibility(View.GONE);
        } else{
            Picasso.get().load(current_post.getImage_url()).fit().into(holder.postImage);
        }
        holder.postdesc.setText(current_post.getDesc());
        holder.postDate.setText(current_post.getDate());

        firebaseFirestore.collection("Users").document(current_post.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    String userName = Objects.requireNonNull(task.getResult()).getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.postUserName.setText(userName);
                    Picasso.get().load(userImage).placeholder(R.drawable.defualtuser).error(R.drawable.defualtuser).into(holder.postUserImage);

                } else {
                    Log.e(TAG, "Something went wrong in adapter at line 91");
                }
            }
        });

        holder.commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("Post_ID",postID);
                context.startActivity(commentIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class PostHolder extends RecyclerView.ViewHolder{

        private TextView postdesc;
        private ImageView postImage;
        private TextView postDate;
        private TextView postUserName;
        private CircleImageView postUserImage;
        private Button commentsButton;

        PostHolder(@NonNull View itemView) {
            super(itemView);
            postdesc = itemView.findViewById(R.id.post_desc);
            postImage = itemView.findViewById(R.id.post_image);
            postUserImage = itemView.findViewById(R.id.post_profile_image);
            postUserName = itemView.findViewById(R.id.post_user_name);
            postDate = itemView.findViewById(R.id.post_date);
            commentsButton = itemView.findViewById(R.id.comments_button);
        }
    }
}

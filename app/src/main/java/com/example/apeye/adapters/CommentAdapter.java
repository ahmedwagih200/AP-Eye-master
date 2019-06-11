package com.example.apeye.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apeye.R;
import com.example.apeye.model.Comment;
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


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private List<Comment> commentList;

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.CommentHolder(itemView);
    }

    public CommentAdapter(List<Comment> comments){
        commentList =comments;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentHolder holder, int position) {

        holder.setIsRecyclable(false);
        Comment currentComment = commentList.get(position);
        holder.comment_date.setText(currentComment.getDate());
        holder.comment_text.setText(currentComment.getComment());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(currentComment.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    String userName = Objects.requireNonNull(task.getResult()).getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.comment_user_name.setText(userName);
                    Picasso.get().load(userImage).placeholder(R.drawable.defualtuser)
                            .error(R.drawable.defualtuser).into(holder.comment_image);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{

        private TextView comment_text;
        private TextView comment_date;
        private TextView comment_user_name;
        private CircleImageView comment_image;

        CommentHolder(@NonNull View itemView) {
            super(itemView);
            comment_text = itemView.findViewById(R.id.comment_item_text);
            comment_image = itemView.findViewById(R.id.comment_item_user_image);
            comment_date = itemView.findViewById(R.id.comment_item_date);
            comment_user_name = itemView.findViewById(R.id.comment_item_user_name);
        }
    }
}

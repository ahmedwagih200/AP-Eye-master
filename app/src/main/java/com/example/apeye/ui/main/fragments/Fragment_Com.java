package com.example.apeye.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.apeye.R;
import com.example.apeye.adapters.PostAdapter;
import com.example.apeye.model.Post;
import com.example.apeye.ui.login.Login;
import com.example.apeye.ui.login.UserInformation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_Com extends Fragment {

    private FirebaseAuth firebaseAuth;
    private List<Post> postList;
    private PostAdapter post_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_com, container, false);

        postList = new ArrayList<>();
        RecyclerView recyclerView = rootView.findViewById(R.id.post_recycler_view);
        post_adapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(container).getContext()));
        recyclerView.setAdapter(post_adapter);
        recyclerView.setHasFixedSize(true);

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.add_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewPost.class));
            }
        });


        FirebaseApp.initializeApp(Objects.requireNonNull(getContext()));
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null ) {

            // User is signed in
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                    if(e == null){

                        if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty())
                        {
                            for(DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                                if (doc.getType() == DocumentChange.Type.ADDED){
                                    String id = doc.getDocument().getId();
                                    Post post = doc.getDocument().toObject(Post.class).withId(id);
                                    postList.add(post);
                                    post_adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });


        } else {
            sendToLogin();
        }
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.com_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                logout();
                return true;

            case R.id.acc_setting:
                Intent intent = new Intent(getContext(), UserInformation.class);
                intent.putExtra("comingFrom",2);
                startActivity(intent);
            default:
                return false;
        }
    }

    private void logout() {
        firebaseAuth.signOut();

        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

}

package com.aac.envision;

// ExampleAdapter.java
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {


    private FirebaseFirestore firestore;
    Context context;
    ArrayList<Post> postArrayList;

    //ArrayList<User> userArrayList;

    public PostAdapter(FirebaseFirestore firestore, Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
        this.firestore = firestore;
    }

    public PostAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super();
    }


    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {

        Post post = postArrayList.get(position);
        fetchUserForPost(post, holder);
        //TESTING OUT GLIDE TO SEE IF IT WORKS::
        Glide.with(holder.itemView.getContext()).
                load(post.getMediaURL()).
                into(holder.imageView);

    }


    private void fetchUserForPost(Post post, PostViewHolder holder) {
        String globalUserID = post.getGlobalUserID();
        if(globalUserID != null) {
            firestore.collection("users").document(post.getGlobalUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userSnapshot = task.getResult();
                        if (userSnapshot.exists()) {
                            User user = userSnapshot.toObject(User.class);
                            holder.emailTextView.setText(user.email);
                        } else {
                            Log.e("PostAdapter", "User not found for post: " + post.getGlobalUserID());
                        }
                    } else {
                        Log.e("PostAdapter", "Error fetching user for post: " + post.getGlobalUserID(), task.getException());
                    }
                }
            });
        }

    }


    public int getItemCount() {
        return postArrayList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView  emailTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.tvEmail);
            imageView = itemView.findViewById(R.id.tvPostImage);
        }


        /**
         * COMMENTING THIS OUT FOR NOW, MIGHT USE LATER
         * public void bind(Post post) {
            // Set data to views
            Glide.with(itemView.getContext())
                    .load(post.getMediaURL())
                    .into(imageView);

            // Assuming timestampTextView is a TextView, you need to format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(post.getTimestamp());
            timestampTextView.setText(formattedDate);
        } **/

    }

}
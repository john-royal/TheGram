package com.codepath.johnroyal.thegram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.johnroyal.thegram.R;
import com.codepath.johnroyal.thegram.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context context;
    List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvCaption;
        ImageView ivPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvCaption = itemView.findViewById(R.id.tv_caption);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvCaption.setText(post.getCaption());
            Glide.with(context).load(post.getImage().getUrl()).into(ivPostImage);
        }
    }
}

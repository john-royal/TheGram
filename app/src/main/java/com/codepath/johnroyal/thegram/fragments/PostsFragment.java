package com.codepath.johnroyal.thegram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.johnroyal.thegram.models.Post;
import com.codepath.johnroyal.thegram.adapters.PostsAdapter;
import com.codepath.johnroyal.thegram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    private static final String TAG = "PostsFragment";

    RecyclerView rvPosts;
    SwipeRefreshLayout swipeRefreshLayout;

    List<Post> posts;
    PostsAdapter adapter;

    PostsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rv_posts);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), posts);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });

        loadPosts();
    }

    protected void loadPosts() {
        throw new UnsupportedOperationException();
    }

    protected void onPostsLoadSuccess(List<Post> newPosts) {
        Log.i(TAG, String.format("Loaded %d posts", newPosts.size()));
        posts.clear();
        posts.addAll(newPosts);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void onPostsLoadFailure(ParseException e) {
        Log.e(TAG, String.format("Query posts failed: %s", e.getLocalizedMessage()), e);
        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static class AllPosts extends PostsFragment {
        @Override
        protected void loadPosts() {
            Post.queryPosts(new FindCallback<Post>() {
                @Override
                public void done(List<Post> newPosts, ParseException e) {
                    if (e == null) {
                        onPostsLoadSuccess(newPosts);
                    } else {
                        onPostsLoadFailure(e);
                    }
                }
            });
        }
    }

    public static class CurrentUserPosts extends PostsFragment {
        @Override
        protected void loadPosts() {
            Post.queryPostsByUser(ParseUser.getCurrentUser(), new FindCallback<Post>() {
                @Override
                public void done(List<Post> newPosts, ParseException e) {
                    if (e == null) {
                        onPostsLoadSuccess(newPosts);
                    } else {
                        onPostsLoadFailure(e);
                    }
                }
            });
        }
    }
}
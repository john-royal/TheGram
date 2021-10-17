package com.codepath.johnroyal.thegram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {

    public static final String TAG = "NewPostActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    EditText etCaption;
    Button btnTakePhoto;
    ImageView ivImagePreview;
    Button btnSubmit;
    ProgressBar pbSubmitting;

    private String imageFileName = "photo.jpg";
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        etCaption = findViewById(R.id.etCaption);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        btnSubmit = findViewById(R.id.btnSubmit);
        pbSubmitting = findViewById(R.id.pbSubmitting);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemSignOut) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            return;
        }
        if (resultCode == RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            // TODO: Resize image if needed.
            ivImagePreview.setImageBitmap(takenImage);
        } else {
            Toast.makeText(this, "Photo was not taken", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePost() {
        if (imageFile == null || ivImagePreview.getDrawable() == null) {
            Toast.makeText(NewPostActivity.this, "Image must be provided", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSubmit.setVisibility(View.INVISIBLE);
        pbSubmitting.setVisibility(View.VISIBLE);

        ParseFile image = new ParseFile(imageFile);
        String caption = etCaption.getText().toString();
        ParseUser user = ParseUser.getCurrentUser();

        Post post = new Post(image, caption, user);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                btnSubmit.setVisibility(View.VISIBLE);
                pbSubmitting.setVisibility(View.INVISIBLE);

                if (e == null) {
                    etCaption.setText("");
                    ivImagePreview.setImageResource(0);
                    Toast.makeText(NewPostActivity.this, "Post saved successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewPostActivity.this, String.format("Cannot save post: %s", e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signOut() {
        ParseUser.logOut();
        Intent i = new Intent(NewPostActivity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }

    public void launchCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileProvider = makeFileProviderUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Ensure it’s safe to call startActivityForResult()
        if (i.resolveActivity(getPackageManager()) == null) {
            return;
        }
        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private Uri makeFileProviderUri() {
        if (imageFile != null) {
            // Delete existing image file if one is present
            imageFile.delete();
        }
        imageFile = makeImageFile();
        return FileProvider.getUriForFile(NewPostActivity.this, "com.codepath.fileprovider", imageFile);
    }

    private File makeImageFile() {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + imageFileName);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    for (Post post: posts) {
                        Log.i(TAG, String.format("Found post from username '%s' with description '%s'", post.getUser().getUsername(), post.getCaption()));
                    }
                } else {
                    Log.e(TAG, String.format("Query posts failed: %s", e.getLocalizedMessage()), e);
                    Toast.makeText(NewPostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
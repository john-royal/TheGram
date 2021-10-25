package com.codepath.johnroyal.thegram.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.johnroyal.thegram.models.Post;
import com.codepath.johnroyal.thegram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    EditText etCaption;
    Button btnTakePhoto;
    ImageView ivImagePreview;
    Button btnSubmit;
    ProgressBar pbSubmitting;

    private String imageFileName = "photo.jpg";
    private File imageFile;

    public ComposeFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCaption = view.findViewById(R.id.et_caption);
        btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        ivImagePreview = view.findViewById(R.id.iv_image_preview);
        btnSubmit = view.findViewById(R.id.btn_submit);
        pbSubmitting = view.findViewById(R.id.pb_submitting);

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

    private void savePost() {
        if (imageFile == null || ivImagePreview.getDrawable() == null) {
            Toast.makeText(getContext(), "Image must be provided", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Post saved successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), String.format("Cannot save post: %s", e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
                }
            }
        });
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
            Toast.makeText(getContext(), "Photo was not taken", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileProvider = makeFileProviderUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Ensure it’s safe to call startActivityForResult()
        if (i.resolveActivity(getContext().getPackageManager()) == null) {
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
        return FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", imageFile);
    }

    private File makeImageFile() {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + imageFileName);
    }
}
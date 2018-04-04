package com.cpen391.healthwatch.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.cpen391.healthwatch.util.UploadImageTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by william on 2018/3/27.
 * Class used for delegating user profile image upload/download tasks to.
 */
public class UserProfileOperator {
    public interface UserProfileImageListener {
        void onUserProfileImageUploaded();
    }
    private static final String TAG = UserProfileOperator.class.getSimpleName();
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private boolean mIsSendingImage;

    public boolean isSendingImage() {
        return mIsSendingImage;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void getUserProfileImage(String imageFilePathJson, FadeInNetworkImageView profileImage) {
        try {
            String imageFilePath = new JSONObject(imageFilePathJson).getString("image");
            String url = ServerInterface.BASE_URL + "/gateway/user/image/" + imageFilePath;
            profileImage.setImageUrl(url, GlobalFactory.getAppControlInterface().getImageLoader());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the photo that was last taken if it exists.
     */
    public void deleteCurrentPhoto() {
        if (mCurrentPhotoPath != null) {
            File imageFile = new File(mCurrentPhotoPath);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    Log.d(TAG, "Unable to delete image file");
                } else {
                    Log.d(TAG, "Image deleted");
                }
            }
            mCurrentPhotoPath = null;
        }
    }

    private File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        deleteCurrentPhoto(); // Delete the previous stored photo if there was one.
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Uploads user profile image if an image exists to be uploaded.
     * @param activity the activity running this user profile operator.
     * @param listener the callback to invoke when profile image is successfully uploaded.
     */
    public void uploadUserProfileImage(final Activity activity, final UserProfileImageListener listener) {
        if (mCurrentPhotoPath != null) {
            List<String> filePaths = new ArrayList<>();
            filePaths.add(mCurrentPhotoPath);
            mIsSendingImage = true;
            Map<String, String> headers = new HashMap<>();
            headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
            Log.d(TAG, "uploading image");
            UploadImageTask uploadImageTask = new UploadImageTask("/gateway/user/image", headers, "image",
                    filePaths, new ServerCallback() {
                @Override
                public void onSuccessResponse(String response) {
                    mIsSendingImage = false;
                    if (listener != null) {
                        listener.onUserProfileImageUploaded();
                    }
                }
            }, new ServerErrorCallback() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mIsSendingImage = false;
                    deleteCurrentPhoto();
                    Toast.makeText(activity, "Unable to upload image", Toast.LENGTH_SHORT).show();
                }
            });
            uploadImageTask.execute();
        }
    }

    /**
     * @param activity the activity to dispatch photo intent from.
     */
    public void dispatchTakePhotoIntent(Activity activity) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Unable to take photo", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}

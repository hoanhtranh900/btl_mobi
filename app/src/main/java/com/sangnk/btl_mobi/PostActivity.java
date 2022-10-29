package com.sangnk.btl_mobi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";

    private ImageView close;
    private ImageView imageAdded;
    private TextView post;
    SocialAutoCompleteTextView description;
    private String imageUrl;

    private Uri imageUri;
    private ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        init();
        close();
        cropImage();
        post();


    }


    private void imageUpload() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (imageUri != null) {
            progressDialog.show();
            //upload image with retrofit2
            File file = new File(imageUri.getPath());
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/" + getMimeType(imageUri)), file));
            Long objectType = 1L;
            Call<ResponseBody> call = apiInterface.postImage(fileToUpload, objectType);
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: " + response.body());
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getString("code").equals("200")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String imageId = data.getString("id");

                                //create Post
                                Map<String, String> bodyPost = new ArrayMap<>();
                                bodyPost.put("description", description.getText().toString());

                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new JSONObject(bodyPost).toString());
                                Call<ResponseBody> callPost = apiInterface.postPost(requestBody);
                                callPost.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response1) {
                                        if (response1.isSuccessful()) {
                                            JSONObject jsonObject1 = null;
                                            try {
                                                jsonObject1 = new JSONObject(response1.body().string());
                                                if (jsonObject1.getString("code").equals("200")) {
                                                    JSONObject data1 = jsonObject1.getJSONObject("data");
                                                    //update objectId for imgge
                                                    Map<String, Object> bodyUpdateImage = new ArrayMap<>();
                                                    bodyUpdateImage.put("objectId", data1.getString("id"));
                                                    bodyUpdateImage.put("listFileIds", Collections.singletonList(imageId));
                                                    RequestBody updateImageObjectTypeBody = RequestBody.create(MediaType.parse("application/json"), new JSONObject(bodyUpdateImage).toString());
                                                    Call<ResponseBody> callUpdateImage = apiInterface.updateImage(updateImageObjectTypeBody);
                                                    callUpdateImage.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response2) {
                                                            if (response2.isSuccessful()) {
                                                                JSONObject jsonObject2 = null;
                                                                try {
                                                                    jsonObject2 = new JSONObject(response2.body().string());
                                                                    if (jsonObject2.getString("code").equals("200")) {
                                                                        Toast.makeText(PostActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(PostActivity.this, com.sangnk.btl_mobi.MainActivity.class));
                                                                        finish();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                        }
                                                    });


                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(PostActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(PostActivity.this, "Faild", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } else //if failed to select image or didn't get image
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "imageUpload: No Image Selected");
        }
    }

    private String getMimeType(Uri imageUri) {
        String extension;
        //Check uri format to avoid null
        if (imageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(imageUri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(imageUri.getPath())).toString());
        }

        return extension;
    }

    //return file Extension type of image this function used in upload image()
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    //post button to post the post with image description and other details
    private void post() {
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUpload();
            }
        });
    }

    //using CropImage Library ->simply crop the image if you want to using crop image.
    private void cropImage() {
        CropImage.activity().start(PostActivity.this);

    }

    //Using cropImage Class  we will check if image is get from user successfully or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            //setting the image on the post activity after getting post with cropImge
            imageAdded.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Try Again - Error", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, com.sangnk.btl_mobi.MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //setting HashTag adapter to get hashtag from Description it is built in social View Class
//        ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
        // getting hashtag and showing as a suggestion of the samename hashtags
//        FirebaseDatabase.getInstance().getReference().child("HashTags")
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot:snapshot.getChildren())
//                {
//                    hashtagAdapter.add(new Hashtag(dataSnapshot.getKey(),(int)snapshot.getChildrenCount()));
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        description.setHashtagAdapter(hashtagAdapter);

    }

    //init
    private void init() {
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);


    }

    //if not to upload simply close
    private void close() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, com.sangnk.btl_mobi.MainActivity.class));
                finish();
            }
        });
    }
}
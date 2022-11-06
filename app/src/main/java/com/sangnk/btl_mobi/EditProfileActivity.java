package com.sangnk.btl_mobi;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sangnk.btl_mobi.Fragments.ProfileFragment;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private TextView save;
    private CircleImageView imageProfile;
    private TextView changePhoto;
    private EditText fullName;
    private EditText username;

    private Uri mImageUri;
    private Uri currentImageUri;

    private StorageTask uploadTask;
    private StorageReference storageRef;
    private ApiInterface apiInterface;

    FirebaseUser fUser;

    private String currentUsername;
    private String currentFullName;
    private String currentImage;

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //init
        init();

        //get data from intent
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("username");
        currentFullName = intent.getStringExtra("fullname");
        currentImage = intent.getStringExtra("imageProfile");
        currentImageUri = Uri.parse(currentImage);
        sharedPrefManager = new SharedPrefManager(this);

        //set data to view
        username.setText(currentUsername);
        fullName.setText(currentFullName);
        Picasso.get().load(currentImage).into(imageProfile);


        //if user want to close the edit profile ativity
        setClose();

        //if user want to change profile pic
        //both will do the same thing -> when user click on text(Change Profile) or Click on Photo.
        setChangePhoto();
        setImageProfile();
        //save everything on Firebase after editing everything
        setSave();


    }
    private void setSave()
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile();
//                startActivity(new Intent(EditProfileActivity.this, ProfileFragment.class));
            }

        });

    }

    private void UpdateProfile() {




         //save
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (mImageUri != null) {
            progressDialog.show();
            //upload image with retrofit2
            File file = new File(mImageUri.getPath());
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/" + getMimeType(mImageUri)), file));
            Long objectType = 3L;
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

                                Map<String, Object> bodyPost = new ArrayMap<>();
                                bodyPost.put("username",username.getText().toString());
                                bodyPost.put("fullName",fullName.getText().toString());
                                bodyPost.put("id",sharedPrefManager.getSPUserId());


                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new JSONObject(bodyPost).toString());
                                Call<ResponseBody> callPost = apiInterface.editUser(requestBody);
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
                                                                        Toast.makeText(EditProfileActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(EditProfileActivity.this, com.sangnk.btl_mobi.MainActivity.class));
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
                                        Toast.makeText(EditProfileActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(EditProfileActivity.this, "Faild", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } else //if failed to select image or didn't get image
        {
            Map<String, Object> bodyPost = new ArrayMap<>();
            bodyPost.put("username",username.getText().toString());
            bodyPost.put("fullName",fullName.getText().toString());
            bodyPost.put("id",sharedPrefManager.getSPUserId());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new JSONObject(bodyPost).toString());
            Call<ResponseBody> callPut = apiInterface.editUser(requestBody);
            callPut.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response1) {
                    if (response1.isSuccessful()) {
                        JSONObject jsonObject1 = null;
                        try {
                            jsonObject1 = new JSONObject(response1.body().string());
                            if (jsonObject1.getString("code").equals("200")) {
                                JSONObject data1 = jsonObject1.getJSONObject("data");

                                //toast update success
                                Toast.makeText(EditProfileActivity.this, "Update succes", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                //set text new data
                                username.setText(data1.getString("username"));
                                fullName.setText(data1.getString("fullName"));



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
                    Toast.makeText(EditProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }
            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            imageProfile.setImageURI(mImageUri);

        }else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    //upload ImageUrl and getUrl
//    private void uploadImage() {
//        ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Uploading");
//        Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
//        pd.show();
//        if(mImageUri !=null)
//        {
//            //setting same things that have setted in PostActivity
//            StorageReference fileRef = storageRef.child(System.currentTimeMillis()+".jpeg");
//            uploadTask = fileRef.putFile(mImageUri);
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if(!task.isSuccessful())
//                    {
//                        throw task.getException();
//                    }else
//                    {
//                        return fileRef.getDownloadUrl();
//                    }
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful())
//                    {
//                        Uri downloadUri = task.getResult();
//                        String uri = downloadUri.toString();
//                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
//                                .child("imageurl").setValue(uri);
//                        pd.dismiss();
//                    }else
//                    {
//                        Toast.makeText(EditProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//
//        }else
//        {
//            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
//        }
//    }

    //
    private void setImageProfile()
    {
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

    }

    //changing profile photo using CopImage Class
    private void setChangePhoto()
    {
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });


    }

    private void setClose()
    {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Init method for views
     */
    private void init()
    {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        close = findViewById(R.id.close);
        save = findViewById(R.id.save);
        imageProfile = findViewById(R.id.image_profile);
        changePhoto = findViewById(R.id.change_photo);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
    }

}
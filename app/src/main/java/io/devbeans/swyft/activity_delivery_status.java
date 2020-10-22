package io.devbeans.swyft;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.data_models.SignatureURLModel;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.mark_parcel_complete;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class activity_delivery_status extends AppCompatActivity {

    CheckBox cb_incomplete, cb_consignee, cb_refure, cb_funds;
    TextView tx_note;
    ProgressBar progressBar = null;
    Button btn_submit, btn_image;
    EasyImage easyImage;
    ArrayList<MediaFile> ImagereturnValue = new ArrayList<>();
    URI uri;
    Bitmap cam_image = null;
    String image_url = "";
    String reason = "";
    List<String> checkbox = new ArrayList<>();
    boolean apiCall = false;

    @Override
    public void onBackPressed() {
        if (apiCall){
            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Caution", "Please wait while the process is completed!");
        }else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_note);
        cb_incomplete = findViewById(R.id.cb_incomplete);
        cb_consignee = findViewById(R.id.cb_consignee);
        cb_refure = findViewById(R.id.cb_refure);
        cb_funds = findViewById(R.id.cb_funds);
        tx_note = findViewById(R.id.tx_note);
        btn_submit = findViewById(R.id.btn_submit);
        btn_image = findViewById(R.id.btn_image);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cb_incomplete.isChecked())
                    checkbox.add("Incomplete address");
                if (cb_consignee.isChecked())
                    checkbox.add("Consignee not Available");
                if (cb_refure.isChecked())
                    checkbox.add("Refused to receive the parcel");
                if (cb_funds.isChecked())
                    checkbox.add("Insufficient funds");

                if (tx_note.getText().toString().isEmpty()) {
                    Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Please insert reason first!");
                } else {
                    if (image_url != null){
                        if (!image_url.isEmpty()){
                            if (!checkbox.isEmpty()){
                                markParcelsTonotcomplete();
                            }else {
                                Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Select a reason from the options first!");
                            }
                        }else {
                            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Capture image first!");
                        }
                    }else {
                        Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Capture image first!");
                    }
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.url_loading_animation);

        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_delivery_status.this.finish();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!image_url.isEmpty()){

                    new AlertDialog.Builder(activity_delivery_status.this)
                            .setTitle("Caution")
                            .setMessage("Are you sure you want to replace the already uploaded image?")

                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(activity_delivery_status.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                                                ContextCompat.checkSelfPermission(activity_delivery_status.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                                            ActivityCompat.requestPermissions(activity_delivery_status.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        else {
                                            image_url = "";
                                            Options options = Options.init()
                                                    .setRequestCode(100)                                           //Request code for activity results
                                                    .setCount(1)                                                   //Number of images to restict selection count
                                                    .setFrontfacing(false)                                         //Front Facing camera on start
                                                    .setExcludeVideos(true)                                       //Option to exclude videos
                                                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                                    .setPath("/pix/images");                                       //Custom Path For media Storage

                                            Pix.start(activity_delivery_status.this, options);
                                        }
                                    } else {
                                        image_url = "";
                                        Options options = Options.init()
                                                .setRequestCode(100)                                           //Request code for activity results
                                                .setCount(1)                                                   //Number of images to restict selection count
                                                .setFrontfacing(false)                                         //Front Facing camera on start
                                                .setExcludeVideos(true)                                       //Option to exclude videos
                                                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                                .setPath("/pix/images");                                       //Custom Path For media Storage

                                        Pix.start(activity_delivery_status.this, options);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(activity_delivery_status.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                                ContextCompat.checkSelfPermission(activity_delivery_status.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                            ActivityCompat.requestPermissions(activity_delivery_status.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        else {
                            Options options = Options.init()
                                    .setRequestCode(100)                                           //Request code for activity results
                                    .setCount(1)                                                   //Number of images to restict selection count
                                    .setFrontfacing(false)                                         //Front Facing camera on start
                                    .setExcludeVideos(true)                                       //Option to exclude videos
                                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                    .setPath("/pix/images");                                       //Custom Path For media Storage

                            Pix.start(activity_delivery_status.this, options);
                        }
                    } else {
                        Options options = Options.init()
                                .setRequestCode(100)                                           //Request code for activity results
                                .setCount(1)                                                   //Number of images to restict selection count
                                .setFrontfacing(false)                                         //Front Facing camera on start
                                .setExcludeVideos(true)                                       //Option to exclude videos
                                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                .setPath("/pix/images");                                       //Custom Path For media Storage

                        Pix.start(activity_delivery_status.this, options);
                    }
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == 0) {


            Options options = Options.init()
                    .setRequestCode(100)                                           //Request code for activity results
                    .setCount(1)                                                   //Number of images to restict selection count
                    .setFrontfacing(false)                                         //Front Facing camera on start
                    .setExcludeVideos(true)                                       //Option to exclude videos
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                    .setPath("/pix/images");                                       //Custom Path For media Storage

            Pix.start(activity_delivery_status.this, options);

        } else {
            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Please give permission for location");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            try {
                if (returnValue != null) {
                    uri = new URI(returnValue.get(0));
                    uploadImage();
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
////            ImagereturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
////            uploadImage();
////        }
//
//        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
//            @Override
//            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
//                ImagereturnValue.addAll(Arrays.asList(imageFiles));
//                uploadImage();
//            }
//
//            @Override
//            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
//                //Some error handling
//                error.printStackTrace();
//            }
//
//            @Override
//            public void onCanceled(@NonNull MediaSource source) {
//                //Not necessary to remove any files manually anymore
//            }
//        });
//
//    }


    public void uploadImage() {

        EnableLoading();

        File image = new File(uri.getPath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        cam_image = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        File f = new File(this.getCacheDir(), "cam_image.jpeg");


//Convert bitmap to byte array
        Bitmap bitmap = cam_image;
        ExifInterface ei = null;
        int orientation = 0;
        try {
            ei = new ExifInterface(image.getAbsolutePath());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        String encoded = Base64.encodeToString(bitmapdata, Base64.DEFAULT);

//write the bytes in file
        FileOutputStream fos = null;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
        MultipartBody.Part filedata = MultipartBody.Part.createFormData("file", f.getName(), reqFile);

        swift_api_delivery riderapidata = Databackbone.getinstance().getRetrofitbuilder().create(swift_api_delivery.class);

        RequestBody uploadContainer = RequestBody.create(MediaType.parse("multipart/form-data"), "MarkParcelStatus");

        Call<SignatureURLModel> call = riderapidata.uploadImage(filedata, uploadContainer);
        call.enqueue(new Callback<SignatureURLModel>() {
            @Override
            public void onResponse(Call<SignatureURLModel> call, Response<SignatureURLModel> response) {
                if (response.isSuccessful()) {

                    SignatureURLModel signatureURLModel = response.body();
                    Databackbone.getinstance().cam_image_data = signatureURLModel;
                    image_url = Databackbone.getinstance().cam_image_data.getMessage();
                    Log.e("UploadImage", image_url);

                    Drawable img = getResources().getDrawable(R.drawable.tick);
                    img.setBounds(0, 0, 40, 40);
                    btn_image.setCompoundDrawables(null, null, img, null);

                    DisableLoading();

                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
//                            mEditor.clear().commit();
//                            mEditor_default.clear().commit();
//                            mEditor_loadsheet.clear().commit();
                            Intent intent = new Intent(activity_delivery_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<SignatureURLModel> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Error Connecting To Server (file/patch-upload) " + t.getMessage());
                DisableLoading();

            }
        });

    }


    public void markParcelsTonotcomplete() {
        btn_submit.setEnabled(false);
        double lat = 0.0;
        double lng = 0.0;
        final List<String> parcelIds = Databackbone.getinstance().parcel_to_process;

        reason = tx_note.getText().toString();

        String action = Databackbone.getinstance().not_delivered_reason;
        //String taskId = Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().task_to_show).getTaskId();
        String taskId = Databackbone.getinstance().getDeliveryTask().getTaskId();

        if (parcelIds.size() == 0) {
            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Server code error 102");
            DisableLoading();
            return;
        }
        if (Databackbone.getinstance().current_location != null) {
            lat = Databackbone.getinstance().current_location.latitude;
            lng = Databackbone.getinstance().current_location.longitude;
        }
        String date = "19-11-2019";
        String phase = "Morning";


        Log.e("captured_image", image_url);

        mark_parcel_complete com_parcels = new mark_parcel_complete(image_url, parcelIds, action, taskId, lat, lng, reason, date, phase, checkbox);

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapi.markParcelComplete(Databackbone.getinstance().rider.getId(), com_parcels);
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if (response.isSuccessful()) {

                    List<RiderActivityDelivery> parcels = response.body();
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().remove_location_complete();

                    DisableLoading();
                    new AlertDialog.Builder(activity_delivery_status.this)
                            .setTitle("Not Delivered")
                            .setMessage("Confirmed")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    activity_delivery_status.this.finish();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
                            Intent intent = new Intent(activity_delivery_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<RiderActivityDelivery>> call, Throwable t) {
                System.out.println(t.getCause());
                btn_submit.setEnabled(true);
                Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Error Connecting To Server (Parcels/manage-parcel) " + t.getMessage());
                DisableLoading();
            }
        });

    }

    public void DisableLoading() {

        progressBar.setVisibility(View.GONE);
        btn_submit.setEnabled(true);
        btn_image.setEnabled(true);
        apiCall = false;

    }

    public void EnableLoading() {

        progressBar.setVisibility(View.VISIBLE);
        btn_submit.setEnabled(false);
        btn_image.setEnabled(false);
        apiCall = true;

    }
}

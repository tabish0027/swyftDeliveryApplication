package io.devbeans.swyft;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.github.gcacace.signaturepad.views.SignaturePad;

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
import io.devbeans.swyft.interface_retrofit_delivery.Datum;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.mark_parcel_complete;
import io.devbeans.swyft.interface_retrofit_delivery.parcel_signature_upload;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class activity_signature_pad extends AppCompatActivity {

    Button btn_submit;
    SignaturePad mSignaturePad;
    ImageView btn_cross;
    ProgressBar progressBar = null;
    Bitmap signature_image = null;
    Boolean has_signature_image = false;
    EditText textView11;
    Button capture_image;
    Bitmap cam_image = null;
    String image_url = "";
    URI uri;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        amount = getIntent().getStringExtra("amount");

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        btn_cross = findViewById(R.id.btn_cross);
        textView11 = findViewById(R.id.textView11);
        btn_cross.setVisibility(View.GONE);
        btn_submit = findViewById(R.id.btn_submit);
        capture_image = findViewById(R.id.capture_image);

        if (amount != null) {
            if (!amount.equals("0")) {
                capture_image.setVisibility(View.INVISIBLE);
            } else {
                capture_image.setVisibility(View.VISIBLE);
            }
        } else {
            capture_image.setVisibility(View.VISIBLE);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
                if (data == null || data.getParcels().size() == 0) {
                    activity_signature_pad.this.finish();
                }
                if (textView11.getText().toString().isEmpty()) {
                    Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Enter receiver's name first!");
                } else {
                    String Parcelid = Databackbone.getinstance().getDeliveryParcelsTask().getParcels().get(0).getParcelId();
                    uploadSignature(Parcelid);
                }

//                if (amount != null) {
//                    if (!amount.equals("0")) {
//                        Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
//                        if (data == null || data.getParcels().size() == 0) {
//                            activity_signature_pad.this.finish();
//                        }
//                        if (textView11.getText().toString().isEmpty()) {
//                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Enter receiver's name first!");
//                        } else {
//                            String Parcelid = Databackbone.getinstance().getDeliveryParcelsTask().getParcels().get(0).getParcelId();
//                            uploadSignature(Parcelid);
//                        }
//                    } else {
//                        if (image_url.isEmpty()) {
//                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Capture the image of ID card of the reciever first!");
//                        } else {
//                            Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
//                            if (data == null || data.getParcels().size() == 0) {
//                                activity_signature_pad.this.finish();
//                            }
//                            if (textView11.getText().toString().isEmpty()) {
//                                Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Enter receiver's name first!");
//                            } else {
//                                String Parcelid = Databackbone.getinstance().getDeliveryParcelsTask().getParcels().get(0).getParcelId();
//                                uploadSignature(Parcelid);
//                            }
//                        }
//                    }
//
//                }
//                else {
//                    if (image_url.isEmpty()) {
//                        Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Capture the image of ID card of the reciever first!");
//                    } else {
//                        Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
//                        if (data == null || data.getParcels().size() == 0) {
//                            activity_signature_pad.this.finish();
//                        }
//                        if (textView11.getText().toString().isEmpty()) {
//                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Enter receiver's name first!");
//                        } else {
//                            String Parcelid = Databackbone.getinstance().getDeliveryParcelsTask().getParcels().get(0).getParcelId();
//                            uploadSignature(Parcelid);
//                        }
//                    }
//                }

            }
        });

        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(activity_signature_pad.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                            ContextCompat.checkSelfPermission(activity_signature_pad.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        ActivityCompat.requestPermissions(activity_signature_pad.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    else {
                        Options options = Options.init()
                                .setRequestCode(100)                                           //Request code for activity results
                                .setCount(1)                                                   //Number of images to restict selection count
                                .setFrontfacing(false)                                         //Front Facing camera on start
                                .setExcludeVideos(true)                                       //Option to exclude videos
                                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                                .setPath("/pix/images");                                       //Custom Path For media Storage

                        Pix.start(activity_signature_pad.this, options);
                    }
                } else {
                    Options options = Options.init()
                            .setRequestCode(100)                                           //Request code for activity results
                            .setCount(1)                                                   //Number of images to restict selection count
                            .setFrontfacing(false)                                         //Front Facing camera on start
                            .setExcludeVideos(true)                                       //Option to exclude videos
                            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                            .setPath("/pix/images");                                       //Custom Path For media Storage

                    Pix.start(activity_signature_pad.this, options);
                }


            }
        });

        progressBar = (ProgressBar) findViewById(R.id.url_loading_animation);
        btn_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
                btn_cross.setVisibility(View.GONE);
                has_signature_image = false;
            }
        });
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
                btn_cross.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                has_signature_image = true;
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
            }
        });
        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_signature_pad.this.finish();
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

            Pix.start(activity_signature_pad.this, options);

        } else {
            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Please give permission for location");
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

                    DisableLoading();

                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
//                            mEditor.clear().commit();
//                            mEditor_default.clear().commit();
//                            mEditor_loadsheet.clear().commit();
                            Intent intent = new Intent(activity_signature_pad.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Error Connecting To Server (file/patch-upload) " + t.getMessage());
                DisableLoading();

            }
        });

    }

    public void uploadSignature(final String parcel_id) {
        EnableLoading();
        if (!has_signature_image) {
            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "error", "Please put Signature");

            return;
        }
        signature_image = mSignaturePad.getSignatureBitmap();

        File f = new File(this.getCacheDir(), "file.jpeg");


//Convert bitmap to byte array
        Bitmap bitmap = signature_image;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

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


        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        RequestBody patchModel = RequestBody.create(MediaType.parse("multipart/form-data"), "Parcel");
        RequestBody modelInstanceId = RequestBody.create(MediaType.parse("multipart/form-data"), parcel_id);
        RequestBody uploadContainer = RequestBody.create(MediaType.parse("multipart/form-data"), "parcels");
        RequestBody updateOn = RequestBody.create(MediaType.parse("multipart/form-data"), "deliverySignature");


        Call<parcel_signature_upload> call = riderapidata.uploadSignature(filedata, patchModel, modelInstanceId, uploadContainer, updateOn);
        call.enqueue(new Callback<parcel_signature_upload>() {
            @Override
            public void onResponse(Call<parcel_signature_upload> call, Response<parcel_signature_upload> response) {
                if (response.isSuccessful()) {

                    //parcel_signature_upload parcels = response.body();
                    // System.out.println(parcels.size());
                    markParcelsToComplete();
                    //DisableLoading();


                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
                            Intent intent = new Intent(activity_signature_pad.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<parcel_signature_upload> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Error Connecting To Server (file/patch-upload) " + t.getMessage());
                DisableLoading();

            }
        });

    }

    public void markParcelsToComplete() {
        double lat = 0.0;
        double lng = 0.0;
        final List<String> parcelIds = Databackbone.getinstance().parcel_to_process;
        String reason = "";
        String action = "delivered";
        RiderActivityDelivery riderDelivery = Databackbone.getinstance().getDeliveryTask();
        if (riderDelivery == null)
            return;
        String taskId = riderDelivery.getTaskId();
        if (parcelIds.size() == 0) {
            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Server code error 102");
            DisableLoading();
            return;
        }

        if (Databackbone.getinstance().current_location != null) {
            lat = Databackbone.getinstance().current_location.latitude;
            lng = Databackbone.getinstance().current_location.longitude;
        }

        mark_parcel_complete com_parcels = new mark_parcel_complete(image_url, parcelIds, action, taskId, lat, lng, reason, textView11.getText().toString());

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapi.markParcelComplete(Databackbone.getinstance().rider.getId(), com_parcels);
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if (response.isSuccessful()) {

                    int resID=getResources().getIdentifier("success", "raw", getPackageName());

                    MediaPlayer mediaPlayer=MediaPlayer.create(activity_signature_pad.this,resID);
                    mediaPlayer.start();

                    List<RiderActivityDelivery> parcels = response.body();
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    Databackbone.getinstance().remove_location_complete();

                    DisableLoading();
                    new AlertDialog.Builder(activity_signature_pad.this)
                            .setTitle("Signature")
                            .setMessage("Confirmed")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    activity_signature_pad.this.finish();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
                            Intent intent = new Intent(activity_signature_pad.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_signature_pad.this, "Error", "Error Connecting To Server (Parcels/manage-parcel) " + t.getMessage());
                DisableLoading();
            }
        });

    }

    public void DisableLoading() {
        btn_submit.setEnabled(true);
        capture_image.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    public void EnableLoading() {
        btn_submit.setEnabled(false);
        capture_image.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }
}

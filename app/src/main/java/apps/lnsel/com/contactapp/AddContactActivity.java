package apps.lnsel.com.contactapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import apps.lnsel.com.contactapp.VolleyLibrary.AppController;


/**
 * Created by apps2 on 7/14/2017.
 */
public class AddContactActivity extends AppCompatActivity {

    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    final int TAKE_PICTURE = 1;
    final int ACTIVITY_SELECT_IMAGE = 2;

    EditText et_contact_person_name, et_contact_no, et_contact_address, et_contact_email;
    TextInputLayout til_contact_person_name, til_contact_no, til_contact_address, til_contact_other_email;
    Button btn_cancel, btn_submit;
    ImageButton ib_contact_image;
    ImageView iv_contact_image;

    String cntName, cntNumber, cntEmail, cntAddress, cntUsrId;

    private static final String TAG = "REQ_ADD_CONTACT";

    SharedManagerUtil session;

    boolean hasPermissionRead;
    boolean hasPermissionCamera;

    Bitmap photo_bitmap;
    String image="";

    private static final int REQUEST_READ_STORAGE = 113;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 114;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        session = new SharedManagerUtil(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Contact");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        et_contact_person_name = (EditText) findViewById(R.id.activity_contact_add_et_person_name);
        et_contact_no = (EditText) findViewById(R.id.activity_contact_add_et_contact_no);
        et_contact_address = (EditText) findViewById(R.id.activity_contact_add_et_address);
        et_contact_email = (EditText) findViewById(R.id.activity_contact_add_et_email);

        ib_contact_image = (ImageButton) findViewById(R.id.activity_contact_add_ib_add_image);
        iv_contact_image = (ImageView) findViewById(R.id.activity_contact_add_iv_image);

        btn_cancel = (Button) findViewById(R.id.activity_contact_add_btn_cancel);
        btn_submit = (Button) findViewById(R.id.activity_contact_add_btn_submit);

        til_contact_person_name = (TextInputLayout) findViewById(R.id.activity_contact_add_til_person_name);
        til_contact_no = (TextInputLayout) findViewById(R.id.activity_contact_add_til_contact_no);
        til_contact_address = (TextInputLayout) findViewById(R.id.activity_contact_add_til_address);
        til_contact_other_email = (TextInputLayout) findViewById(R.id.activity_contact_add_til_email);

        et_contact_person_name.addTextChangedListener(new MyTextWatcher(et_contact_person_name));
        et_contact_no.addTextChangedListener(new MyTextWatcher(et_contact_no));

        ////******************* Check Camera Permission ************************///
        hasPermissionRead = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionRead) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }

        hasPermissionCamera = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ib_contact_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hasPermissionRead = (ContextCompat.checkSelfPermission(AddContactActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermissionRead) {
                    ActivityCompat.requestPermissions(AddContactActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_STORAGE);
                }

                hasPermissionCamera = (ContextCompat.checkSelfPermission(AddContactActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermissionCamera) {
                    ActivityCompat.requestPermissions(AddContactActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
                if (hasPermissionCamera&&hasPermissionRead) {
                    selectImage();
                }

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePersonName()) {
                    return;
                }
                if (!validateContactNo()) {
                    return;
                }

                cntName = et_contact_person_name.getText().toString();
                cntNumber = et_contact_no.getText().toString();
                cntEmail = et_contact_email.getText().toString();
                cntAddress = et_contact_address.getText().toString();
                cntUsrId = session.getUserID();

                if(photo_bitmap!=null) {
                    image = getStringImage(photo_bitmap);
                    if(isNetworkAvailable()){
                        String cntImageStatus = "true";
                        addContactWebService(cntImageStatus);
                    }else {
                        Toast.makeText(AddContactActivity.this, "Please check Internet Connection", Toast.LENGTH_LONG);
                    }

                }else {
                    noPictureDialog();
                }



            }
        });
    }

    public void addContactWebService(final String cntImageStatus){

        String url = WebServiceUrls.ADD_CONTACT_URL;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        String str_response = response;

                        try {
                            JSONObject jsonObj = new JSONObject(str_response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            if(status.equals("failed")){
                                Toast.makeText(AddContactActivity.this, message, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(AddContactActivity.this, message, Toast.LENGTH_LONG).show();
                                Intent signup = new Intent(AddContactActivity.this, MainActivity.class);
                                startActivity(signup);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(AddContactActivity.this, "Server not Responding, Please Check your Internet Connection", Toast.LENGTH_LONG).show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("cntName",cntName);
                params.put("cntNumber",cntNumber);
                params.put("cntEmail",cntEmail);
                params.put("cntAddress",cntAddress);
                params.put("usrId",cntUsrId);
                params.put("cntImage",image);
                params.put("cntImageStatus",cntImageStatus);
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(postRequest);

    }

    //********** Text Watcher for Validation *******************//
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.activity_contact_add_et_person_name:
                    validatePersonName();
                    break;
                case R.id.activity_contact_add_et_contact_no:
                    validateContactNo();
                    break;
            }
        }
    }


    private boolean validatePersonName() {
        if (et_contact_person_name.getText().toString().trim().isEmpty()) {
            til_contact_person_name.setError("person name can not be blank");
            requestFocus(et_contact_person_name);
            return false;
        } else {
            til_contact_person_name.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateContactNo() {
        if (et_contact_no.getText().toString().trim().isEmpty()) {
            til_contact_no.setError("contact no can not be blank");
            requestFocus(et_contact_no);
            return false;
        } else {
            til_contact_no.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    /////********************************************* FOR IMAGE SELECT AND UPLOAD*********************
    public String getStringImage(Bitmap bmp){

        int actualHeight = bmp.getHeight();
        int actualWidth = bmp.getWidth();

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        int inSampleSize = calculateInSampleSize(bmp, actualWidth, actualHeight);

        actualHeight=actualHeight/inSampleSize;
        actualWidth=actualWidth/inSampleSize;
        bmp=Bitmap.createScaledBitmap(bmp, actualWidth,actualHeight , true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static int calculateInSampleSize(Bitmap bmp, int reqWidth, int reqHeight) {
        final int height = bmp.getHeight();
        final int width = bmp.getWidth();
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void noPictureDialog(){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Do you want to submit contact without Image")
                .setTitle("Contact Image")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        String cntImageStatus = "false";
                        addContactWebService(cntImageStatus);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options,new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(options[which].equals("Take Photo"))
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
                else if(options[which].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                }
                else if(options[which].equals("Cancel"))
                {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    public void onActivityResult(int requestcode,int resultcode,Intent intent)
    {
        super.onActivityResult(requestcode, resultcode, intent);
        if(resultcode==this.RESULT_OK)
        {
            if(requestcode==TAKE_PICTURE)
            {
                photo_bitmap = (Bitmap)intent.getExtras().get("data");
                Drawable drawable=new BitmapDrawable(photo_bitmap);
                iv_contact_image.setBackgroundDrawable(drawable);
            }
            else if(requestcode==ACTIVITY_SELECT_IMAGE)
            {
                Uri selectedImage = intent.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                photo_bitmap = (BitmapFactory.decodeFile(picturePath));
                Drawable drawable=new BitmapDrawable(photo_bitmap);
                iv_contact_image.setBackgroundDrawable(drawable);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "The app was not allowed to read to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }

            case CAMERA_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "The app was not allowed to take photo from camera. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }








}

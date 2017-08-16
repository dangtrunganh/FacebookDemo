package com.anhdt.facebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int SELECT_IMAGE = 1001;

    private CallbackManager callbackManager;
    protected LoginButton mBtnFb;
    private Button mBtnShareUrl, mBtnChooseImage, mBtnShareImage;
    private EditText mEdtTitle, mEdtDescription, mEdtUrl;
    private ShareLinkContent shareLinkContent;
    private ImageView ivImageShare;
    private ShareDialog shareDialog;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initViews();
        loginWithFacebook();
    }

    private void initViews() {
        mBtnFb = (LoginButton) findViewById(R.id.login_button);
        mBtnShareUrl = (Button) findViewById(R.id.btn_share_url);
        mBtnChooseImage = (Button) findViewById(R.id.btn_choose_image);
        mBtnShareImage = (Button) findViewById(R.id.btn_share_image);

        ivImageShare = (ImageView) findViewById(R.id.iv_image_share);

        mEdtTitle = (EditText) findViewById(R.id.edt_title);
        mEdtDescription = (EditText) findViewById(R.id.edt_description);
        mEdtUrl = (EditText) findViewById(R.id.edt_url);


        mBtnShareUrl.setOnClickListener(this);
        mBtnChooseImage.setOnClickListener(this);
        mBtnShareImage.setOnClickListener(this);

        shareDialog = new ShareDialog(MainActivity.this);
        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivImageShare.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Login Success\n" + loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Login Error\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_url:
                shareUrl();
                break;
            case R.id.btn_choose_image:
                chooseImage();
                break;
            case R.id.btn_share_image:
                shareImage();
                break;
            default:
                break;
        }
    }

    private void shareUrl() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(mEdtTitle.getText().toString())
                    .setContentDescription(mEdtDescription.getText().toString())
                    .setContentUrl(Uri.parse(mEdtUrl.getText().toString()))
                    .build();
        }
        shareDialog.show(shareLinkContent);
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                        .build();
    }

    private void shareImage() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE);
    }
}

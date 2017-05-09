package com.project.luulinhson.demochiasehinhanh;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    public final static String CLIENT_ID = "4c939580d05946659a7199cf687aff4d";
    public final static String CLIENT_SECRET = "12def63bbc1543788d26819439281b39";
    public final static String CALLBACK_URL = "https://www.facebook.com/nino.el.16";


    private InstagramApp mApp;
    private Button btnConnect, btnViewInfo, btnGetAllImages;
    private LinearLayout llAfterLoginView;
    private HashMap<String, String> userInfoHashmap = new HashMap<String,String>();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = new InstagramApp(this,CLIENT_ID,
                CLIENT_SECRET,CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {

                btnConnect.setText("Disconnect");
                llAfterLoginView.setVisibility(View.VISIBLE);
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        setWidgetReference();
        bindEventHandlers();
        if (mApp.hasAccessToken()) {
// tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
            llAfterLoginView.setVisibility(View.VISIBLE);
            mApp.fetchUserName(handler);
        }
    }

    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
        btnViewInfo.setOnClickListener(this);
        btnGetAllImages.setOnClickListener(this);
    }

    private void setWidgetReference() {
        llAfterLoginView = (LinearLayout) findViewById(R.id.llAfterLoginView);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnViewInfo = (Button) findViewById(R.id.btnViewInfo);
        btnGetAllImages = (Button) findViewById(R.id.btnGetAllImages);
    }

    // OAuthAuthenticationListener listener ;
    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            connectOrDisconnectUser();
        } else if (v == btnViewInfo) {
            displayInfoDialogView();
        } else if (v == btnGetAllImages) {
            startActivity(new Intent(MainActivity.this, AllMediaFiles.class)
                    .putExtra("userInfo", userInfoHashmap));
        }
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
// btnConnect.setVisibility(View.VISIBLE);
                                    llAfterLoginView.setVisibility(View.GONE);
                                    btnConnect.setText("Connect");
// tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    private void displayInfoDialogView() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("Profile Info");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.profile_view, null);
        alertDialog.setView(view);
        ImageView ivProfile = (ImageView) view
                .findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvNoOfFollwers = (TextView) view
                .findViewById(R.id.tvNoOfFollowers);
        TextView tvNoOfFollowing = (TextView) view
                .findViewById(R.id.tvNoOfFollowing);
        new ImageLoader(MainActivity.this).DisplayImage(
                userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE),
                ivProfile);
        tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
        tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
        tvNoOfFollwers.setText(userInfoHashmap
                .get(InstagramApp.TAG_FOLLOWED_BY));
        alertDialog.create().show();
    }
}

}

package com.example.parteek.accurateotp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView txtresult;
    Button scan;
    Uri uri, imageUri;
    public Bitmap mbitmap;
    public static final int PICK_IMAGE = 1;
    Handler handler;
    CardView cardViewButton=null;
    CardView cardViewButton1=null;
    EditText editText=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        scan = (findViewById(R.id.scan));
        img = (findViewById(R.id.imgview));
        txtresult = (findViewById(R.id.txtResult));
        scan.setVisibility(View.GONE);
        handler=new Handler();
    }


    public void btnScan(View view) {


        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        Frame frame = new Frame.Builder()
                .setBitmap(mbitmap)
                .build();

        SparseArray<Barcode> barcodeSparseArray= detector.detect(frame);

        if (barcodeSparseArray.size()>0) {
            Barcode result = barcodeSparseArray.valueAt(0);
//            txtresult.setText(result.rawValue);
            String serviceOTP=scanTxt(result.rawValue);
            if (serviceOTP.contains("#") ||serviceOTP.contains("@") || serviceOTP.contains("%") || serviceOTP.contains("&")) {
                showDialouge();
            }else {
                shareWhatsapp(serviceOTP);
            }
        }else {
            Toast.makeText(this, "Select a QR code Or QR Code is Not Clear", Toast.LENGTH_SHORT).show();
        }



    }


    public void SelectImg(View view) {

        Intent i = new Intent();
        i.setType("image/jpeg");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Qr Code"),PICK_IMAGE);
        txtresult.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            uri = data.getData();
            try {
                mbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                img.setImageBitmap(mbitmap);
                scan.setVisibility(View.VISIBLE);
            }
            catch (IOException e)
            {

            }



        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,101,0,"Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i=item.getItemId();
        switch (i){
            case 101:
                Intent intent=new Intent(this,login.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
//    #@%&

    String scanTxt(String raw){
        StringBuilder Id = new StringBuilder();
        StringBuilder Id1 = new StringBuilder();
        if (raw.contains("#") ||raw.contains("@") || raw.contains("%") || raw.contains("&")) {
            return raw;
        }else {
            for (int i = 0; i <= raw.length() - 1; i++) {
                char d = raw.charAt(i);
                if (Character.isLetter(d)) {
                    int ASCII = d;
                    ASCII = ASCII + i;
                    if (ASCII == 91) {
                        d = 'o';
                    } else if (ASCII == 92) {
                        d = 'p';
                    } else if (ASCII == 93) {
                        d = 'q';
                    } else if (ASCII == 94) {
                        d = 'r';
                    } else if (ASCII == 95) {
                        d = 's';
                    } else if (ASCII == 96) {
                        d = 't';
                    } else {
                        d = (char) ASCII;
                    }
                    Id.append(d);
                } else {
                    Id.append(d);
                }
            }
            for (int j = 0; j <= Id.length() - 1; j++) {
                char p = Id.charAt(j);
                if (j % 4 == 0) {
                    Id1.append(" " + p);
                } else {
                    Id1.append(p);
                }
            }
            Toast.makeText(this, Id1, Toast.LENGTH_SHORT).show();
            return String.valueOf(Id);
        }

    }

    void shareWhatsapp(String text){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Service OTP");
        intent.putExtra(Intent.EXTRA_TEXT,"This is Service OTP:- "+text);
        startActivity(Intent.createChooser(intent,"Share Using"));
    }

    void showDialouge(){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.print_count_dialouge);
        dialog.setTitle("Print Count OTP");
        dialog.setCancelable(false);
        cardViewButton=(CardView) dialog.findViewById(R.id.generate);
        cardViewButton1=(CardView) dialog.findViewById(R.id.cancel);
        editText=(EditText) dialog.findViewById(R.id.editText3);
        cardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVarified()){
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    boolean isVarified(){
        boolean isValidate=true;
        int i= Integer.parseInt(editText.getText().toString());
        if (!(editText.getText().toString().contains("00") || editText.getText().toString().contains("000"))){
            isValidate=false;
            editText.setError("Not Multiple of 100");
        }else if(i>9999){
            isValidate=false;
            editText.setError("Greater Than 9999");
        }
        return isValidate;
    }
}

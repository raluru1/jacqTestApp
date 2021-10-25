package com.example.imageclassification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imageclassification.ml.MobilenetV110224Quant;
import com.example.imageclassification.ml.MobilenetV110224Quant.Outputs;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.android.jacquard.sdk.JacquardManager;
import com.google.android.jacquard.sdk.rx.Signal;
import com.google.android.jacquard.sdk.tag.AdvertisedJacquardTag;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class MainActivity extends AppCompatActivity {

  Bitmap image;
  private AppBarConfiguration appBarConfiguration;
  private ArrayList<AdvertisedJacquardTag> tags = new ArrayList<>();


  public final int getMax(@NotNull float[] arr) {
    Intrinsics.checkParameterIsNotNull(arr, "arr");
    int ind = 0;
    float min = 0.0F;
    int i = 0;

    for(short var5 = 1000; i <= var5; ++i) {
      if (arr[i] > min) {
        min = arr[i];
        ind = i;
      }
    }
    return ind;
  }

  public final void checkandGetpermissions() {
    if (this.checkSelfPermission("android.permission.CAMERA") == this.checkSelfPermission("android.permission.DENIED")) {
      this.requestPermissions(new String[]{"android.permission.CAMERA"}, 100);
    } else {
      Toast.makeText((Context)this, (CharSequence)"Camera permission granted", Toast.LENGTH_LONG).show();
    }

    if (this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == this.checkSelfPermission("android.permission.DENIED")) {
      this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 100);
    } else {
      Toast.makeText((Context)this, (CharSequence)"SDcard permission granted", Toast.LENGTH_LONG).show();
    }

    if (this.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == this.checkSelfPermission("android.permission.DENIED")) {
      this.requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 100);
    } else {
      Toast.makeText((Context)this, (CharSequence)"Location permission granted", Toast.LENGTH_LONG).show();
    }

    if (this.checkSelfPermission("android.permission.BLUETOOTH") == this.checkSelfPermission("android.permission.DENIED")) {
      this.requestPermissions(new String[]{"android.permission.BLUETOOTH"}, 100);
    } else {
      Toast.makeText((Context)this, (CharSequence)"Bluetooth permission granted", Toast.LENGTH_LONG).show();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Button button = findViewById(R.id.button);
    final Button button2 = findViewById(R.id.button2);
    final ImageView imageView = findViewById(R.id.imageView2);
    final TextView textView = findViewById(R.id.textView);
    final Button cameraButton = findViewById(R.id.camerabtn);

    RecyclerView recyclerView = findViewById(R.id.tag_recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new TagListAdapter(tags));

        button.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            Log.d("Automation#", "clicking R.id.button Select Image");
            // Code here executes on main thread after user presses button
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 250);
          }
        });

    button2.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v)  {
        checkandGetpermissions();
        Log.d("Automation#", "clicking R.id.button Make Prediction");
        // Code here executes on main thread after user presses button
        BitmapFactory.Options options = new BitmapFactory.Options();

          Bitmap resized = Bitmap.createScaledBitmap(image, 224, 224, true);

        TensorImage tbuffer = TensorImage.fromBitmap(resized);
        ByteBuffer byteBuffer = tbuffer.getBuffer();

        TensorBuffer probabilityBuffer =
            TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
        probabilityBuffer.loadBuffer(byteBuffer);
        try {
          MobilenetV110224Quant model = MobilenetV110224Quant
              .newInstance((Context) MainActivity.this);

          Outputs outputs = model.process(probabilityBuffer);
          TensorBuffer tensorBuffer = outputs.getOutputFeature0AsTensorBuffer();
          Log.d("mssg", "NSMR FLOAT ID " +tensorBuffer.getFloatArray() );

          int intValue = getMax(tensorBuffer.getFloatArray());
           List<String> labelList = new ArrayList<>();
           BufferedReader reader =
              new BufferedReader(new InputStreamReader(MainActivity.this.getAssets().open
                  ("labels.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
              labelList.add(line);
            }
           textView.setText(labelList.get(intValue));
          Log.d("mssg", "NSMR ID " +labelList.get(intValue) );
          Log.d("mssg", "NSMR ID " +labelList.get(700) );
          Log.d("mssg", "NSMR ID " +labelList.get(500) );
          Log.d("mssg", "NSMR ID " +labelList.get(9) );
          model.close();
         } catch (Exception e)  {
        }
      }
    });

    cameraButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        checkandGetpermissions();
        Log.d("Automation#", "clicking R.id.button Make cameraButton");
        // Code here executes on main thread after user presses button
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 200);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d("Automation#", "is OnResume being called ");
    checkandGetpermissions();
    startScan();
  }
/*
  private final ActivityResultLauncher<String> requestPermissionLauncher =
      registerForActivityResult(
          new ActivityResultContracts.RequestPermission(),
          isGranted -> {
            if (isGranted) {
              startScan();
            }
          });



  private boolean hasPermissions() {
    if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
      return true;
    } else if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
      // User has denied the permission. Its time to show rationale.
      return false;
    } else {
      requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
      return false;
    }
  }

 */

  private void startScan() {
    JacquardManager jacquardManager = JacquardManager.getInstance();
    Signal<List<AdvertisedJacquardTag>> scanningSignal = jacquardManager.startScanning().distinct()
        .scan(tags,
            (tagList, tag) -> {
              tagList.add(tag);
              return tagList;
            });
    scanningSignal.onNext(tagList -> {
      // Notify RecyclerView adapter to update the list
      RecyclerView recyclerView = findViewById(R.id.tag_recyclerview);
      recyclerView.getAdapter().notifyDataSetChanged();
    });
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case 250:
        if (resultCode == Activity.RESULT_OK) {
          ImageView imageView = (ImageView) findViewById(R.id.imageView2);
          try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

           // final Bitmap b = BitmapFactory.decodeFile("/sdcard/DCIM/Camera/IMG_20200706_125619.jpg", options);
            imageView.setImageBitmap(bitmap);
            image = bitmap;
          } catch (Exception e) {
          }
        }
        break;
      case 200:
        if (resultCode == Activity.RESULT_OK) {
          ImageView imageView = (ImageView) findViewById(R.id.imageView2);
          Bitmap photo = (Bitmap) data.getExtras().get("data");
          imageView.setImageBitmap(photo);
          image = photo;
        }
    }
  }
}
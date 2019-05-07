package com.example.example4;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.io.FileWriter;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.text.method.ScrollingMovementMethod;

import org.w3c.dom.Text;


/**
 *
 */
public class MainActivity extends Activity implements SensorEventListener {

    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;
    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;
    /**
     * The text view.
     */
    private TextView textRssi,currentX, currentY, currentZ, currentDX, currentDY, currentDZ,
            textViewCellA, textViewCellB, textViewCellC, textViewCellD;
    /**
     * The button.
     */
    private Button buttonRssi;
    /**
     * Accelerometer x value
     */
    private float aX = 0;
    /**
     * Accelerometer y value
     */
    private float aY = 0;
    /**
     * Accelerometer z value
     */
    private float aZ = 0;
    /**
     * Accelerometer x value
     */
    private float dX = 0;
    /**
     * Accelerometer y value
     */
    private float dY = 0;
    /**
     * Accelerometer z value
     */
    private float dZ = 0;

    private int fileCount = 0;

    private ProgressBar progressBar;

    private Switch trainingSwitch;

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            return true;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create items.
        textRssi = (TextView) findViewById(R.id.textRSSI);
        buttonRssi = (Button) findViewById(R.id.buttonRSSI);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        currentDX = (TextView) findViewById(R.id.currentDX);
        currentDY = (TextView) findViewById(R.id.currentDY);
        currentDZ = (TextView) findViewById(R.id.currentDZ);
        trainingSwitch = (Switch) findViewById(R.id.switchTest);
        textViewCellA = (TextView) findViewById(R.id.textViewCellA);
        textViewCellB = (TextView) findViewById(R.id.textViewCellB);
        textViewCellC = (TextView) findViewById(R.id.textViewCellC);
        textViewCellD = (TextView) findViewById(R.id.textViewCellD);

        textViewCellA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cellAChecked() && trainingSwitch.isChecked()) {
                    textViewCellA.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewCellB.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellC.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellD.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        textViewCellB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cellBChecked() && trainingSwitch.isChecked()) {
                    textViewCellA.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellB.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewCellC.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellD.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        textViewCellC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cellCChecked() && trainingSwitch.isChecked()) {
                    textViewCellA.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellB.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellC.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewCellD.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        textViewCellD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cellDChecked() && trainingSwitch.isChecked()) {
                    textViewCellA.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellB.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellC.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    textViewCellD.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        // Set listener for the button.
        buttonRssi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickScanButton(v);
            }
        });

        trainingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked){
                if(isChecked){
                    buttonRssi.setText("Scan training");
                } else {
                    buttonRssi.setText("Scan location");
                }
                textViewCellA.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textViewCellB.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textViewCellC.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textViewCellD.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        textRssi.setMovementMethod(new ScrollingMovementMethod());

        // Set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // No accelerometer!
        }
        checkPermissions();
    }

    private void handleWiFiScan(Context c, Intent intent, boolean training){
        boolean success = intent.getBooleanExtra(
                WifiManager.EXTRA_RESULTS_UPDATED, false);
        progressBar.setVisibility(View.INVISIBLE);
        if (success) {
            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if(training){
                //TODO File handling
                textRssi.setText("\n\tWriting file to: " + c.getExternalFilesDir(null).toString());
                try {
                    FileWriter file = new FileWriter(c.getExternalFilesDir(null).toString() + " \\" +  getCheckedCellName());

                    file.flush();
                    file.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                textRssi.setText("");
                for (ScanResult scanResult : scanResults) {
                    //if((scanResult.SSID.equals("eduroam")))
                    textRssi.setText(textRssi.getText() + "\n\t"
                            + scanResult.BSSID + " " + scanResult.level);
                }
            }
            // Write results to a label

        } else {
            // scan failure handling
            textRssi.setText("\n\tScanResult no success");
        }
    }

    private void onClickScanButton(View v){
        checkPermissions();
        // Check if any cell is checked for training
        if(trainingSwitch.isChecked() && !anyCellChecked()){
            textRssi.setText("\n\tSelect a cell to train");
            return;
        }
        textRssi.setText("\n\tScanning...");
        // Set wifi manager.
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                handleWiFiScan(c, intent, trainingSwitch.isChecked());
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        // Start a wifi scan.
        if (!wifiManager.startScan()){
            textRssi.setText("\n\tStartscan error!");
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
        currentDX.setText("0.0");
        currentDY.setText("0.0");
        currentDZ.setText("0.0");

        dX = aX;
        dY = aY;
        dZ = aZ;

        // get the the x,y,z values of the accelerometer
        aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];

        // display the current x,y,z accelerometer values
        currentX.setText(Float.toString(aX));
        currentY.setText(Float.toString(aY));
        currentZ.setText(Float.toString(aZ));
        currentDX.setText(Float.toString(aX - dX));
        currentDY.setText(Float.toString(aY - dY));
        currentDZ.setText(Float.toString(aZ - dZ));

    }

    private boolean cellAChecked(){
        return ((ColorDrawable) textViewCellA.getBackground()).getColor() == getResources().getColor(R.color.colorAccent);
    }

    private boolean cellBChecked(){
        return ((ColorDrawable) textViewCellB.getBackground()).getColor() == getResources().getColor(R.color.colorAccent);
    }

    private boolean cellCChecked(){
        return ((ColorDrawable) textViewCellC.getBackground()).getColor() == getResources().getColor(R.color.colorAccent);
    }

    private boolean cellDChecked(){
        return ((ColorDrawable) textViewCellD.getBackground()).getColor() == getResources().getColor(R.color.colorAccent);
    }

    private boolean anyCellChecked(){
        return cellAChecked() || cellBChecked() || cellCChecked() || cellDChecked();
    }

    private String getCheckedCellName(){
        String name = "";
        if(cellAChecked()){
            name = (String) textViewCellA.getText();
        } else if(cellBChecked()) {
            name = (String) textViewCellB.getText();
        } else if(cellCChecked()) {
            name = (String) textViewCellC.getText();
        } else if(cellDChecked()) {
            name = (String) textViewCellD.getText();
        }
        return name;
    }
}
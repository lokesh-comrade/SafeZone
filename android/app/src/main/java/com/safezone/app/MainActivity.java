package com.safezone.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SafeZoneNative";
    private static final int PERMISSION_REQ_CODE = 1001;

    private WebView webView;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Kinematic 5-shake detector state
    private int shakeCount = 0;
    private long lastShakeTime = 0;
    private static final float SHAKE_THRESHOLD = 18.0f;
    private static final long SHAKE_WINDOW_MS = 2000;

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hardware-accelerated full-screen WebView
        webView = new WebView(this);
        setContentView(webView);

        initWebViewSettings();
        checkAndRequestPermissions();
        initShakeSensor();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Register Native JavaScript Interface
        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> {
                    // Auto-grant WebRTC audio/video capture
                    request.grant(request.getResources());
                });
            }
        });

        // Load bundled Universal Mobile Web Core
        webView.loadUrl("file:///android_asset/app.html");
    }

    private void checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_REQ_CODE);
        }
    }

    private void initShakeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                Log.d(TAG, "Accelerometer sensor registered for 5-shake detection");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > (SHAKE_THRESHOLD / SensorManager.GRAVITY_EARTH)) {
                long now = System.currentTimeMillis();

                if (now - lastShakeTime > 250) {
                    if (now - lastShakeTime > SHAKE_WINDOW_MS) {
                        shakeCount = 0;
                    }
                    shakeCount++;
                    lastShakeTime = now;
                    Log.d(TAG, "Kinematic shake detected: count = " + shakeCount);

                    if (shakeCount >= 5) {
                        shakeCount = 0;
                        onFiveShakesTriggered();
                    }
                }
            }
        }
    }

    private void onFiveShakesTriggered() {
        runOnUiThread(() -> {
            Toast.makeText(this, "🚨 5-Shake Gesture: SafeZone SOS Triggered!", Toast.LENGTH_LONG).show();
            vibrateDevice(800);

            // Inform JavaScript engine
            if (webView != null) {
                webView.evaluateJavascript("if(window.triggerSOS){ window.triggerSOS('SHAKE_5X'); }", null);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }

    private void vibrateDevice(long ms) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(ms);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // =========================================================================
    // NATIVE JAVASCRIPT INTERFACE BRIDGE
    // =========================================================================
    public class AndroidBridge {
        private final Context context;

        public AndroidBridge(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void sendSMS(String phoneNumber, String message) {
            Log.d(TAG, "Bridge sendSMS: " + phoneNumber + " -> " + message);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
                    runOnUiThread(() -> Toast.makeText(context, "Emergency SMS dispatched to " + phoneNumber, Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e(TAG, "SMS dispatch error", e);
                }
            } else {
                Log.w(TAG, "SEND_SMS permission not granted");
            }
        }

        @JavascriptInterface
        public void makeCall(String phoneNumber) {
            Log.d(TAG, "Bridge makeCall: " + phoneNumber);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Call initiation error", e);
                }
            } else {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialIntent);
            }
        }

        @JavascriptInterface
        public void vibrateDevice(long milliseconds) {
            MainActivity.this.vibrateDevice(milliseconds);
        }

        @JavascriptInterface
        public int getBatteryLevel() {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
            return 85;
        }

        @JavascriptInterface
        public boolean isNativeApp() {
            return true;
        }
    }
}

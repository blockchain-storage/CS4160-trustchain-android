package nl.tudelft.cs4160.trustchain_android.qr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import org.libsodium.jni.Sodium;
import org.libsodium.jni.encoders.Raw;
import org.libsodium.jni.keys.KeyPair;

import java.math.BigInteger;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;

public class ScanQRActivity extends AppCompatActivity {
    private Vibrator vibrator;

    private ZXingScannerView scannerView;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        scannerView = findViewById(R.id.scanner_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Camera request permission handling
        if (hasCameraPermission()) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestCameraPermission();
                    }
                };

                new AlertDialog.Builder(this).setTitle(R.string.camera_permissions_required)
                        .setMessage(R.string.camera_permisions_required_long)
                        .setNeutralButton(android.R.string.ok, null)
                        .setOnDismissListener(dismissListener)
                        .show();
            } else {
                requestCameraPermission();
            }
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                finish();
            }
        }
    }

    private void startCamera() {
        scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            public void handleResult(Result result) {
                new AlertDialog.Builder(ScanQRActivity.this)
                        .setTitle(result.getBarcodeFormat().toString())
                        .setMessage(result.getText())
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
                vibrator.vibrate(100);

                processBytes(result.getRawBytes());

                scannerView.resumeCameraPreview(this);
            }
        });
        scannerView.startCamera();
    }

    private void processBytes(byte[] bytes) {
        int pkLength = Sodium.crypto_box_secretkeybytes();
        int vkLength = Sodium.crypto_sign_ed25519_bytes();

        if (bytes.length != pkLength + vkLength)
            throw new RuntimeException("Should be " + (pkLength + vkLength) + " length but was " + bytes.length);

        byte[] pk = Arrays.copyOfRange(bytes, 0, pkLength);
        byte[] vk = Arrays.copyOfRange(bytes, pkLength, vkLength);

        System.out.println("pk: " + new BigInteger(1, pk).toString(16));
        System.out.println("vk: " + new BigInteger(1, vk).toString(16));

        KeyPair kp = new KeyPair(new Raw().encode(vk), new Raw());
        Key.saveKeyPair(ScanQRActivity.this, kp);
    }

    private boolean hasCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}

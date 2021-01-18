package com.ikhokha.techcheck;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.ikhokha.techcheck.databinding.ActivityBarcodeBinding;
import com.ikhokha.techcheck.ui.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BarcodeActivity extends BaseActivity {

    private ActivityBarcodeBinding mBinding;

    private CameraSource mCameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode);
        mBinding.setLifecycleOwner(this);

        initialize();
    }

    private void initialize() {
        mBinding.btnProceed.setOnClickListener(view -> onBackPressed());
    }

    private void initialiseDetectorsAndSources() {
        BarcodeDetector mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        mCameraSource = new CameraSource.Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        mBinding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        mCameraSource.start(mBinding.surfaceView.getHolder());
                    else requestPermission();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NotNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() != 0) {
                    mBinding.layoutBarcodeFragment.post(() -> {
                        if (barcode.valueAt(0).email != null)
                            mBinding.layoutBarcodeFragment.removeCallbacks(null);
                        else getShoppingCart(barcode.valueAt(0).displayValue, true);
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraSource.release();
    }
}

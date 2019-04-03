package dozzercoder.wordpress.com.tflitedemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.fingerpaintview.FingerPaintView;

import androidx.appcompat.app.AppCompatActivity;
import dozzercoder.wordpress.com.tflitedemo.ml.Classifier;
import dozzercoder.wordpress.com.tflitedemo.ml.Result;
import dozzercoder.wordpress.com.tflitedemo.utils.ImageUtil;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int PIXEL_WIDTH = 28;

    private Classifier mClassifier;

    private FingerPaintView mPaintView;

    private Button mResetButton, mDetectButton;

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wireUpWidgets();
        setUpClassifier();
        setListeners();
    }

    private void setUpClassifier() {
        try {
            mClassifier = new Classifier(this);
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to create classifier...", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "setUpClassifier(): Failed to create tflite model", ex);
        }
    }

    private void setListeners() {
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultTextView.setText("");
                mPaintView.clear();
            }
        });

        mDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClassifier == null)
                    Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
                else if (mPaintView.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please write a digit...", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap image = mPaintView.exportToBitmap(
                            Classifier.DIM_IMG_SIZE_WIDTH, Classifier.DIM_IMG_SIZE_HEIGHT);
                    // The model is trained on images with black background and white font
                    Bitmap inverted = ImageUtil.invert(image);
                    Result result = mClassifier.classify(inverted);
                    mResultTextView.setText("Result : " + result.getNumber());
                }
            }
        });
    }

    private void wireUpWidgets() {
        mPaintView = findViewById(R.id.paintView);
        mResetButton = findViewById(R.id.button_clear);
        mDetectButton = findViewById(R.id.button_detect);
        mResultTextView = findViewById(R.id.text_result);
    }

}

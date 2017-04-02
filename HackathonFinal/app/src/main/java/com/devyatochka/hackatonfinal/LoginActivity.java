package com.devyatochka.hackatonfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import javax.crypto.Cipher;

/**
 * Created by alexbelogurow on 31.03.17.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String PIN = "pin";
    private static final String UUID = "uuid";

    private TextView mTextViewEnterPinCode;
    private EditText mEditTextPinCode;
    private Button mButtonLogin;
    private ImageView mImageViewFingerPrint;

    private SharedPreferences mPreferences;
    private SharedPreferences mIDPreferences;
    private String correctPin;
    private String correctUUID;

    private FingerprintHelper mFingerprintHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTextViewEnterPinCode = (TextView) findViewById(R.id.textViewEnterPinCode);
        mEditTextPinCode = (EditText) findViewById(R.id.editTextPinCode);
        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
        mImageViewFingerPrint = (ImageView) findViewById(R.id.imageViewFingerPrint);

        mPreferences = getSharedPreferences("pin_code", Context.MODE_PRIVATE);
        mIDPreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        correctPin = mPreferences.getString(PIN, "-1");
        correctUUID = mIDPreferences.getString(UUID,"-1");
        Log.i("MainPIN", correctPin);
        if (correctPin != "-1" && correctUUID != "-1"
            && FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, this)) {
            mTextViewEnterPinCode.setText(mTextViewEnterPinCode.getText() + " or use Fingerprint");
            mImageViewFingerPrint.setVisibility(View.VISIBLE);
        }

        mEditTextPinCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Perform action on key press
                    prepareLogin();
                    return false;
                }
                return false;
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareLogin();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreferences.contains(PIN)) {
            prepareSensor();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFingerprintHelper != null) {
            mFingerprintHelper.cancel();
        }
    }

    private void prepareLogin() {


        mPreferences = getSharedPreferences("pin_code", Context.MODE_PRIVATE);
        mIDPreferences = getSharedPreferences("id",Context.MODE_PRIVATE);
        correctPin = mPreferences.getString(PIN, "-1");
        correctUUID = mPreferences.getString(UUID,"-1");

        final String pin = mEditTextPinCode.getText().toString();
        Log.i("PIN", correctPin + " : " + pin);
        // первый вход correctPin = -1
        if (correctPin.equals("-1")) {
            if (pin.length() == 4) {

                savePin(pin);
                Intent mainActivity = new Intent(this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivity);
                mEditTextPinCode.setText("");
            } else {
                Toast.makeText(this, "Pin code must contain 4 characters", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // последующий вход correctPin != -1
            if (pin.length() == 4 && pin.equals(correctPin)) {
                //Log.i("encode", CryptoUtils.encode(pin).length() + "");
                Intent mainActivity = new Intent(this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivity);
                mEditTextPinCode.setText("");
            }
            else {
                if (pin.length() != 4) {
                    Toast.makeText(this, "Pin code must contain 4 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Pin is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /*if (correctPin != "-1") {
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            final String pin = mEditTextPinCode.getText().toString();
            if (pin.length() > 0) {
                savePin(pin);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "pin is empty", Toast.LENGTH_SHORT).show();
            }
        }


        /*}
        if (pin.length() > 0) {
            savePin(pin);
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "pin is empty", Toast.LENGTH_SHORT).show();
        } */
    }

    private void savePin(String pin) {
//        String encoded = CryptoUtils.encode(pin);
//        Log.i("encode", CryptoUtils.encode(pin).length() + " ");
        mPreferences.edit().putString(PIN, pin).apply();
        mIDPreferences.edit().putString(UUID, java.util.UUID.randomUUID().toString()).apply();
        Log.i("Input pin", pin);
//        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, this)) {
//            encoded = CryptoUtils.encode(pin);
//            Log.i("encoded",encoded.length() + " ");
        //    mPreferences.edit().putString(PIN, encoded).apply();
//        }
    }

    private void prepareSensor() {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, this)) {
            FingerprintManagerCompat.CryptoObject cryptoObject = CryptoUtils.getCryptoObject();
            if (cryptoObject != null) {
                //Toast.makeText(this, "use fingerprint to login", Toast.LENGTH_LONG).show();
                mFingerprintHelper = new FingerprintHelper(this);
                mFingerprintHelper.startAuth(cryptoObject);
            } else {
                mPreferences.edit().remove(PIN).apply();
                mIDPreferences.edit().remove(UUID).apply();
                Toast.makeText(this, "new fingerprint enrolled. enter pin again", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {
        private Context mContext;
        private CancellationSignal mCancellationSignal;

        FingerprintHelper(Context context) {
            mContext = context;
        }

        void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
            mCancellationSignal = new CancellationSignal();
            FingerprintManagerCompat manager = FingerprintManagerCompat.from(mContext);
            manager.authenticate(cryptoObject, 0, mCancellationSignal, this, null);
        }

        void cancel() {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            //Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            //Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            /*Cipher cipher = result.getCryptoObject().getCipher();
            String encoded = mPreferences.getString(PIN, null);
            String decoded = CryptoUtils.decode(encoded, cipher);
            //mEditTextPinCode.setText(decoded);
            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show(); */

            mEditTextPinCode.setText(mPreferences.getString(PIN, "-1"));
            //Toast.makeText(mContext, encoded.length(), Toast.LENGTH_SHORT).show();



            // переход в main
            prepareLogin();
        }

        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show();
        }

    }


}
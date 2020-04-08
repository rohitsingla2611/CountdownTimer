package rohitsingla.rdrock.countdowntimerpart5;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    long backKeyPressedTime;
    int hours, minutes, seconds;
    String strFormatTimeLeft;
    private long timeStartingInMilliseconds;
    private TextView textViewTime;
    private Button buttonStartPause, buttonReset, buttonConfirm;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    private long timeLeftInMilliSeconds;
    private long timeEndInMilliSeconds;
    private EditText editTextTimerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    void initViews() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.countdown_timer);

        editTextTimerValue = findViewById(R.id.editTextTimerValue);
        textViewTime = findViewById(R.id.textViewTime);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        buttonReset = findViewById(R.id.buttonReset);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        buttonStartPause.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        timeStartingInMilliseconds = sharedPreferences.getLong("TIME START", 660000);
        timeLeftInMilliSeconds = sharedPreferences.getLong("TIME LEFT", timeStartingInMilliseconds);
        isTimerRunning = sharedPreferences.getBoolean("TIMER RUNNING", false);

        updateCountdownText();
        updateButtons();
        if (isTimerRunning) {
            timeEndInMilliSeconds = sharedPreferences.getLong("END TIME", 0);
            timeLeftInMilliSeconds = timeEndInMilliSeconds - System.currentTimeMillis();
            if (timeLeftInMilliSeconds < 1000) {
                timeLeftInMilliSeconds = 0;
                isTimerRunning = false;
                updateButtons();
                updateCountdownText();
            } else {
                startTimer();
            }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("TIME START", timeStartingInMilliseconds);
        editor.putLong("TIME LEFT", timeLeftInMilliSeconds);
        editor.putLong("END TIME", timeEndInMilliSeconds);
        editor.putBoolean("TIMER RUNNING", isTimerRunning);
        editor.apply();
        if (countDownTimer != null)
            countDownTimer.cancel();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfirm:
                String inputValue = editTextTimerValue.getText().toString();
                if (inputValue.length() == 0) {
                    Toast.makeText(this, "Please Enter Timer value", Toast.LENGTH_SHORT).show();
                    return;
                }
                long inputValueMinutes = Long.parseLong(inputValue) * 60000;
                if (inputValueMinutes == 0) {
                    Toast.makeText(this, "Value Can't be Zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTimerValue(inputValueMinutes);
                editTextTimerValue.setText("");
                return;
            case R.id.buttonStartPause:
                if (isTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
                return;

            case R.id.buttonReset:
                resetTimer();


        }
    }

    private void setTimerValue(long setTimeStartingInMilliseconds) {
        timeStartingInMilliseconds = setTimeStartingInMilliseconds;
        resetTimer();
    }

    @SuppressLint("SetTextI18n")
    private void startTimer() {

        timeEndInMilliSeconds = System.currentTimeMillis() + timeLeftInMilliSeconds;
        countDownTimer = new CountDownTimer(timeLeftInMilliSeconds, 1000) {
            @Override
            public void onTick(long milliSecondsUntilFinished) {

                timeLeftInMilliSeconds = milliSecondsUntilFinished;
                updateCountdownText();

            }

            @Override
            public void onFinish() {


                isTimerRunning = false;
                timeLeftInMilliSeconds = 0;
                updateButtons();

            }
        }.start();
        isTimerRunning = true;
        updateButtons();
    }

    @SuppressLint("SetTextI18n")
    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        updateButtons();


    }

    private void resetTimer() {
        timeLeftInMilliSeconds = timeStartingInMilliseconds;
        updateCountdownText();
        updateButtons();

    }

    private void updateCountdownText() {
        hours = (int) (timeLeftInMilliSeconds / 1000) / 3600;
        minutes = (int) (timeLeftInMilliSeconds / 1000) % 3600 / 60;
        seconds = (int) (timeLeftInMilliSeconds / 1000) % 60;
        if (hours > 0)
            strFormatTimeLeft = String.format(Locale.getDefault(),
                    "%2d:%02d:%02d", hours, minutes, seconds);
        else
            strFormatTimeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewTime.setText(strFormatTimeLeft);
    }

    @SuppressLint("SetTextI18n")
    private void updateButtons() {
        if (isTimerRunning) {

            editTextTimerValue.setVisibility(View.INVISIBLE);
            buttonConfirm.setVisibility(View.INVISIBLE);
            buttonReset.setVisibility(View.INVISIBLE);
            buttonStartPause.setText("PAUSE");

        } else {
            buttonStartPause.setText("START");

            editTextTimerValue.setVisibility(View.VISIBLE);
            buttonConfirm.setVisibility(View.VISIBLE);
            if (timeLeftInMilliSeconds == timeStartingInMilliseconds) {

                buttonStartPause.setVisibility(View.VISIBLE);
                buttonReset.setVisibility(View.INVISIBLE);
            } else if (timeLeftInMilliSeconds < 1000) {
                buttonReset.setVisibility(View.VISIBLE);
                buttonStartPause.setVisibility(View.INVISIBLE);
            } else {
                buttonStartPause.setVisibility(View.VISIBLE);
                buttonReset.setVisibility(View.VISIBLE);


            }

        }
    }

    @Override
    public void onBackPressed() {
        if (backKeyPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show();
        }
        backKeyPressedTime = System.currentTimeMillis();
    }

}

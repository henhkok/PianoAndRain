package henhkok.pianoandrain;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Gravity;
import android.widget.TimePicker;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    int DIALOG_TIME = 1;
    int myHour = 00;
    int myMinute = 05;

    private ProgressBar playSeekBar;

    private Button buttonstarttime;

    private Button buttonPlay;

    private Button buttonInfo;

    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 10;
    private TextView countdownDisplay;
    private CountDownTimer timer;

    boolean connected = false;
    Context context;
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIElements();

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())){
                flag=true;
                buttonPlay.setBackgroundResource(R.drawable.greypause90);
            }
        }
    }

    private void initializeUIElements() {

        playSeekBar = (ProgressBar) findViewById(R.id.progressBar1);
        playSeekBar.setMax(100);
        playSeekBar.setVisibility(View.INVISIBLE);

        buttonstarttime = (Button) findViewById(R.id.startButton);
        buttonstarttime.setOnClickListener(this);

        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setBackgroundResource(R.drawable.greyplay90);
        buttonPlay.setOnClickListener(this);

        buttonInfo = (Button) findViewById(R.id.btnStar);
        buttonInfo.setOnClickListener(this);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
    }

    boolean flag=false;
    @Override
    public void onClick(View v) {

        context = getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            if (v == buttonPlay) {
                if (flag) {
                    buttonPlay.setBackgroundResource(R.drawable.greyplay90);
                    stopService(new Intent(this, MyService.class));
                }
                else {
                    // возвращаем первую картинку
                    buttonPlay.setBackgroundResource(R.drawable.greypause90);
                    startService(new Intent(this, MyService.class));
                }
                flag = !flag;
            } else if (v== buttonstarttime){
                //showTimer(myHour * MILLIS_PER_SECOND);
                countdownDisplay.setVisibility(View.VISIBLE);
                showDialog(DIALOG_TIME);
            } else if (v==buttonInfo)
                this.Feedback(v);
        }
        else {
            connected = false;
            Toast toast = Toast.makeText(context, R.string.Connecting, duration);
            toast.setGravity(Gravity.BOTTOM, 0, 220);
            toast.show();
        }
    }

    private void showTimer(int countdownMillis) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(countdownMillis, MILLIS_PER_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {

                //final long start = mStartTime;
                //long millis = SystemClock.uptimeMillis() - start;
                int seconds = (int) (millisUntilFinished / 1000);
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                seconds = seconds % 60;

                if (seconds < 10) {
                    if (minutes < 10) {
                        countdownDisplay.setText("" + hours + ":0" + minutes + ":0" + seconds);
                    } else {
                        countdownDisplay.setText("" + hours + ":" + minutes + ":0" + seconds);
                    }
                } else {
                    if (minutes < 10) {
                        countdownDisplay.setText("" + hours + ":0" + minutes + ":" + seconds);
                    } else {
                        countdownDisplay.setText("" + hours + ":" + minutes + ":" + seconds);
                    }
                }
            }

            @Override
            public void onFinish() {
                countdownDisplay.setText(R.string.TimeOver);
                stopService(new Intent(MainActivity.this, MyService.class));
                NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(56248);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                //System.runFinalizersOnExit(true);
                //System.exit(0);
            }
        }.start();
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            tpd.setTitle(R.string.setTimer);
            return tpd;
        }
        return super.onCreateDialog(id);
    }
    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            myHour = myHour*3600 + myMinute*60;
            showTimer(myHour * MILLIS_PER_SECOND);
        }
    };

    public void Feedback(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=henhkok.pianoandrain"));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(browserIntent);
    }

    @Override
    public void onDestroy()
    {
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(56248);
        super.onDestroy();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.Exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(new Intent(MainActivity.this, MyService.class));
                        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.cancel(56248);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //finish();
                        //System.runFinalizersOnExit(true);
                        //System.exit(0);
                    }
                }).setNegativeButton(R.string.no, null).show();
    };
}

/**
 * @author Matei Mircea
 * 
 * The alarm activity is displayed when the ana larm goes of for a aschedule item
 */

package ro.mmp.tic.activities.streetmap;

import java.io.IOException;

import ro.mmp.tic.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends Activity {
	private MediaPlayer mMediaPlayer;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("TouristInfoCenter Alarm");

		this.setFinishOnTouchOutside(false);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_alarm);

		TextView allarmInfotext = (TextView) findViewById(R.id.alarmInfoText);
		Intent intent = getIntent();
		allarmInfotext.setText("You have a schedule item pending at: \n"
				+ intent.getStringExtra("schedulePlace") + " \n on:"
				+ intent.getStringExtra("scheduleDate") + " \n at:"
				+ intent.getStringExtra("scheduleTime"));

		Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
		stopAlarm.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mMediaPlayer.stop();
				finish();
				return false;
			}
		});

		playSound(this, getAlarmUri());
	}

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("OOPS");
		}
	}

	// Get an alarm sound. Try for an alarm. If none set, try notification,
	// Otherwise, ringtone.
	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}
}
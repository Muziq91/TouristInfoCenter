/**
 * @author Matei Mircea
 * 
 * Helps plan the schedule for the user
 */

package ro.mmp.tic.activities.streetmap.util;

import ro.mmp.tic.activities.streetmap.AlarmActivity;
import ro.mmp.tic.domain.Schedule;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduleAlarm {

	private static int alarmNumber = 0;

	public void setScheduleAllarm(Context context, long alarmTime,
			Schedule schedule) {

		Log.d("ScheduleAlarm", "Set alarm for " + alarmTime);

		Intent intent = new Intent(context, AlarmActivity.class);

		intent.putExtra("scheduleDate", schedule.getDate());
		intent.putExtra("scheduleTime", schedule.getTime());
		intent.putExtra("schedulePlace", schedule.getPlace());

		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				alarmNumber, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
		alarmNumber++;
		Log.d("ScheduleAlarm",
				"Creating the alarm Manager and seting the alarm" + alarmTime);

	}
}

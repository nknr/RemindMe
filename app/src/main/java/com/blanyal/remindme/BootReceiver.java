/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.blanyal.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            ReminderDatabase rb = new ReminderDatabase(context);
            Calendar mCalendar = Calendar.getInstance();
            AlarmReceiver mAlarmReceiver = new AlarmReceiver();

            List<Reminder> reminders = rb.getAllReminders();

            for (Reminder rm : reminders) {
                int mReceivedID = rm.getID();
                String mRepeat = rm.getRepeat();
                String mRepeatNo = rm.getRepeatNo();
                String mRepeatType = rm.getRepeatType();
                String mActive = rm.getActive();
                String mDate = rm.getDate();
                String mTime = rm.getTime();

                String[] mDateSplit = mDate.split("/");
                String[] mTimeSplit = mTime.split(":");

                int mDay = Integer.parseInt(mDateSplit[0]);
                int mMonth = Integer.parseInt(mDateSplit[1]);
                int mYear = Integer.parseInt(mDateSplit[2]);
                int mHour = Integer.parseInt(mTimeSplit[0]);
                int mMinute = Integer.parseInt(mTimeSplit[1]);

                mCalendar.set(Calendar.MONTH, --mMonth);
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);

                // Cancel existing notification of the reminder by using its ID
                // mAlarmReceiver.cancelAlarm(context, mReceivedID);

                // Check repeat type
                long mRepeatTime = 0;

                switch (mRepeatType) {
                    case "Minute":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                        break;
                    case "Hour":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                        break;
                    case "Day":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                        break;
                    case "Week":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                        break;
                    case "Month":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                        break;
                }

                // Create a new notification
                if (mActive.equals("true")) {
                    if (mRepeat.equals("true")) {
                        mAlarmReceiver.setRepeatAlarm(context, mCalendar, mReceivedID, mRepeatTime);
                    } else if (mRepeat.equals("false")) {
                        mAlarmReceiver.setAlarm(context, mCalendar, mReceivedID);
                    }
                }
            }
        }
    }
}
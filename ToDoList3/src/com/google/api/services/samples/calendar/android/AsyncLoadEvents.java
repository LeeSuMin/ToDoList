/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.calendar.android;

import com.google.api.services.calendar.model.Event;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.IOException;

/**
 * Asynchronously delete a calendar with a progress dialog.
 * 
 * @author Ravi Mistry
 */
class AsyncLoadEvents extends AsyncTask<Void, Void, Void> {

  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private final int calendarIndex;
  private com.google.api.services.calendar.Calendar client;

  AsyncLoadEvents(CalendarSample calendarSample, int calendarIndex) {
    this.calendarSample = calendarSample;
    client = calendarSample.client;
    this.calendarIndex = calendarIndex;
    dialog = new ProgressDialog(calendarSample.activity);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Loading Events...");
    dialog.show();
  }

  @SuppressLint("NewApi")
  @Override
  protected Void doInBackground(Void... arg0) {
    String calendarId = calendarSample.calendars.get(calendarIndex).id;
    try {
      calendarSample.events.clear();
      com.google.api.services.calendar.model.Events events = null;
      // 네트워크 처리를 메인 쓰레드에서 처리했으므로 에러가 발생했었음. 따라서 아래의 Async Task로 해결
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
      events = client.events().list(calendarId).execute();
      java.util.List<Event> items = events.getItems();

      for (Event event : items) {
        // Event 에 대한 내용이지만 calendarSample.calendars에 넣기 위해 CalendarInfo로 선언
        EventInfo info =
            new EventInfo(event.getId(), event.getSummary(), event.getStart(), event.getEnd());
        calendarSample.events.add(info);
      }
    } catch (IOException e) {
      calendarSample.handleGoogleException(e);
    } finally {
      calendarSample.onRequestCompleted();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    dialog.dismiss();
    calendarSample.refreshEvents();
  }
}

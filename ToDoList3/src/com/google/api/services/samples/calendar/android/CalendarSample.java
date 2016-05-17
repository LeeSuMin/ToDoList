/*
 * Copyright (c) 2010 Google Inc.
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

import com.example.sonymobile.smartextension.adapter.ToDo;
import com.example.sonymobile.smartextension.todolist.ToDoListActivity;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Lists;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample for Google Calendar API v3. It shows how to authenticate, get
 * calendars, add a new calendar, update it, and delete it.
 * 
 * <p>
 * To enable logging of HTTP requests/responses, change {@link #LOGGING_LEVEL}
 * to {@link Level#CONFIG} or {@link Level#ALL} and run this command:
 * </p>
 * 
 * <pre>
 * adb shell setprop log.tag.HttpTransport DEBUG
 * </pre>
 * 
 * @author Yaniv Inbar
 */
@SuppressLint("NewApi")
public final class CalendarSample extends ListView {
	/** Logging level for HTTP requests/responses. */
	private static final Level LOGGING_LEVEL = Level.OFF;
	private static final String TAG = "CalendarSample";
	private static final String AUTH_TOKEN_TYPE = "cl";
	private static final int REQUEST_AUTHENTICATE = 0;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = new GsonFactory();
	static final String PREF_ACCOUNT_NAME = "accountName";
	static final String PREF_AUTH_TOKEN = "authToken";
	GoogleAccountManager accountManager;
	SharedPreferences settings;
	String accountName;
	String authToken;
	com.google.api.services.calendar.Calendar client;
	List<CalendarInfo> calendars = Lists.newArrayList();
	List<EventInfo> events = Lists.newArrayList();
	private boolean received401;
	Activity activity;
	int index;

	public CalendarSample(Activity activity) {
		super(activity);
		this.activity = activity;

		HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
				request.getHeaders().setAuthorization(
						GoogleHeaders.getGoogleLoginValue(authToken));
			}
		};
		client = new com.google.api.services.calendar.Calendar.Builder(
				transport, jsonFactory, requestInitializer)
				.setApplicationName("Google-CalendarAndroidSample/1.0")
				.setJsonHttpRequestInitializer(
						new GoogleKeyInitializer(ClientCredentials.KEY))
				.build();
		settings = activity.getPreferences(Activity.MODE_PRIVATE);
		accountName = settings.getString(PREF_ACCOUNT_NAME, null);
		authToken = settings.getString(PREF_AUTH_TOKEN, null);
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		accountManager = new GoogleAccountManager(activity);
		activity.registerForContextMenu(this);
		gotAccount();
	}

	void gotAccount() {
		Account account = accountManager.getAccountByName(accountName);
		if (account == null) {
			chooseAccount();
			return;
		}
		if (authToken != null) {
			onAuthToken();
			return;
		}
		accountManager.getAccountManager().getAuthToken(account,
				AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

					public void run(AccountManagerFuture<Bundle> future) {
						try {
							Bundle bundle = future.getResult();
							if (bundle.containsKey(AccountManager.KEY_INTENT)) {
								Intent intent = bundle
										.getParcelable(AccountManager.KEY_INTENT);
								intent.setFlags(intent.getFlags()
										& ~Intent.FLAG_ACTIVITY_NEW_TASK);
								activity.startActivityForResult(intent,
										REQUEST_AUTHENTICATE);
							} else if (bundle
									.containsKey(AccountManager.KEY_AUTHTOKEN)) {
								setAuthToken(bundle
										.getString(AccountManager.KEY_AUTHTOKEN));
								onAuthToken();
							}
						} catch (Exception e) {
							Log.e(TAG, e.getMessage(), e);
						}
					}
				}, null);
	}

	private void chooseAccount() {
		accountManager.getAccountManager().getAuthTokenByFeatures(
				GoogleAccountManager.ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null,
				activity, null, null, new AccountManagerCallback<Bundle>() {

					public void run(AccountManagerFuture<Bundle> future) {
						Bundle bundle;
						try {
							bundle = future.getResult();
							setAccountName(bundle
									.getString(AccountManager.KEY_ACCOUNT_NAME));
							setAuthToken(bundle
									.getString(AccountManager.KEY_AUTHTOKEN));
							onAuthToken();
						} catch (OperationCanceledException e) {
							// user canceled
						} catch (AuthenticatorException e) {
							Log.e(TAG, e.getMessage(), e);
						} catch (IOException e) {
							Log.e(TAG, e.getMessage(), e);
						}
					}
				}, null);
	}

	void setAccountName(String accountName) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, accountName);
		editor.commit();
		this.accountName = accountName;
	}

	void setAuthToken(String authToken) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_AUTH_TOKEN, authToken);
		editor.commit();
		this.authToken = authToken;
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { super.onActivityResult(requestCode,
	 * resultCode, data); switch (requestCode) { case REQUEST_AUTHENTICATE: if
	 * (resultCode == RESULT_OK) { gotAccount(); } else { chooseAccount(); }
	 * break; } }
	 */

	void onAuthToken() {
		new AsyncLoadCalendars(this).execute();
	}

	void onRequestCompleted() {
		received401 = false;
	}

	void handleGoogleException(final IOException e) {
		if (e instanceof GoogleJsonResponseException) {
			GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
			if (exception.getStatusCode() == 401 && !received401) {
				received401 = true;
				accountManager.invalidateAuthToken(authToken);
				authToken = null;
				SharedPreferences.Editor editor2 = settings.edit();
				editor2.remove(PREF_AUTH_TOKEN);
				editor2.commit();
				gotAccount();
			}
		}
		Log.e(TAG, e.getMessage(), e);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				new AlertDialog.Builder(activity).setTitle("Exception")
						.setMessage(e.getMessage())
						.setNeutralButton("ok", null).create().show();
			}
		});
	}

	void refreshCalendars() {
		Collections.sort(calendars);
		setAdapter(new ArrayAdapter<CalendarInfo>(activity,
				android.R.layout.simple_list_item_1, calendars));
	}

	void refreshEvents() {
		Collections.sort(events);
		ToDoListActivity todolistActivity = (ToDoListActivity) activity;
		todolistActivity.getEvents(events);
	}

	public String click(int index) {
		new AsyncLoadEvents(this, index).execute();
		this.index = index;
		return calendars.get(index).id;
	}

	public String insertEvent(ToDo todo) {
		String calenderId = calendars.get(index).id;
		
        Event event = new Event();
        event.setSummary(todo.getTodo());
        
        if(!todo.getCheck())
        {/*
        	EventDateTime eventDateTime = new EventDateTime();
        	eventDateTime.setDateTime(todo.getCalendar());
        	event.setEnd(eventDateTime);*/
        }
        try {
        	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        	event = client.events().insert(calenderId, event).execute();
            EventInfo info = new EventInfo(event.getId(), event.getSummary(), event.getStart(), event.getEnd());
            events.add(info);
            onRequestCompleted();
            return event.getId();
          } catch (IOException e) {
            return null;
          }
	}

	public void deleteEvent(String eventId) {
		if(eventId == null)
			return;
		String calenderId = calendars.get(index).id;
		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.permitNetwork().build());
			client.events().delete(calenderId, eventId).execute();
			events.remove(eventId);
		} catch (IOException e) {
			return;
		} finally {
			onRequestCompleted();
		}
	}
}
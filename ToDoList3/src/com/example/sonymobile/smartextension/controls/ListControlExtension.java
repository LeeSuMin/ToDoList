/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.sonymobile.smartextension.controls;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.sonymobile.smartextension.adapter.ToDo;
import com.example.sonymobile.smartextension.todolist.R;
import com.example.sonymobile.smartextension.todolist.ToDoListExtensionService;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;

/**
 * ListControlExtension displays a scrollable list, based on a string array.
 * Tapping on list items opens a swipable detail view.
 */
public class ListControlExtension extends ManagedControlExtension
{
	protected int mLastKnowPosition = 0;
	private SharedPreferences pref;
	private ArrayList<ToDo> list_todo;
	private String str_id_todo;
	private String str_todo;
	private String str_calendar_todo;
	private String str_check_todo;
	private StringTokenizer st_id_todo;
	private StringTokenizer st_todo;
	private StringTokenizer st_calendar;
	private StringTokenizer st_check;
	private String str_id_done;
	private String str_done;
	private String str_calendar_done;
	private String str_check_done;
	private SharedPreferences.Editor prefEditor;
	private StringBuilder sb_id_todo;
	private StringBuilder sb_todo;
	private StringBuilder sb_calendar;
	private StringBuilder sb_check;
	private int level;
	private int exp;
	
	/**
	 * @see ManagedControlExtension#ManagedControlExtension(Context, String,
	 * 		ControlManagerCostanza, Intent)
	 */
	public ListControlExtension(Context context, String hostAppPackageName,
			ToDoListControl controlManager, Intent intent)
	{
		super(context, hostAppPackageName, controlManager, intent);
		Log.d(ToDoListExtensionService.LOG_TAG, "ListControl constructor");
	}
	
	@Override
	public void onResume()
	{
		Log.d(ToDoListExtensionService.LOG_TAG, "onResume");
		set();
		showLayout(R.layout.todolist_smartwatch2, null);
		sendListCount(R.id.listView_smartwatch2, list_todo.size());
		sendListPosition(R.id.listView_smartwatch2, 0);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		// Position is saved into Control's Intent, possibly to be used later.
		getIntent().putExtra("position", mLastKnowPosition);
	}
	
	@Override
	public void onRequestListItem(final int layoutReference, final int listItemPosition)
	{
		Log.d(ToDoListExtensionService.LOG_TAG,
				"onRequestListItem() - position " + listItemPosition);
		if(layoutReference!=-1 && listItemPosition!=-1 &&
				layoutReference==R.id.listView_smartwatch2)
		{
			ControlListItem item = createControlListItem(listItemPosition);
			if(item != null)
				sendListItem(item);
		}
	}
	
	@Override
	public void onListItemSelected(ControlListItem listItem)
	{
		super.onListItemSelected(listItem);
		// We save the last "selected" position, this is the current visible
		// list item index. The position can later be used on resume
		mLastKnowPosition = listItem.listItemPosition;
	}
	
	@Override
	public void onListItemClick(final ControlListItem listItem, final int clickType,
			final int itemLayoutReference)
	{
		Log.d(ToDoListExtensionService.LOG_TAG, "Item clicked. Position " +
				listItem.listItemPosition + ", itemLayoutReference " +
				itemLayoutReference + ". Type was: " +
				(clickType == Control.Intents.CLICK_TYPE_SHORT ? "SHORT" : "LONG"));
		if(clickType == Control.Intents.CLICK_TYPE_SHORT)
		{
			if(itemLayoutReference == R.id.button_list_smartwatch2)
			{
				Intent intent = new Intent(mContext, ToDoControl.class);
				// Here we pass the item position to the next control. It would also be possible
				// to put some unique item id in the list item and pass listItem.listItemId here.
				int position = listItem.listItemPosition;
				intent.putExtra("position", position);
				intent.putExtra("todo", list_todo.get(position).getTodo());
				intent.putExtra("calendar", list_todo.get(position).getCalendar());
				intent.putExtra("check", list_todo.get(position).getCheck());
				mControlManager.startControl(intent);
			}
			else if(itemLayoutReference == R.id.button_check_smartwatch2)
			{
				check(listItem.listItemPosition);
				showLayout(R.layout.todolist_smartwatch2, null);
				sendListCount(R.id.listView_smartwatch2, list_todo.size());
				sendListPosition(R.id.listView_smartwatch2, 0);
			}
		}
	}
	
	/**
	 * Creates a list item containing an icon, a title and a body text.
	 * 
	 * @param position The position of the item in the list.
	 * @return The list item.
	 */
	protected ControlListItem createControlListItem(int position)
	{
		ControlListItem item = new ControlListItem();
		item.layoutReference = R.id.listView_smartwatch2;
		item.dataXmlLayout = R.layout.list_smartwatch2;
		item.listItemPosition = position;
		// We use position as listItemId. Here we could use some other unique id
		// to reference the list data
		item.listItemId = position;
		
		Bundle iconBundle = new Bundle();
		iconBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE,
				R.id.button_check_smartwatch2);
		
		Bundle headerBundle = new Bundle();
		headerBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE,
				R.id.button_list_smartwatch2);
		headerBundle.putString(Control.Intents.EXTRA_TEXT, list_todo.get(position).getTodo());
		
		item.layoutData = new Bundle[2];
		item.layoutData[0] = iconBundle;
		item.layoutData[1] = headerBundle;
		
		return item;
	}
	
	public void set()
	{
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"id_todo", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"todo", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"calendar_todo", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"check_todo", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"id_done", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"done", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"calendar_done", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"check_done", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"level", Context.MODE_PRIVATE);
		pref = (SharedPreferences)mContext.getSharedPreferences(
				"exp", Context.MODE_PRIVATE);
		
		list_todo = new ArrayList<ToDo>();
		str_id_todo = pref.getString("id_todo", null);
		str_todo = pref.getString("todo", null);
		str_calendar_todo = pref.getString("calendar_todo", null);
		str_check_todo = pref.getString("check_todo", null);
		str_id_done = pref.getString("id_done", null);
		str_done = pref.getString("done", null);
		str_calendar_done = pref.getString("calendar_done", null);
		str_check_done = pref.getString("check_done", null);
		level = pref.getInt("level", 0);
		exp = pref.getInt("exp", 0);
		
		if(str_todo!=null)
		{
			st_id_todo = new StringTokenizer(str_id_todo, ",");
			st_todo = new StringTokenizer(str_todo, ",,");
			st_calendar = new StringTokenizer(str_calendar_todo, ",");
			st_check = new StringTokenizer(str_check_todo, ",");
			while(st_todo.hasMoreTokens())
			{
				ToDo todo = new ToDo();
				todo.setID(st_id_todo.nextToken());
				todo.setTodo(st_todo.nextToken());
				todo.setCalendar(st_calendar.nextToken());
				todo.setCheck(Boolean.parseBoolean(st_check.nextToken()));
				list_todo.add(todo);
			}
		}
	}
	
	public void check(int position)
	{
		ToDo todo = list_todo.get(position);
		
		str_id_done += todo.getID() + ",";
		str_done += todo.getTodo() + ",,";
		str_calendar_done += todo.getCalendar() + ",";
		str_check_done += todo.getCheck() + ",";
		
		list_todo.remove(todo);
		sb_id_todo = new StringBuilder();
		sb_todo = new StringBuilder();
		sb_calendar = new StringBuilder();
		sb_check = new StringBuilder();
		for(int pos=0; pos<list_todo.size(); pos++)
		{
			sb_id_todo.append(list_todo.get(pos).getID()).append(",");
			sb_todo.append(list_todo.get(pos).getTodo()).append(",,");
			sb_calendar.append(list_todo.get(pos).getCalendar()).append(",");
			sb_check.append(list_todo.get(pos).getCheck()).append(",");
		}
		expUp();
		
		prefEditor = pref.edit();
		prefEditor.putString("id_todo", sb_id_todo.toString());
		prefEditor.putString("todo", sb_todo.toString());
		prefEditor.putString("calendar_todo", sb_calendar.toString());
		prefEditor.putString("check_todo", sb_check.toString());
		prefEditor.putString("id_done", str_id_done);
		prefEditor.putString("done", str_done);
		prefEditor.putString("calendar_done", str_calendar_done);
		prefEditor.putString("check_done", str_check_done);
		prefEditor.putInt("level", level);
		prefEditor.putInt("exp", exp);
		prefEditor.commit();
		
		Intent intent = new Intent(
				"com.example.sonymobile.smartextension.todolist.ACTION_UPDATE_ACTIVITY");
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		try {
			mContext.stopService(intent);
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(ToDoListExtensionService.LOG_TAG, e.toString());
		}
	}
	
	public void expUp()
	{
		if(level == 5)
			return;
		
		exp += 5;
		
		if(exp == level*100)
		{
			exp = 0;
			level++;
		}
	}
}
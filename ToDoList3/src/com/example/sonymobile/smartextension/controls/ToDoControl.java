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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sonymobile.smartextension.todolist.R;
import com.example.sonymobile.smartextension.todolist.ToDoListExtensionService;

/**
 * GalleryTestControl displays a swipeable gallery, based on a string array.
 */
public class ToDoControl extends ManagedControlExtension
{
	private Intent intent;
	private String todo;
	private String calendar;
	private boolean check;
	private String time;
	
	/**
	 * @see ManagedControlExtension#ManagedControlExtension(Context, String,
	 * 		ControlManagerCostanza, Intent)
	 */
	public ToDoControl(Context context, String hostAppPackageName,
			ToDoListControl controlManager, Intent intent)
	{
		super(context, hostAppPackageName, controlManager, intent);
	}
	
	@Override
	public void onResume()
	{
		Log.d(ToDoListExtensionService.LOG_TAG, "onResume");
		
		intent = getIntent();
		todo = intent.getStringExtra("todo").toString();
		calendar = intent.getStringExtra("calendar").toString();
		check = intent.getBooleanExtra("check", false);
		if(check)
			time = "기간 없음";
		else
			time = calendar;
		
		showLayout(R.layout.todo_smartwatch2, null);
		sendText(R.id.textView_todo_smartwatch2, todo);
		sendText(R.id.textView_time_smartwatch2, time);
	}
}
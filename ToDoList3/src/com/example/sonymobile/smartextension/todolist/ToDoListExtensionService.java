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

package com.example.sonymobile.smartextension.todolist;

import android.os.Handler;
import android.util.Log;

import com.example.sonymobile.smartextension.controls.ToDoListControl;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The ToDoListService handles registration and keeps track of all controls
 * on all accessories.
 */
public class ToDoListExtensionService extends ExtensionService
{
	public static final String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_TODOLIST";
	public static final String LOG_TAG = "ToDoList";
	
	public ToDoListExtensionService()
	{
		super();
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(ToDoListExtensionService.LOG_TAG, "ToDoListExtensionService: onCreate");
	}
	
	@Override
	protected RegistrationInformation getRegistrationInformation()
	{
		return new ToDoListRegistrationInformation(this);
	}
	
	@Override
	protected boolean keepRunningWhenConnected()
	{
		return false;
	}
	
	@Override
	public ToDoListControl createControlExtension(String hostAppPackageName)
	{
		// First we check if the API level and screen size we require is
		// supported by the host application.
		boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
				this, hostAppPackageName);
		if (advancedFeaturesSupported)
			// If the required properties are supported, we return a control
			// extension object.
			return new ToDoListControl(hostAppPackageName, this, new Handler());
		else
			throw new IllegalArgumentException("No control for: " + hostAppPackageName);
	}
}
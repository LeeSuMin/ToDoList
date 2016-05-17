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

import com.google.api.services.calendar.model.EventDateTime;

/**
 * Class that holds information about a Calendar.
 * 
 * @author Ravi Mistry
 */
public class EventInfo implements Comparable<EventInfo> {
  public String id;
  public String summary;
  public EventDateTime startDateTime;
  public EventDateTime endDateTime;

  EventInfo(String id, String summary, EventDateTime startDateTime, EventDateTime endDateTime) {
    this.id = id;
    this.summary = summary;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  @Override
  public String toString() {
    return summary;
  }

  public int compareTo(EventInfo other) {
    long value1;
    long value2;
    try {
      value1 = endDateTime.getDateTime().getValue();
      value2 = other.endDateTime.getDateTime().getValue();
    } catch (Exception e) {
      return 1;
    }

    int diff = (int) (value1 - value2);
    if (diff > 1)
      diff = 1;
    else if (diff < -1) diff = -1;
    return (int) (value1 - value2);
  }
}

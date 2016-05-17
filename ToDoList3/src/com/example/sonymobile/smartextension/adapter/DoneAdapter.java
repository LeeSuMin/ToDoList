package com.example.sonymobile.smartextension.adapter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.example.sonymobile.smartextension.todolist.Avatar;
import com.example.sonymobile.smartextension.todolist.R;
import com.google.api.services.samples.calendar.android.CalendarSample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoneAdapter extends BaseAdapter
{
	public final static int VIEW = 0;
	public final static int DELETE = 1;
	private Context context;
	private int layoutRes;
	private ArrayList<ToDo> list;
	private LayoutInflater inflater;
	private ToDoAdapter todoAdapter;
	private Button button_check;
	private Button button_list;
	private Button button_delete;
	private SharedPreferences pref;
	private SharedPreferences.Editor prefEditor;
	private String str_id;
	private String str_done;
	private String str_calendar;
	private String str_check;
	private StringTokenizer st_id;
	private StringTokenizer st_done;
	private StringTokenizer st_calendar;
	private StringTokenizer st_check;
	private StringBuilder sb_id;
	private StringBuilder sb_done;
	private StringBuilder sb_calendar;
	private StringBuilder sb_check;
	private LinearLayout layout;
	private TextView textView_view_todo;
	private TextView textView_view_time;
	Avatar avatar;
	public CalendarSample calendarSample;
	
	public DoneAdapter(Context context, int layoutRes,
			SharedPreferences pref)
	{
		this.context = context;
		this.layoutRes = layoutRes;
		this.pref = pref;
		
		list = new ArrayList<ToDo>();
		str_id = pref.getString("id_done", null);
		str_done = pref.getString("done", null);
		str_calendar = pref.getString("calendar_done", null);
		str_check = pref.getString("check_done", null);
		
		if(str_done!=null)
		{
			st_id = new StringTokenizer(str_id, ",");
			st_done = new StringTokenizer(str_done, ",,");
			st_calendar = new StringTokenizer(str_calendar, ",");
			st_check = new StringTokenizer(str_check, ",");
			while(st_done.hasMoreTokens())
			{
				ToDo todo = new ToDo();
				todo.setID(st_id.nextToken());
				todo.setTodo(st_done.nextToken());
				todo.setCalendar(st_calendar.nextToken());
				todo.setCheck(Boolean.parseBoolean(st_check.nextToken()));
				list.add(todo);
			}
		}
		
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
			convertView = inflater.inflate(layoutRes, parent, false);
		
		button_check = (Button)convertView.findViewById(R.id.button_check_done);
		button_list = (Button)convertView.findViewById(R.id.button_list_done);
		button_delete = (Button)convertView.findViewById(R.id.button_delete_done);
		
		button_list.setText(list.get(position).getTodo());
		button_list.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				createAlert(VIEW, position);
			}
		});
		
		button_delete.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				createAlert(DELETE, position);
			}
		});
		
		button_check.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ToDo todo = list.get(position);
				todoAdapter.add(todo);
				list.remove(todo);
				avatar = todoAdapter.getAvatar();
				if(avatar.level!=5 && avatar.exp!=0)
					avatar.exp -= 5;
				todoAdapter.avatar.setExp(avatar.exp);
				updateData();
			}
		});
		
		return convertView;
	}
	
	public void add(ToDo todo)
	{
		list.add(todo);
		updateData();
	}
	
	public void linkAdapter(ToDoAdapter adapter)
	{
		todoAdapter = adapter;
	}
	
	public void createAlert(int id, final int position)
	{
		switch (id)
		{
		case VIEW:
			layout = (LinearLayout)View.inflate(context, R.layout.todolist_view, null);
			textView_view_todo = (TextView)layout.findViewById(R.id.textView_view_todo);
			textView_view_time = (TextView)layout.findViewById(R.id.textView_view_time);
			
			textView_view_todo.setText(list.get(position).getTodo());
			if(!list.get(position).getCheck())
			{
				st_calendar = new StringTokenizer(list.get(position).getCalendar(), ".");
				String cal = st_calendar.nextToken() + "-" + st_calendar.nextToken() + "-" +
						st_calendar.nextToken() + " (" + st_calendar.nextToken() + ") " +
						st_calendar.nextToken() + " " + st_calendar.nextToken() + ":" +
						st_calendar.nextToken();
				textView_view_time.setText(cal);
			}
			else
				textView_view_time.setText("기간 없음");
			
			new AlertDialog.Builder(context)
				.setView(layout)
				.setTitle("한 일 보기")
				.setPositiveButton("OK", null).show();
			break;
		case DELETE:
			new AlertDialog.Builder(context)
			.setTitle("한 일 삭제")
			.setMessage("정말 삭제하시겠습니까?")
			.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					ToDo todo = list.get(position);
					list.remove(todo);
					calendarSample.deleteEvent(todo.getID());
					updateData();
				}
			})
			.setNegativeButton("CANCEL", null).show();
		default:
			break;
		}
	}
	
	public void updateData()
	{
		notifyDataSetChanged();
		sb_id = new StringBuilder();
		sb_done = new StringBuilder();
		sb_calendar = new StringBuilder();
		sb_check = new StringBuilder();
		for(int position=0; position<getCount(); position++)
		{
			sb_done.append(list.get(position).getID()).append(",");
			sb_done.append(list.get(position).getTodo()).append(",,");
			sb_calendar.append(list.get(position).getCalendar()).append(",");
			sb_check.append(list.get(position).getCheck()).append(",");
		}
		
		avatar = todoAdapter.getAvatar();
		prefEditor = pref.edit();
		prefEditor.putString("id_done", sb_id.toString());
		prefEditor.putString("done", sb_done.toString());
		prefEditor.putString("calendar_done", sb_calendar.toString());
		prefEditor.putString("check_done", sb_check.toString());
		prefEditor.putInt("exp", avatar.exp);
		prefEditor.commit();
	}
}
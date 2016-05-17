package com.example.sonymobile.smartextension.todolist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AvatarActivity extends Activity
{
	final static String Avatar1 = "알";
	final static String Avatar2 = "파이리";
	final static String Avatar3 = "리자드";
	final static String Avatar4 = "리자몽";
	final static String Avatar5 = "메가리자몽";
	private Intent intent;
	private Avatar avatar;
	private ImageView imageView_avatar;
	private TextView textView_avatar;
	private ProgressBar progressBar;
	private TextView textView_exp;
	private TextView textView_next_avatar;
	private ImageView imageView_next_avatar;
	private LayoutParams currenctParams;
	private LayoutParams nextParams;
	private String currentAvatar;
	private String nextAvatar;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		intent = getIntent();
		avatar = new Avatar();
		avatar.level = intent.getIntExtra("level", 1);
		avatar.exp = intent.getIntExtra("exp", 0);
		
		imageView_avatar = (ImageView)findViewById(R.id.imageView_avatar);
		textView_avatar = (TextView) findViewById(R.id.textView_avatar);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textView_exp = (TextView) findViewById(R.id.textView_exp);
		textView_next_avatar = (TextView) findViewById(R.id.textView_next_avatar);
		imageView_next_avatar = (ImageView)findViewById(R.id.imageView_next_avatar);
		
		setAvatar(avatar.level);
	}
	
	public void setAvatar(int level)
	{
		currenctParams = (LayoutParams) imageView_avatar.getLayoutParams();
		nextParams = (LayoutParams) imageView_next_avatar.getLayoutParams();
		switch (level) {
		case 1:
			currentAvatar = Avatar1;
			nextAvatar = Avatar2;
			imageView_avatar.setImageResource(R.drawable.avatar1);
			currenctParams.width = 300;
			currenctParams.height = 300;
			imageView_next_avatar.setImageResource(R.drawable.avatar2);
			nextParams.width = 200;
			nextParams.height = 200;
			break;
		case 2:
			currentAvatar = Avatar2;
			nextAvatar = Avatar3;
			imageView_avatar.setImageResource(R.drawable.avatar2);
			currenctParams.width = 250;
			currenctParams.height = 250;
			imageView_next_avatar.setImageResource(R.drawable.avatar3);
			nextParams.width = 250;
			nextParams.height = 250;
			break;
		case 3:
			currentAvatar = Avatar3;
			nextAvatar = Avatar4;
			imageView_avatar.setImageResource(R.drawable.avatar3);
			currenctParams.width = 250;
			currenctParams.height = 250;
			imageView_next_avatar.setImageResource(R.drawable.avatar4);
			nextParams.width = 300;
			nextParams.height = 300;
			break;
		case 4:
			currentAvatar = Avatar4;
			nextAvatar = Avatar5;
			imageView_avatar.setImageResource(R.drawable.avatar4);
			currenctParams.width = 300;
			currenctParams.height = 300;
			imageView_next_avatar.setImageResource(R.drawable.avatar5);
			nextParams.width = 300;
			nextParams.height = 300;
			break;
		case 5:
			currentAvatar = Avatar5;
			imageView_avatar.setImageResource(R.drawable.avatar5);
			currenctParams.width = 300;
			currenctParams.height = 300;
			textView_next_avatar.setVisibility(android.view.View.INVISIBLE);
			imageView_next_avatar.setVisibility(android.view.View.INVISIBLE);
			avatar.exp = 500;
			break;
		default:
			break;
		}
		textView_avatar.setText(currentAvatar + " (Lv." + level + ")");
		imageView_avatar.setLayoutParams(currenctParams);
		progressBar.setMax(avatar.level * 100);
		progressBar.setProgress(avatar.exp);
		textView_exp.setText(avatar.exp + " / " + level * 100);
		textView_next_avatar.setText("다음 진화 : " + nextAvatar);
		imageView_next_avatar.setLayoutParams(nextParams);
	}
}
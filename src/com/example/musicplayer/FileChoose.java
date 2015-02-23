package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileChoose extends ListActivity{

	List<File> files;
	private final String TAG = "FileChoose Activity";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filechoose);

//		if (Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
//            Toast.makeText(FileChoose.this,
//                    Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
//        }
//		
		files = getMusicList(Environment.getExternalStorageDirectory());

		setListAdapter(new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, files));

	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		//create reply to intent here
		Intent returnIntent = new Intent();
		returnIntent.putExtra("FilePath", files.get(position).getAbsolutePath());
		this.setResult(RESULT_OK, returnIntent);
		finish();
	}



	private List<File> getMusicList(File parentDir){
		ArrayList<File> ret = new ArrayList<File>(); //value to be returned
		
		File[] files =  parentDir.listFiles();
		if(files != null){
			for(File curr: files){
				//Log.d(TAG,"File Name: " + curr.getAbsolutePath());
				if(curr.isDirectory()){
					ret.addAll(getMusicList(curr));
				}else if(curr.getName().endsWith(".wav")){
					ret.add(curr);
				}
			}
			
		}else{
			Log.d(TAG,"Is Null");
		}
		return ret;
	}
}


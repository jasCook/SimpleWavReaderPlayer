package com.example.musicplayer;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void chooseFile(View v){
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("file/*");
//
//		try{
//			startActivityForResult(intent, 152);
//		}catch(android.content.ActivityNotFoundException ex){
//			Toast.makeText(this, "Please install a File Manager.", 
//					Toast.LENGTH_SHORT).show();
//		}
		
		Intent intent = new Intent(this, FileChoose.class);
		
		
		
		try{
			startActivityForResult(intent, 152);
		}catch(android.content.ActivityNotFoundException ex){
			Toast.makeText(this, "Class not found", 
					Toast.LENGTH_SHORT).show();
		}
		
		

	}

	private String path;
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 152){
			if (resultCode == RESULT_OK){

//				Uri uri = data.getData();

				// Get the path
//				path = getPath(this, uri);
				
				path = (String) data.getExtras().get("FilePath");

				findViewById(R.id.choose_file).setEnabled(false);
				findViewById(R.id.play).setEnabled(true);
				findViewById(R.id.stop).setEnabled(false);
			}
		}


		super.onActivityResult(requestCode, resultCode, data);
	}

	
	public void playMusic(View v){
		FilePlayer.playMusic(path);
	}
	
	public void pauseMusic(View v){
		FilePlayer.pauseMusic();
	}
	
		
	protected String getPath(Context con, Uri uri){

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = con.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}

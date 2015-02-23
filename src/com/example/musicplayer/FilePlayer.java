package com.example.musicplayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Toast;

public class FilePlayer {
	private static File fp;
	private static DataInputStream in;
	private static WavReader musicInfo;
	private static AudioTrack at;
	private static String path;
	private static List<byte[]> musicBuffer;   //needed for udp connection
	private static final String TAG = "File Player";
	private static int BUFSIZE; //needs to be set in code

	protected static void playMusic(String p){
		if (path == null){
			path = p;
			musicBuffer = new LinkedList<byte[]>(); 
		}
		 
		
		fp = new File(p);
		
		try{
			//read wav file
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(fp)));
			musicInfo = new WavReader(in);
			BUFSIZE = AudioTrack.getMinBufferSize((int) musicInfo.sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT); //best to use AudioFormat.CHANNEL_OUT_STEREO or else
													// it may throw ERROR_BAD_VALUE
			Log.d(TAG, "Buff size: " + BUFSIZE);
			
			if(BUFSIZE == AudioTrack.ERROR){
				Log.e(TAG, "Cannot determine buffer size");
				System.exit(AudioTrack.ERROR);
			}
			

			if(BUFSIZE == AudioTrack.ERROR_BAD_VALUE){
				Log.e(TAG, "Invalid parameter passed to AudioTrack.getMinBufferSize");
				System.exit(AudioTrack.ERROR_BAD_VALUE);
			}
			
			
			
			at = new AudioTrack(AudioManager.STREAM_MUSIC, (int) musicInfo.sampleRate,
					AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,  BUFSIZE, AudioTrack.MODE_STREAM);
			
			
			//read actual music in wav file
			int bCount = 0;
			byte[] bytes = new byte[BUFSIZE];
			at.play();
			while((bCount = in.read(bytes)) != -1){
				
//				//pads the end of the array with 0 in the case of bCount < bytes.length
//				for(int i = bCount; i < bytes.length; i++){
//					bytes[i] = 0;
//				}
				
				at.write(bytes, 0, bCount);				
				Log.d(TAG, "Music Out: " + bCount);
			}
			
			
			
		}catch(FileNotFoundException e){
			Log.e("File not found", TAG);
		}catch(UnsupportedOperationException f){
			Log.e("Wrong wav format", TAG);
		}catch (IOException g){
			Log.e("IOException", TAG);
			Log.e(g.getMessage(),TAG);
		}finally{
			if(at != null){
				//at.flush();
				at.stop();
				at.release();
			}
			
			try {
				if (in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		

		}	
		
		
	}
	
	protected static void pauseMusic(){
		//TODO implement this
	}
	
	
}

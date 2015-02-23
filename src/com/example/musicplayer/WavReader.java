package com.example.musicplayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WavReader {


	private final int RIFF = 0x52494646;
	private final int RIFFTYPE_WAVE = 0x57415645;
	private final int fmt  = 0x666D7420;
	private final int data = 0x64617461; 

	// in main chunk
	protected long chunkDataSize;

	//in format chunk
	protected long fmtChunkSize;
	protected int numChannels;
	protected long sampleRate;
	protected long avgBytesPerSec;
	protected long blockAlign;
	protected long significantBitsPerSample;
	//Extra format bytes not used

	long dataChunkSize;
	
	public WavReader(DataInputStream in){
		processWav(in);

	}

	/**
	 * processes wav file based on structure listed on http://www.sonicspot.com/guide/wavefiles.html
	 * 
	 * @param file wav file to be processed
	 */
	private void processWav(DataInputStream streamIn){


		long readin; //data that is read in from the byte array and converted to an int
		DataInputStream in = null;
		try {
			in = streamIn;
			byte[] word2 = new byte[2];
			byte[] word4 = new byte[4];


			//get RIFF (chunk ID)
			in.read(word4);
			readin = getBigEndian(word4);
			if (readin != RIFF){
				throw new UnsupportedOperationException("Not a valid RIFF file");
			}

			//get chunk size
			in.read(word4);
			chunkDataSize = getLittleEndian(word4);

			//get format (WAVE)
			in.read(word4);
			readin = getBigEndian(word4);
			if (readin != RIFFTYPE_WAVE){
				throw new UnsupportedOperationException("Not a valid RIFF Type");
			}

			readFormat(in, word2, word4);
			checkForDataChunk(in, word4);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
//			try {
//				if (in != null){
//					in.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		}

	}

	/**
	 * reads in the format chunk
	 * @param in datainputstream associated with the file
	 * @param word2 a byte array of size 2
	 * @param word4 a byte array of size 4
	 * @throws IOException
	 */
	private void readFormat(DataInputStream in, byte[] word2, byte[] word4) throws IOException{
		in.read(word4); //read format

		if (getBigEndian(word4) != fmt){
			throw new UnsupportedOperationException("\"fmt \" expected!");
		}

		in.read(word4); //read chunk data size
		fmtChunkSize = getLittleEndian(word4);

		in.read(word2); //compression code. 1 for PCM, 0 for unknown

		long compCode = getLittleEndian(word2);
		if (compCode != 0 && compCode != 1){
			System.out.print(compCode);
			throw new UnsupportedOperationException("Compression not supported");
		}

		in.read(word2);
		numChannels = (int) getLittleEndian(word2);		

		in.read(word4);
		sampleRate = getLittleEndian(word4);

		in.read(word4);
		avgBytesPerSec = getLittleEndian(word4);
		//display.append("Average Bytes per Second Read in: " + avgBytesPerSec + "\n");

		in.read(word2);
		blockAlign = getLittleEndian(word2);

//		display.append("Block Align Read in: " + blockAlign +
//				"\nAverage Bytes Per Second Calculated: " + (blockAlign * sampleRate)
//				+ "\n");

		in.read(word2);
		significantBitsPerSample = getLittleEndian(word2);
//		display.append("Significant Bits Per Sample: " + significantBitsPerSample + "\n");

		if (significantBitsPerSample % 8 != 0){
			throw new UnsupportedOperationException("Not byte aligned!");
		}

		blockAlign = significantBitsPerSample / 8 * numChannels;
		avgBytesPerSec = sampleRate * blockAlign;

//		display.append("Calculated block align: " + 
//				blockAlign + " \nCalculated Average Bytes Per Sec: "
//				+ avgBytesPerSec + "\n");
	}
	
	
	public void checkForDataChunk(DataInputStream in, byte[] word4) throws IOException{
		in.read(word4);
		if (getBigEndian(word4) != data){
			throw new UnsupportedOperationException("Data chunk expected");

		}
	}
	

	/** 
	 * gets big endian data and returns it as a java integer
	 * @param arr byte array to be converted
	 * @return integer value of byte array
	 */
	private long getBigEndian(byte[] arr){
		long retVal=0;

		for (int i=0; i < arr.length; i++){
			retVal = (retVal <<  8) | ( ((long) arr[i]) & 255); //accounting for widening conversion
		}

		return retVal;
	}

	/** gets little endian data and returns it as a java integer
	 * @param arr byte array to be converted
	 * @return integer value of byte array, long is used since java does
	 *         not support unsigned integers
	 */
	private long getLittleEndian(byte[] arr){
		long retVal=0;
		long bitShifts;
		//		display.append("---------------\n")
		for (int i=0; i < arr.length; i++){
			bitShifts = 0L | arr[i];

			//to account for widening conversion
			retVal = (retVal  | (bitShifts << (i * 8))) & ( ipow(2, (i + 1) * 8) - 1);

		}

		return retVal;
	}
	
	private int ipow(int base, int exp){
		int result = 1;
		while (exp != 0)
		{
			if ((exp & 1) == 1)
				result *= base;
			exp >>= 1;
			base *= base;
		}

		return result;
	}



}

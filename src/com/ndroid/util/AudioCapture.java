package com.ndroid.util;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.media.MediaRecorder;

public class AudioCapture {
	private final MediaRecorder mr;
	
	public static final int FORMAT_AAC = MediaRecorder.OutputFormat.AAC_ADTS;
	public static final int FORMAT_AMR_NARROWBAND = MediaRecorder.OutputFormat.AMR_NB;
	public static final int FORMAT_AMR_WIDEBAND = MediaRecorder.OutputFormat.AMR_WB;
	public static final int FORMAT_3GPP = MediaRecorder.OutputFormat.THREE_GPP;
	public static final int FORMAT_MP4 = MediaRecorder.OutputFormat.MPEG_4;
	
	private int audioSource;
	private int audioEncoder;
	
	public AudioCapture() {
		mr = new MediaRecorder();
		
		audioSource = MediaRecorder.AudioSource.MIC;
		audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
		
		new WeakReference<AudioCapture>(this);
	}
	
	public void setAudioSource(int audioSource) {
		this.audioSource = audioSource;
	}
	
	public void setAudioEncoder(int audioEncoder) {
		this.audioEncoder = audioEncoder;
	}
	
	public void startRecording(int fileFormat, String filePath) throws IllegalStateException, IOException {
		mr.setAudioSource(audioSource);
		mr.setOutputFormat(fileFormat);
		mr.setAudioEncoder(audioEncoder);
		mr.setOutputFile(filePath);
		
		mr.prepare();
		mr.start();
	}
	
	public void stopRecording() {
		mr.stop();
		mr.reset();
	}
	
	public void close() {
		mr.release();
	}
}

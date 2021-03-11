package com.ksyun.ks3.services.handler;

import android.annotation.SuppressLint;
import android.util.Log;

import cz.msebera.android.httpclient.Header;

import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.transfer.RequestProgressListener;

public abstract class PutObjectResponseHandler extends Ks3HttpResponceHandler implements RequestProgressListener{

	public abstract void onTaskFailure(int statesCode, Ks3Error error, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onTaskSuccess(int statesCode, Header[] responceHeaders);
	
	public abstract void onTaskStart();
	
	public abstract void onTaskFinish();
	
	public abstract void onTaskCancel();

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		onTaskSuccess(statesCode, responceHeaders);
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		onTaskFailure(statesCode,error, responceHeaders, response == null? "":new String(response), throwable);
	}

	@Override
	public final void onStart() {
		onTaskStart();
	}

	@Override
	public final void onFinish() {
		onTaskFinish();
	}

	@Override
	public final void onCancel() {
		onTaskCancel();
	}
	
	@Override
	public final void onProgress(long bytesWritten, long totalSize) {}

	@SuppressLint("LongLogTag")
	@Override
	protected void finalize() throws Throwable {
		Log.d("PutObjectResponseHandler", "PutObjectResponseHandler finalize:"+this);
		super.finalize();
	}
	
	
}

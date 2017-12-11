package com.combintech.baojili.printer;

import android.os.Debug;
import android.util.Log;

import com.combintech.baojili.BuildConfig;

public class Logger {

	private static final String TAG = "LWPrint SDK Sample";

	public static final void d(String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static final void d(String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, msg, tr);
		}
	}

	public static final void d(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static final void d(String tag, String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg, tr);
		}
	}

	public static final void e(String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, msg);
		}
	}

	public static final void e(String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, msg, tr);
		}
	}

	public static final void e(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static final void e(String tag, String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static final void i(String msg) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, msg);
		}
	}

	public static final void i(String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, msg, tr);
		}
	}

	public static final void i(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static final void i(String tag, String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.i(tag, msg, tr);
		}
	}

	public static final void v(String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, msg);
		}
	}

	public static final void v(String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, msg, tr);
		}
	}

	public static final void v(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static final void v(String tag, String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.v(tag, msg, tr);
		}
	}

	public static final void w(String msg) {
		if (BuildConfig.DEBUG) {
			Log.w(TAG, msg);
		}
	}

	public static final void w(String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.w(TAG, msg, tr);
		}
	}

	public static final void w(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static final void w(String tag, String msg, Throwable tr) {
		if (BuildConfig.DEBUG) {
			Log.w(tag, msg, tr);
		}
	}

	public static final void heap() {
		heap(TAG);
	}

	public static final void heap(String tag) {
		if (BuildConfig.DEBUG) {
			String msg = "heap : Free="
					+ Long.toString(Debug.getNativeHeapFreeSize() / 1024)
					+ "kb" + ", Allocated="
					+ Long.toString(Debug.getNativeHeapAllocatedSize() / 1024)
					+ "kb" + ", Size="
					+ Long.toString(Debug.getNativeHeapSize() / 1024) + "kb";

			Log.v(tag, msg);
		}
	}

}

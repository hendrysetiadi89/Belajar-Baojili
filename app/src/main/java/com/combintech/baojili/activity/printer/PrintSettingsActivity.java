package com.combintech.baojili.activity.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.combintech.baojili.R;
import com.combintech.baojili.apphelper.LWPrintSampleUtil;
import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintTapeCut;

public class PrintSettingsActivity extends Activity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener {

	private EditText _editCopies;
	private RadioGroup _radioTapeCut;
	private ToggleButton _toggleHalfCut;
	private ToggleButton _toggleLowSpeed;

	private EditText _editDensity;

	private int mTapeCutMode = LWPrintTapeCut.EachLabel;

	private int _copies;
	private int _density;

	android.os.Handler handler = new android.os.Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_settings);

		Button buttonDone = (Button) findViewById(R.id.print_settings_ok_button);
		buttonDone.setOnClickListener(this);

		_editCopies = (EditText) findViewById(R.id.copies_edit_text);
		_editCopies.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// determine EnterKey are pressed
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					// close the soft keyboard
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					// input check
					if (checkInputCopies() == false) {
						showCopiesAlertDialog("Error", "The range of a copy is to 1 to 99.");
					}
					return true;
				}
				return false;
			}
		});
		_radioTapeCut = (RadioGroup) findViewById(R.id.tape_cut_radio_group);
		_radioTapeCut.setOnCheckedChangeListener(this);
		_toggleHalfCut = (ToggleButton) findViewById(R.id.half_cut_toggle_button);
		_toggleLowSpeed = (ToggleButton) findViewById(R.id.low_speed_toggle_button);
		_editDensity = (EditText) findViewById(R.id.density_edit_text);
		
		_editDensity.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// determine EnterKey are pressed
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					// close the soft keyboard
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					// input check
					if (checkInputDensity() == false) {
						showDensityAlertDialog("Error", "The range of a density is to -5 to 5.");
					}
					return true;
				}
				return false;
			}
		});
		getValues();

		findViewById(R.id.vg_root).requestFocus();
	}

	private boolean checkInputCopies() {
		boolean result = true;

		// input check
		boolean bError = false;
		try {
			int value = Integer.parseInt(_editCopies.getText().toString());
			if (value < 1 || value >= 100) {
				// error
				bError = true;
			}
		} catch (Exception e) {
			// error
			bError = true;
		}
		if (bError) {
			result = false;
		}

		return result;
	}

	private boolean checkInputDensity() {
		boolean result = true;

		// input check
		boolean bError = false;
		try {
			int value = Integer.parseInt(_editDensity.getText().toString());
			if (value < -5 || value > 5) {
				// error
				bError = true;
			}
		} catch (Exception e) {
			// error
			bError = true;
		}
		if (bError) {
			result = false;
		}

		return result;
	}

	private void showCopiesAlertDialog(final String title, final String message) {
		handler.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						PrintSettingsActivity.this);
				alert.setTitle(title);
				alert.setMessage(message);
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								_editCopies.setText(String.valueOf(_copies));
							}
						});
				alert.create().show();
			}
		}, 1);
	}

	private void showDensityAlertDialog(final String title, final String message) {
		handler.postDelayed(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						PrintSettingsActivity.this);
				alert.setTitle(title);
				alert.setMessage(message);
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								_editDensity.setText(String.valueOf(_density));
							}
						});
				alert.create().show();
			}
		}, 1);
	}

	private void getValues() {
		SharedPreferences pref = getSharedPreferences(
				LWPrintSampleUtil.PREFERENCE_FILE_NAME, MODE_PRIVATE);

		_copies = pref.getInt(LWPrintParameterKey.Copies,
				LWPrintSampleUtil.DEFAULT_COPIES_SETTING);

		int tapeCutMode = pref.getInt(LWPrintParameterKey.TapeCut,
				LWPrintSampleUtil.DEFAULT_TAPE_CUT_SETTING);
		mTapeCutMode = tapeCutMode;

		boolean halfCutSetting = pref.getBoolean(LWPrintParameterKey.HalfCut,
				LWPrintSampleUtil.DEFAULT_HALF_CUT_SETTING);

		boolean lowSpeedSetting = pref.getBoolean(
				LWPrintParameterKey.PrintSpeed, LWPrintSampleUtil.DEFAULT_LOW_SPEED_SETTING);

		_density = pref.getInt(LWPrintParameterKey.Density,
				LWPrintSampleUtil.DEFAULT_DENSITY_SETTING);

		_editCopies.setText(String.valueOf(_copies));
		RadioButton radioTapeCut = null;
		switch (mTapeCutMode) {
		case 0:
			radioTapeCut = (RadioButton) findViewById(R.id.each_radio);
			break;
		case 1:
			radioTapeCut = (RadioButton) findViewById(R.id.after_radio);
			break;
		case 2:
			radioTapeCut = (RadioButton) findViewById(R.id.none_radio);
			break;
		default:
			radioTapeCut = (RadioButton) findViewById(R.id.each_radio);
			break;
		}
		radioTapeCut.setChecked(true);
		_toggleHalfCut.setChecked(halfCutSetting);
		_toggleLowSpeed.setChecked(lowSpeedSetting);
		_editDensity.setText(String.valueOf(_density));
	}

	private void setValues() {
		SharedPreferences pref = getSharedPreferences(
				LWPrintSampleUtil.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		int copies = LWPrintSampleUtil.DEFAULT_COPIES_SETTING;
		try {
			copies = Integer.parseInt(_editCopies.getText().toString());
		} catch (NumberFormatException e) {
		}
		editor.putInt(LWPrintParameterKey.Copies, copies);
		editor.putInt(LWPrintParameterKey.TapeCut, mTapeCutMode);
		editor.putBoolean(LWPrintParameterKey.HalfCut,
				_toggleHalfCut.isChecked());
		editor.putBoolean(LWPrintParameterKey.PrintSpeed,
				_toggleLowSpeed.isChecked());
		int density = LWPrintSampleUtil.DEFAULT_DENSITY_SETTING;
		try {
			density = Integer.parseInt(_editDensity.getText().toString());
		} catch (NumberFormatException e) {
		}
		editor.putInt(LWPrintParameterKey.Density, density);

		editor.commit();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.print_settings_ok_button) {
			if (checkInputCopies() == false) {
				showCopiesAlertDialog("Error", "The range of a copy is to 1 to 99.");
				return;
			}
			if (checkInputDensity() == false) {
				showDensityAlertDialog("Error", "The range of a density is to -5 to 5.");
				return;
			}

			setValues();
			Intent intent = getIntent();
			int copies = LWPrintSampleUtil.DEFAULT_COPIES_SETTING;
			try {
				copies = Integer.parseInt(_editCopies.getText().toString());
			} catch (NumberFormatException e) {
			}
			intent.putExtra(LWPrintParameterKey.Copies, copies);
			intent.putExtra(LWPrintParameterKey.TapeCut, mTapeCutMode);
			intent.putExtra(LWPrintParameterKey.HalfCut,
					_toggleHalfCut.isChecked());
			intent.putExtra(LWPrintParameterKey.PrintSpeed,
					_toggleLowSpeed.isChecked());
			int density = LWPrintSampleUtil.DEFAULT_DENSITY_SETTING;
			try {
				density = Integer.parseInt(_editDensity.getText().toString());
			} catch (NumberFormatException e) {
			}
			intent.putExtra(LWPrintParameterKey.Density, density);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg0 == _radioTapeCut) {
			switch (arg1) {
			case R.id.each_radio:
				mTapeCutMode = LWPrintTapeCut.EachLabel;
				break;
			case R.id.after_radio:
				mTapeCutMode = LWPrintTapeCut.AfterJob;
				break;
			case R.id.none_radio:
				mTapeCutMode = LWPrintTapeCut.CutNotCut;
				break;
			default:
				break;
			}
		}
	}

}

package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.combintech.baojili.application.BaseApplication;
import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintTapeCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LWPrintSampleUtil {

	public static final String PREFERENCE_FILE_NAME = "lwprintsample";
	public static final boolean SAVE_VALUES_MODE = false;

	public static final int DEFAULT_COPIES_SETTING = 1;
	public static final int DEFAULT_TAPE_CUT_SETTING = LWPrintTapeCut.EachLabel;
	public static final boolean DEFAULT_HALF_CUT_SETTING = true;
	public static final boolean DEFAULT_LOW_SPEED_SETTING = false;
	public static final int DEFAULT_DENSITY_SETTING = 0;

	private static SharedPreferences instance;

	private static SharedPreferences getPrefInstance(){
		if (instance == null) {
			instance = BaseApplication.getContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		}
		return instance;
	}

	public static String getSelectedSettingsString(){
		SharedPreferences pref = getPrefInstance();
		int copies = pref.getInt(LWPrintParameterKey.Copies, DEFAULT_COPIES_SETTING);
		int tapeCutMode = pref.getInt(LWPrintParameterKey.TapeCut, DEFAULT_TAPE_CUT_SETTING);
		boolean isHalfCut = pref.getBoolean(LWPrintParameterKey.HalfCut, DEFAULT_HALF_CUT_SETTING);
		boolean isLowSpeed = pref.getBoolean(LWPrintParameterKey.PrintSpeed, DEFAULT_LOW_SPEED_SETTING);
		int density = pref.getInt(LWPrintParameterKey.Density, DEFAULT_DENSITY_SETTING);
		return copies + " copy(s)\nTape Cut " +
				((tapeCutMode == LWPrintTapeCut.EachLabel) ? "Each" :
						(tapeCutMode == LWPrintTapeCut.AfterJob)? "After": "None" ) +
				(isHalfCut? "\nHalfCut" : "") +
				(isLowSpeed? "\nKecepatan rendah" : "")+
				("\nPresisi " + density);
	}

	public static Map<String, Object> getPrintParameter(){
		Map<String, Object> printParameter = new HashMap<String, Object>();
		printParameter.put(LWPrintParameterKey.Copies, getCopies());
		printParameter.put(LWPrintParameterKey.TapeCut, getTapeCutMode());
		printParameter.put(LWPrintParameterKey.HalfCut, isHalfCut());
		printParameter.put(LWPrintParameterKey.PrintSpeed, isLowSpeed());
		printParameter.put(LWPrintParameterKey.Density, getDensity());
		return printParameter;
	}

	public static void savePrefFromBundleActResult(Bundle extras) {
		if (extras != null) {
			SharedPreferences.Editor preferences = getPrefInstance().edit();
			preferences.putInt(LWPrintParameterKey.Copies,
					extras.getInt(LWPrintParameterKey.Copies));
			preferences.putInt(LWPrintParameterKey.TapeCut,
					extras.getInt(LWPrintParameterKey.TapeCut));
			preferences.putBoolean(LWPrintParameterKey.HalfCut,
					extras.getBoolean(LWPrintParameterKey.HalfCut));
			preferences.putBoolean(LWPrintParameterKey.PrintSpeed,
					extras.getBoolean(LWPrintParameterKey.PrintSpeed));
			preferences.putInt(LWPrintParameterKey.Density,
					extras.getInt(LWPrintParameterKey.Density));
			preferences.commit();
		}
	}

	public static void saveCopies(int copies){
		getPrefInstance()
                .edit()
                .putInt(LWPrintParameterKey.Copies, copies)
                .commit();
	}

	public static int getCopies(){
		return getPrefInstance().getInt(LWPrintParameterKey.Copies, DEFAULT_COPIES_SETTING);
	}

	public static int getTapeCutMode(){
		return getPrefInstance().getInt(LWPrintParameterKey.TapeCut, DEFAULT_TAPE_CUT_SETTING);
	}

	public static boolean isHalfCut(){
		return getPrefInstance().getBoolean(LWPrintParameterKey.HalfCut, DEFAULT_HALF_CUT_SETTING);
	}

	public static boolean isLowSpeed(){
		return getPrefInstance().getBoolean(LWPrintParameterKey.PrintSpeed, DEFAULT_LOW_SPEED_SETTING);
	}

	public static int getDensity(){
		return getPrefInstance().getInt(LWPrintParameterKey.Density, DEFAULT_DENSITY_SETTING);
	}


	public static String getSuffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}

	public static String getPreffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(0, point);
		}
		return fileName;
	}

}

package com.bhullnatik.RNDateTimePicker;

import android.graphics.Color;
import android.text.format.DateFormat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RNDateTimePickerModule extends ReactContextBaseJavaModule {

    public RNDateTimePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private static final String NAME = "RNDateTimePicker";

    private static final String ERROR_INVALID_DATE = "E_INVALID_DATE";
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";

    @Override
    public String getName() {
        return NAME;
    }

    // Default javascript date format
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    private static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private Calendar[] getCalendarArray(final ReadableMap options, final String arrayName, final Promise promise) {
        ReadableArray dateArray = options.getArray(arrayName);
        Calendar[] dates = new Calendar[dateArray.size()];

        for (int index = 0; index < dateArray.size(); index++) {
            try {
                dates[index] = dateToCalendar(dateFormat.parse(dateArray.getString(index)));
            } catch (ParseException e) {
                promise.reject(ERROR_INVALID_DATE, "Invalid `" + arrayName + "` at [" + index + "]` param passed to DatePicker", e);
            }
        }
        return dates;
    }

    private Calendar getDefaultDate(final ReadableMap options, final Promise promise) {
        final Calendar now = Calendar.getInstance();

        if (options.hasKey("date")) {
            try {
                now.setTime(dateFormat.parse(options.getString("date")));
            } catch (ParseException e) {
                promise.reject(ERROR_INVALID_DATE, "Invalid `date` param passed to DatePicker", e);
            }
        }
        return now;
    }

    @ReactMethod
    public void showDatePicker(final ReadableMap options, final Promise promise) {
        final Calendar now = getDefaultDate(options, promise);

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        WritableMap result = new WritableNativeMap();

                        result.putInt("year", year);
                        result.putInt("monthOfYear", monthOfYear);
                        result.putInt("dayOfMonth", dayOfMonth);
                        promise.resolve(result);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if (options.hasKey("color")) {
            dpd.setAccentColor(Color.parseColor(options.getString("color")));
        }
        if (options.hasKey("title")) {
            dpd.setTitle(options.getString("title"));
        }
        if (options.hasKey("minDate")) {
            try {
                dpd.setMinDate(dateToCalendar(dateFormat.parse(options.getString("minDate"))));
            } catch (ParseException e) {
                promise.reject(ERROR_INVALID_DATE, "Invalid `minDate` param passed to DatePicker", e);
            }
        }
        if (options.hasKey("maxDate")) {
            try {
                dpd.setMaxDate(dateToCalendar(dateFormat.parse(options.getString("maxDate"))));
            } catch (ParseException e) {
                promise.reject(ERROR_INVALID_DATE, "Invalid `maxDate` param passed to DatePicker", e);
            }
        }
        if (options.hasKey("okText")) {
            dpd.setOkText(options.getString("okText"));
        }
        if (options.hasKey("cancelText")) {
            dpd.setCancelText(options.getString("cancelText"));
        }
        if (options.hasKey("themeDark")) {
            dpd.setThemeDark(options.getBoolean("themeDark"));
        }
        if (options.hasKey("showYearPickerFirst")) {
            dpd.showYearPickerFirst(options.getBoolean("showYearPickerFirst"));
        }
        if (options.hasKey("selectableDays")) {
            dpd.setSelectableDays(getCalendarArray(options, "selectableDays", promise));
        }
        if (options.hasKey("highlightedDays")) {
            dpd.setHighlightedDays(getCalendarArray(options, "highlightedDays", promise));
        }
        try {
            dpd.show(getCurrentActivity().getFragmentManager(), "DatePickerDialog");
        } catch (Exception e) {
            promise.reject(ERROR_NO_ACTIVITY, "No Activity currently attached", e);
        }
    }

    @ReactMethod
    public void showTimePicker(final ReadableMap options, final Promise promise) {
        final Calendar now = getDefaultDate(options, promise);

        boolean is24HourFormat = DateFormat.is24HourFormat(getReactApplicationContext());
        if (options.hasKey("24hourFormat")) {
            is24HourFormat = options.getBoolean("24hourFormat");
        }
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        WritableMap result = new WritableNativeMap();

                        result.putInt("hourOfDay", hourOfDay);
                        result.putInt("minute", minute);
                        result.putInt("second", second);
                        promise.resolve(result);
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                is24HourFormat
        );
        if (options.hasKey("color")) {
            tpd.setAccentColor(Color.parseColor(options.getString("color")));
        }
        if (options.hasKey("okText")) {
            tpd.setOkText(options.getString("okText"));
        }
        if (options.hasKey("cancelText")) {
            tpd.setCancelText(options.getString("cancelText"));
        }
        if (options.hasKey("themeDark")) {
            tpd.setThemeDark(options.getBoolean("themeDark"));
        }
        if (options.hasKey("title")) {
            tpd.setTitle(options.getString("title"));
        }
        try {
            tpd.show(getCurrentActivity().getFragmentManager(), "TimePickerDialog");
        } catch (Exception e) {
            promise.reject(ERROR_NO_ACTIVITY, "No Activity currently attached", e);
        }
    }
}

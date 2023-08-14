package tk.harshsurani.leaflibrary;

import android.app.Application;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(long timestamp){

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        String date = DateFormat.getTimeInstance().format("dd/MM/yyyy");
        return date;
    }
}

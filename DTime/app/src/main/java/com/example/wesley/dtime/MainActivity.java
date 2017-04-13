package com.example.wesley.dtime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    static Calendar date;
    static Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateButton = ((Button)findViewById(R.id.dateChooseButton));
        date = new GregorianCalendar();
        date.setTime(new Date());
        dateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(date.getTime()));
    }

    public void find(View v) throws Exception {
        findViewById(R.id.getProgressBar).setVisibility(ProgressBar.VISIBLE);
        findViewById(R.id.findButton).setEnabled(false);
        AsyncTask<String, Integer, Document> aSync = new GetWebpageTask().execute("http://aa.usno.navy.mil/cgi-bin/aa_altazw.pl?form=1&body=10&year=" + date.get(Calendar.YEAR) + "&month=" + (date.get(Calendar.MONTH) + 1) + "&day=" + date.get(Calendar.DAY_OF_MONTH) + "&intv_mag=1&state=" + (getResources().getStringArray(R.array.stateAcros))[((Spinner)findViewById(R.id.stateSpinner)).getSelectedItemPosition()] + "&place=" + ((EditText)findViewById(R.id.cityEditText)).getText().toString());
    }

    public void find(Document d) {
        if (d != null) {
            ((TextView)findViewById(R.id.outputTextView)).setText(comprehend(d.body().text()));
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Error retrieving webpage. Check your Internet.").setTitle("Error");

            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

    public void chooseDate(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public String comprehend(String s) {
        if (s.charAt(0) == 'A') {
            String time1 = "",time2 = "";
            Scanner sc = new Scanner(s);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 19; i++) {
                if (i == 4) {
                    String city = sc.nextLine().split(",")[0].toLowerCase();
                    ((EditText)findViewById(R.id.cityEditText)).setText(city.substring(0, 1).toUpperCase() + city.substring(1, city.length()));
                }
                sc.nextLine();
            }
            String time = sc.next();
            while (!time.equals("Back")) {
                if (Double.parseDouble(sc.next()) > 50) {
                    time1 = ((time1.equals("")) ? time : time1);
                    time2 = time;
                }
                sc.next();
                time = sc.next();
            }
            return time1.equals("") ? "Vitamin D is never produced" : "Vitamin D is produced between the hours of\n" + time1 + "\nand\n" + time2;
        }
        else if (s.charAt(7) == 'M') {
            (new AlertDialog.Builder(this)).setMessage("Please enter a longer city name").setTitle("Error").create().show();
        }
        else if (s.charAt(0) == 'U') {
            (new AlertDialog.Builder(this)).setMessage("Please enter a valid city name").setTitle("Error").create().show();
        }
        else {
            (new AlertDialog.Builder(this)).setMessage("Unsupported error, please report to developer\nDetails: " + s).setTitle("Error").create().show();
        }
        return "";
    }

    private class GetWebpageTask extends AsyncTask<String, Integer, Document> {

        @Override
        protected Document doInBackground(String... params) {
            try {
                return Jsoup.connect(params[0]).get();
            }
            catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Document d) {
            findViewById(R.id.getProgressBar).setVisibility(ProgressBar.INVISIBLE);
            findViewById(R.id.findButton).setEnabled(true);
            find(d);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.set(year, month, day);
            dateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(date.getTime()));
        }
    }
}

package com.example.wesley.dtime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATIONBUTTONS = 0;
    private static final int CALCULATEBUTTONS = 1;
    private static final int LOADINGBUTTONSLOCATION = 2;
    private static final int LOADINGBUTTONSCALCULATE = 3;
    private static final int NORMAL = 4;
    private static final int ERRORRECHECK = 5;
    private static final int other = 0;

    private static Calendar date;
    private static Button dateButton;
    private String doc;
    private boolean locationUpdated = false;
    private boolean calculationUpdated = false;
    private boolean isOffline = true;

    ExecutorService locationAndCalculateThreads = Executors.newFixedThreadPool(1);

    public void startup() {
        try{

            findLocation(true);
            calculate(true);
            setButtons(NORMAL);
        }catch (Exception e){e.printStackTrace(); errorRecovery();}
    }

    public synchronized void findLocation(boolean isStartup) throws InterruptedException
    {
        int option = 0; // FIXME: 5/3/17
        setButtons(LOCATIONBUTTONS);

        //WARNING: NEEDS TO ADD IN SOMETHING THAT WAITS UNTIL NOTIFIED THAT OPTION IS SELECTED
        if(option == 2)
        {
            locationAndCalculateThreads.execute(new GpsThread());
        }
        else
        {
            if(option == 1)
            {
                locationAndCalculateThreads.execute(new CityToLatitude());
            }
            else
            {
                locationAndCalculateThreads.execute(new OperateSQLForPrevLocations(this));
            }
        }

        setButtons(LOADINGBUTTONSLOCATION);

        //Waits until the thread comes back (MUST RELINQUISH KEY) or it times out by waiting for around one-two minute.
        //If waits too long, throw timeout exception
        long time = System.currentTimeMillis();
        while(!locationUpdated && 60000 > System.currentTimeMillis() - time)
        {
            wait();
        }
        if(60000 <= System.currentTimeMillis() - time)
            throw new InterruptedException("The Credentials could not be found within a minute");
        else
            locationUpdated = false;


        String address = "";
        String coordinates = "";
        new Thread(new OperateSQLForPrevLocations(this, address, coordinates)).start();

    }

    public synchronized void calculate(boolean isStartup) throws InterruptedException
    {
        setButtons(LOADINGBUTTONSCALCULATE);
        if(isOffline)
        {
            locationAndCalculateThreads.execute(new CalculateOffline());
        }
        else
        {
            locationAndCalculateThreads.execute(new CalculateOnline());
        }
        //awahile until times out or returns (MUST RELINQUISH KEY), and throws exception if times out
        long time = System.currentTimeMillis();
        while(!calculationUpdated && 60000 > System.currentTimeMillis() - time)
        {
            wait();
        }
        if(60000 <= System.currentTimeMillis() - time)
            throw new InterruptedException("The Credentials could not be found within a minute");
        else
            calculationUpdated = false;
    }

    public void changeLocation()
    {
        try{
            findLocation(false);
            calculate(false);
            setButtons(NORMAL);
        }catch(Exception e){e.printStackTrace();}
    }
    public void changeCalculation()
    {
        try{
            calculate(false);
            setButtons(NORMAL);
        }catch(Exception e){e.printStackTrace();}
    }

    public void updateGUI()
    {
        //updates the gui with all of the information gathered
    }
    public void errorRecovery()
    {
        //tries to determine what went wrong and tries to re-setup the connection stuff
    }
    private void setButtons(int typeOfChange)
    {
        //All necessary change in buttons
    }
    public synchronized void setDocument(String doc, boolean isLocation)
    {
        this.doc = doc;
        if (isLocation)
        {
            locationUpdated = true;
        }
        else
        {
            calculationUpdated = true;
        }
        notifyAll();
    }
    public synchronized String getDocument()
    {
        return doc;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateButton = ((Button)findViewById(R.id.dateChooseButton));
        date = new GregorianCalendar();
        date.setTime(new Date());
        dateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(date.getTime()));
        Intent intent = new Intent(this, LocationSelectorActivity.class);
        startActivity(intent);
        System.out.println("I'm a survivor"); //For testing porpoises only
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

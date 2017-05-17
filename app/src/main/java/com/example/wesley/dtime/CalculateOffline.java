package com.example.wesley.dtime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ryan on 4/25/17.
 * https://en.wikipedia.org/wiki/Position_of_the_Sun
 */

public class CalculateOffline implements Runnable {

    public void run()
    {
        double declenation = declinationOfSun();
    }


    private double declinationOfSun()
    {
        //Date date = new SimpleDateFormat("MMM dd yyyy hh:mma").parse(str.replaceAll("(?<=\\d)(?=\\D* \\d+ )\\p{L}+", ""));
        GregorianCalendar gc = new GregorianCalendar();
        System.out.println(gc.get(Calendar.DAY_OF_YEAR));
        double n = gc.get(Calendar.DAY_OF_YEAR);
        return arcsin(sin(-23.44)*cos(((360/365.24)*(n+10)) + ((360/java.lang.Math.PI)*0.0167*sin(((360/365.24)*(n-2))))) );
    }





    private double obliquityofEcliptic()
    {
        return 23.439-(0.0000004*numDaysFromGreenwichNoon(new Date().toString()));  // FIXME: 5/17/17
    }
    private double distanceSunfromEarth()
    {
        double g = meanAnomalyofSun()%360;
        return 1.00014-(0.01671*(cos(g)))-(0.00014*cos(2*g));
    }
    private double eclipticLatitude(){return 0;}
    private double eclipticLongitude()
    {
        double g = meanAnomalyofSun()%360;
        return (meanLongitudeCorrectedForAberrationOfLight()%360) + (1.915*sin(g)) + (0.020*sin(2*g));
    }
    private double meanAnomalyofSun()
    {
        return 357.528+(0.9856003*numDaysFromGreenwichNoon(new Date().toString())); // FIXME: 5/17/17
    }
    private double meanLongitudeCorrectedForAberrationOfLight()
    {
        double degrees = 280.460;
        return degrees + (0.9856474*numDaysFromGreenwichNoon(new Date().toString()));   // FIXME: 5/17/17
    }
    private double numDaysFromGreenwichNoon(String date)
    {
        double constant = 2451545.0;
        return convertToJulian(date) - constant;
    }


    private int convertToJulian(String unformattedDate)
    {
    /*Unformatted Date: ddmmyyyy*/
        int resultJulian = 0;
        if(unformattedDate.length() > 0)
        {
     /*Days of month*/
            int[] monthValues = {31,28,31,30,31,30,31,31,30,31,30,31};

            String dayS, monthS, yearS;
            dayS = unformattedDate.substring(0,2);
            monthS = unformattedDate.substring(3, 5);
            yearS = unformattedDate.substring(6, 10);

     /*Convert to Integer*/
            int day = Integer.valueOf(dayS);
            int month = Integer.valueOf(monthS);
            int year = Integer.valueOf(yearS);

            //Leap year check
            if(year % 4 == 0)
            {
                monthValues[1] = 29;
            }
            //Start building Julian date
            String julianDate = "1";
            //last two digit of year: 2012 ==> 12
            julianDate += yearS.substring(2,4);

            int julianDays = 0;
            for (int i=0; i < month-1; i++)
            {
                julianDays += monthValues[i];
            }
            julianDays += day;

            if(String.valueOf(julianDays).length() < 2)
            {
                julianDate += "00";
            }
            if(String.valueOf(julianDays).length() < 3)
            {
                julianDate += "0";
            }

            julianDate += String.valueOf(julianDays);
            resultJulian =  Integer.valueOf(julianDate);
        }
        return resultJulian;
    }
    private double cos(double degrees) //Returns in degrees as well
    {
        return java.lang.Math.toDegrees(java.lang.Math.cos(java.lang.Math.toRadians(degrees)));
    }
    private double sin (double degrees) //returns in degrees as well
    {
        return java.lang.Math.toDegrees(java.lang.Math.sin(java.lang.Math.toRadians(degrees)));
    }
    private double arcsin(double degrees)
    {
        return java.lang.Math.toDegrees(java.lang.Math.asin(java.lang.Math.toRadians(degrees)));
    }


   /* private double rectangularEquatorialCoordinatesX()
    {
        return distanceSunfromEarth()*cos(eclipticLongitude());
    }
    private double rectangularEquatorialCoordinatesY()
    {
        return distanceSunfromEarth()*cos(obliquityofEcliptic())*sin(eclipticLongitude());
    }
    private double rectangularEquatorialCoordinatesY()
    {
        return distanceSunfromEarth()*sin(obliquityofEcliptic())*sin(eclipticLongitude());
    }
*/
    //private float rightAscension() {}   //FIGURE OUT HOW TO DO THIS, IS ODDLY WORDED ON WIKIPEDIA
  /*  private double declination()
    {
        return asin(sin(obliquityofEcliptic()) * sin(eclipticLongitude()));
    }*/
}

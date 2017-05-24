package com.example.wesley.dtime;

import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class LocationSelectorActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(
                    (getArguments().getInt(ARG_SECTION_NUMBER) == 1) ? R.layout.previous_location_selector_tab :
                            (getArguments().getInt(ARG_SECTION_NUMBER) == 2) ? R.layout.address_location_selector_tab :
                                    (getArguments().getInt(ARG_SECTION_NUMBER) == 3) ? R.layout.gps_location_selector_tab :
                                            (getArguments().getInt(ARG_SECTION_NUMBER) == 4) ? R.layout.map_location_selector_tab :
                    R.layout.activity_location_selector,
                    container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "By History";
                case 1:
                    return "By Address";
                case 2:
                    return "By GPS or Coordinates";
                case 3:
                    return "By Map";
            }
            return null;
        }
    }

    public void searchAddress(View v) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.US);
        ArrayList<Address> listOfAddress;
        listOfAddress = (ArrayList<Address>)geocoder.getFromLocationName(((EditText)findViewById(R.id.container).findViewById(R.id.address_EditText)).getText().toString(), 20);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); //(ArrayAdapter)((Spinner)findViewById(R.id.container).findViewById(R.id.addressSelect_Spinner)).getAdapter();
        if (listOfAddress.size() > 0) {
            if (listOfAddress.size() > 1) adapter.add("Select City");
            for (Address a : listOfAddress) {
                adapter.add(a.getLocality() + ", " + (a.getCountryName().equals("United States") ? a.getAdminArea() + ": " : "") + a.getCountryName());
            }
        }
        else {
            adapter.add("No Cities Found");
        }
        ((Spinner)findViewById(R.id.container).findViewById(R.id.addressSelect_Spinner)).setAdapter(adapter);
    }
}

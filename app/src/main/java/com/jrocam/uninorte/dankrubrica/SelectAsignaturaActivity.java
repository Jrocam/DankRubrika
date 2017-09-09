package com.jrocam.uninorte.dankrubrica;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class SelectAsignaturaActivity extends AsignaturaActivity {

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
    private DatabaseReference myRef;
    public String asignatura;
    public String[] alumnos = {"sin alumnos"};
    public String[] examenes = {"sin examenes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_asignatura);

        //Get stuff from last activity
        myRef = super.database.getReference();
        Intent i = getIntent();
        asignatura = i.getStringExtra("asignatura");
        setAlumnosExamenes(asignatura);
        Log.d("Msg", "NOMBRE?: " + asignatura);

        setTitle(asignatura);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



;

        //TestClass testObject = (TestClass) i.getSerializableExtra("testObject");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_listview, alumnos);
        //ListView listView = (ListView) findViewById(R.id.alumnos_lista);
        //listView.setAdapter(adapter);

    }
    public void setAlumnosExamenes(final String name){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear asignaturas

                //obteniendo alumnos de una asignatura
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.child("users").child("test").child("class").child(name).child("roster").getValue();
                if (value != null) {
                    Object[] alum = value.values().toArray();
                    alumnos = Arrays.copyOf(alum, alum.length, String[].class);
                    //Here you do your thang
                    for (int i = 0; i < alumnos.length; i++) {

                        Log.d("Msg", "Value is: " + alumnos[i]);
                    }
                }
                //obteniendo examenes de una asignatura
                Map<String, Object> valueEx = (Map<String, Object>) dataSnapshot.child("users").child("test").child("class").child(name).child("exams").getValue();
                if (valueEx != null) {
                    Object[] exam = valueEx.values().toArray();
                    examenes = Arrays.copyOf(exam, exam.length, String[].class);
                    //Here you do your thang
                    for (int i = 0; i < examenes.length; i++) {

                        Log.d("Msg", "Value is: " + examenes[i]);
                    }
                }
                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_asignatura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        private static final String ARG_ALUMNOS = "alumnos_list";
        private String[] menuItems;
        public PlaceholderFragment(){
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber, String[] alu) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putStringArray(ARG_ALUMNOS,alu);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            menuItems = getArguments().getStringArray(ARG_ALUMNOS);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_select_asignatura, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            ListView listView = (ListView) rootView.findViewById(R.id.alumnos_lista);
            Log.d("MENSAJE!", " MENU ES: " + Arrays.toString(menuItems));
            if(menuItems != null){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        menuItems
                );
                listView.setAdapter(adapter);
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment p;
            if (position==0){
                p = PlaceholderFragment.newInstance(position+1, alumnos);
            }else{
                p = PlaceholderFragment.newInstance(position+1, examenes);
            }
            return p;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1, alumnos);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LISTA";
                case 1:
                    return "EXÁMENES";
            }
            return null;
        }
    }
}

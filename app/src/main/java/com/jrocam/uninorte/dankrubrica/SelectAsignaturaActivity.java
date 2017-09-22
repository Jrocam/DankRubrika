package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



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
    public String[] rubricas = {"sin rubrica"};
    public String[] rubricasTotales = {"rub1","rub2"};
    public Fragment frag_asignaturas;
    public Fragment frag_examenes;
    static boolean active = false;

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

    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
    public void setAlumnosExamenes(final String name){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear asignaturas

                //examenes=null;
                //obteniendo alumnos de una asignatura
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.child("users").child("test").child("class").child(name).child("roster").getValue();
                if (value != null) {
                    Object[] alum = value.values().toArray();
                    alumnos = Arrays.copyOf(alum, alum.length, String[].class);

                    //INFLATER HERE
                    for (int i = 0; i < alumnos.length; i++) {
                        Log.d("Msg", "Value is: " + alumnos[i]);
                    }
                }
                ArrayList<String> vist = new ArrayList<String>();
                //obteniendo examenes de una asignatura
                Map<String, Object> valueEx = (Map<String, Object>) dataSnapshot.child("users").child("test").child("class").child(name).child("exams").getValue();
                if (valueEx != null) {
                    Object[] keys = valueEx.keySet().toArray();
                    Object[] exam = valueEx.values().toArray();
                    examenes = Arrays.copyOf(keys, keys.length, String[].class);
                    rubricas = Arrays.copyOf(exam, exam.length, String[].class);
                    //Here you do your thang
                    for (int i = 0; i < examenes.length; i++) {
                        Log.d("Msg", "Value is: " + examenes[i]);
                    }
                }
                ArrayList<String> listarubricas = new ArrayList<String>();

                //Obteniendo rubricas
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").getChildren()) {
                    Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();
                    //Put shit on components. yea boi. that's right
                    Log.d("Msg", "RUBRICA NAME IS: " + v.get("name").toString());
                    listarubricas.add(v.get("name").toString());

                }
                Object[] rubObj = listarubricas.toArray();
                rubricasTotales = Arrays.copyOf(rubObj,rubObj.length,String[].class);
                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                if (mSectionsPagerAdapter == null){
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);
                }else{
                    // Set up the ViewPager with the sections adapter.
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                            if(active){
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }
;
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
        private DatabaseReference myRef;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_ALUMNOS = "alumnos_list";
        private static final String ARG_EXAMENES_RUB = "rubricas_list";
        private static final String ARG_ASIGNATURA = "asignatura_name";
        private static final String ARG_RUBRICAS = "rubricas_total";
        private String[] menuItems;
        private String[] menuItemsRub;
        private int position;
        public String asigna;
        private String[] rubricasTotalSpinner;

        public PlaceholderFragment(){
        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber, String[] alu, String asig, String[] rub, String[] rubTotal) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_ASIGNATURA,asig);
            args.putStringArray(ARG_ALUMNOS,alu);
            args.putStringArray(ARG_EXAMENES_RUB,rub);
            args.putStringArray(ARG_RUBRICAS,rubTotal);
            fragment.setArguments(args);
            return fragment;
        }
        protected void dialogAlumno(final View v) {
            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
            View promptView = layoutInflater.inflate(R.layout.input_alumno, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setView(promptView);

            final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            User usr = new User("test");
                            usr.addStudentToClass(asigna,editText.getText().toString());
                            Snackbar.make(v, "Añadido alumno.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    })
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        protected void dialogExamen(final View v) {

            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
            View promptView = layoutInflater.inflate(R.layout.input_examen, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setView(promptView);

            final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
            final Spinner spin = (Spinner) promptView.findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, rubricasTotalSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);
            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            User usr = new User("test");
                            usr.addExam(asigna,editText.getText().toString(),spin.getSelectedItem().toString());
                            Snackbar.make(v, "Añadido examen.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    })
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            menuItems = getArguments().getStringArray(ARG_ALUMNOS);
            menuItemsRub = getArguments().getStringArray(ARG_EXAMENES_RUB);
            position = getArguments().getInt(ARG_SECTION_NUMBER);
            asigna = getArguments().getString(ARG_ASIGNATURA);
            rubricasTotalSpinner = getArguments().getStringArray(ARG_RUBRICAS);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_select_asignatura, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            if (position == 0){
            ListView listView = (ListView) rootView.findViewById(R.id.alumnos_lista);
            Log.d("MENSAJE!", " ALUMNOS arreglo ES: " + Arrays.toString(menuItems));
                if(menuItems != null){
                    BaseAdapter adapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_1,
                            menuItems
                    );
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("MENSAJE!", "CLICKED: ");
                        }

                    });
                }
                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton2);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAlumno(view);
                    }
                });

            }
            if (position == 1){
                ListView listView = (ListView) rootView.findViewById(R.id.alumnos_lista);
                Log.d("MENSAJE!", " EXAMENES arreglo ES: " + Arrays.toString(menuItems));
                if(menuItems != null){
                    BaseAdapter adapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_2,
                            android.R.id.text1,
                            menuItems
                    ){  @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(menuItems[position]);
                        text2.setText(menuItemsRub[position]);
                        return view;
                    }
                    };

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("MENSAJE!", "CLICKED: ");
                        }

                    });
                }

                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton2);
                fab.setImageResource(R.drawable.ic_add_file);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogExamen(view);
                    }
                });
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

            if (position==0){
                frag_asignaturas = PlaceholderFragment.newInstance(position, alumnos,asignatura,rubricas,rubricasTotales);
                return frag_asignaturas;
            }else{
                frag_examenes = PlaceholderFragment.newInstance(position, examenes,asignatura,rubricas,rubricasTotales);
                return frag_examenes;
            }
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
                    return "ESTUDIANTES";
                case 1:
                    return "EXÁMENES";
            }
            return null;
        }
    }
}

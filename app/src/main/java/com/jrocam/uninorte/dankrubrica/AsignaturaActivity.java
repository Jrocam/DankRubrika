package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AsignaturaActivity extends MainActivity implements NavigationView.OnNavigationItemSelectedListener {
    private LinearLayout parentLinearLayout;
    private int count=0;
    private DrawerLayout mDraverHijo;
    private DatabaseReference myRef;
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressBar spinner;

    User usr = new User("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura);
        setTitle("Asignaturas");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddField(view);
            }
        });
        mDraverHijo = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDraverHijo, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDraverHijo.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
        //FIREBASE
        myRef = database.getReference();
        setAsignaturas();
    }

    public void onAsignatura(View v) {
        TextView texto = (TextView) v.findViewById(R.id.titulo_asignatura);
        Intent i = new Intent(this,SelectAsignaturaActivity.class);
        Log.d("Msg", "ASIIiIII: " + texto.getText().toString());
        i.putExtra("asignatura", texto.getText().toString() );
        startActivity(i);
    }

    public void onAddField(View v){
        //crea un dialogo y lo muestra en pantalla
        showInputDialog(v);

        //añade al layout la nueva asignatura si apretó OK

    }
    public void onDelete(View v){
        parentLinearLayout.removeView((View) v.getParent());
        count--;
    }
    public void setAsignaturas(){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear asignaturas
                parentLinearLayout.removeAllViews();
                //many stuff, obteniendo materias de un user
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("class").getChildren()) {
                    // TODO: handle the post
                    Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();
                    //Put shit on components. yea boi. that's right
                    Log.d("Msg", "VALUUEEEE is: " + v.get("name"));
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.list_asignatura_card, null);
                    TextView texto = (TextView) rowView.findViewById(R.id.titulo_asignatura);
                    count++;
                    texto.setText(v.get("name").toString());
                    parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - count);
                }
                spinner.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }
    protected void showInputDialog(final View v) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(AsignaturaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_asignatura, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AsignaturaActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        usr.addNewClass(editText.getText().toString());
                        Snackbar.make(v, "Añadida nueva asignatura.", Snackbar.LENGTH_LONG)
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
}

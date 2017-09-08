package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class RubricaActivity extends MainActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout parentLinearLayout;
    private int count=0;
    private DrawerLayout mDraverHijo;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubrica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddField(view);
                Snackbar.make(view, "Añadida nueva rubrica.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDraverHijo = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,  mDraverHijo, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDraverHijo.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        //FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        setRubrics();
    }


    private void onAddField(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_rubrica_card, null);
        TextView texto = (TextView) rowView.findViewById(R.id.titulo_rubrica);
        count++;
        texto.setText("RUBRICA: "+ count);
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - count);
    }
    public void onDelete(View v){
        parentLinearLayout.removeView((View) v.getParent());
        count--;
    }
    public void setRubrics(){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //many stuff, obteniendo materias de un user
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").getChildren()) {
                    // TODO: handle the post
                    Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();

                    //Put shit on components. yea boi. that's right
                    Log.d("Msg", "VALUUEEEE is: " + v.get("name"));
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.list_rubrica_card, null);
                    TextView texto = (TextView) rowView.findViewById(R.id.titulo_rubrica);
                    count++;
                    texto.setText(v.get("name").toString());
                    parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - count);                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }
}

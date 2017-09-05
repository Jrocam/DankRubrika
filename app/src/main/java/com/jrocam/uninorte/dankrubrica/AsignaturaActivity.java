package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AsignaturaActivity extends MainActivity implements NavigationView.OnNavigationItemSelectedListener {
    private LinearLayout parentLinearLayout;
    private int count=0;
    private DrawerLayout mDraverHijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddField(view);
                Snackbar.make(view, "Añadida nueva asignatura.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDraverHijo = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDraverHijo, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDraverHijo.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onAsignatura(View view) {

        startActivity(new Intent(this,SelectAsignaturaActivity.class));

    }

    private void onAddField(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_asignatura_card, null);
        TextView texto = (TextView) rowView.findViewById(R.id.titulo_asignatura);
        count++;
        texto.setText("ASIGNATURA: "+count);

        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - count);
    }
    public void onDelete(View v){
        parentLinearLayout.removeView((View) v.getParent());
        count--;
    }

}

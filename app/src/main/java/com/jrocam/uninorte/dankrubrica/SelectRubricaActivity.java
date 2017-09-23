package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SelectRubricaActivity extends RubricaActivity {
    public String rubrica;
    private LinearLayout parentLinearLayout2;
    private DatabaseReference myRef;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rubrica);
        //FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Intent i = getIntent();
        rubrica = i.getStringExtra("rubrica");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(rubrica);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("categorías");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        parentLinearLayout2 = (LinearLayout) findViewById(R.id.parent_linear_layout);
        getCategorias();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_rubrica, menu);
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
    public void getCategorias(){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //REMOVE ALL
                parentLinearLayout2.removeAllViews();
                //many stuff, obteniendo materias de un user
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").child(rubrica).getChildren()){
                    // TODO: handle the post
                    String categ = postSnapshot.getKey();
                    if (!categ.equals("name")){
                        //Put shit on components. yea boi. that's right
                        Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();
                        Log.d("Msg", "CATEGORIAS is: " + categ);
                        Log.d("Msg", "peso is: " + v.get("peso"));
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.list_categorias, null);
                        TextView texto = (TextView) rowView.findViewById(R.id.cat_name);
                        TextView peso = (TextView) rowView.findViewById(R.id.cat_peso);
                        count++;
                        texto.setText(categ);
                        peso.setText(v.get("peso").toString()+" %");
                        parentLinearLayout2.addView(rowView,parentLinearLayout2.getChildCount() - count);
                    }
                }
                //spinner2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }
    public void onCategoria(View v) {
        TextView texto = (TextView) v.findViewById(R.id.cat_name);
        TextView porce = (TextView) v.findViewById(R.id.cat_peso);
        Intent r = new Intent(this,SelectCategoriasActivity.class);
        r.putExtra("categoria", texto.getText().toString() );
        r.putExtra("porcentaje", porce.getText().toString() );
        r.putExtra("rubrica", rubrica);
        startActivity(r);
    }
}

package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

public class SelectCategoriasActivity extends AppCompatActivity {
    private String categoria;
    private String rubrica;
    private String porcentajeCat;
    private LinearLayout parentLinearLayout3;
    private DatabaseReference myRef;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categorias);
        //FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Intent i = getIntent();
        rubrica = i.getStringExtra("rubrica");
        categoria = i.getStringExtra("categoria");
        porcentajeCat = i.getStringExtra("porcentaje");
        setTitle(categoria+"  ("+porcentajeCat+")");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("elementos");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        parentLinearLayout3 = (LinearLayout) findViewById(R.id.parent_linear_layout);
        getElementos();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void getElementos(){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //REMOVE ALL
                parentLinearLayout3.removeAllViews();
                //many stuff, obteniendo materias de un user
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").child(rubrica).child(categoria).getChildren()){
                    // TODO: handle the post
                    String elem = postSnapshot.getKey();
                    if (!elem.equals("peso")){
                        //Put shit on components. yea boi. that's right
                        Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();
                        Log.d("Msg", "ELEMENTOS is: " + elem);
                        Log.d("Msg", "peso is: " + v.get("peso"));
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.list_elementos, null);
                        TextView texto = (TextView) rowView.findViewById(R.id.ele_name);
                        TextView peso = (TextView) rowView.findViewById(R.id.ele_peso);
                        TextView descrip = (TextView) rowView.findViewById(R.id.ele_descrip);
                        count++;
                        texto.setText(elem);
                        peso.setText(v.get("peso").toString()+" %");
                        descrip.setText("- "+v.get("descripcion").toString());
                        parentLinearLayout3.addView(rowView,parentLinearLayout3.getChildCount() - count);
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
    private void onAddField(View v){
        /*LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_rubrica_card, null);
        TextView texto = (TextView) rowView.findViewById(R.id.titulo_rubrica);
        count++;
        texto.setText("RUBRICA: "+ count);
        parentLinearLayout.addView(rowView, count);*/
        //addRubricaDialog(v);
    }
    public void onDelete(View v){
        parentLinearLayout3.removeView((View) v.getParent());
        count--;
    }
}

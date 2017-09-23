package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SelectCategoriasActivity extends AppCompatActivity {
    private String categoria;
    private String rubrica;
    private String porcentajeCat;
    private String l1,l2,l3,l4;
    private LinearLayout parentLinearLayout3;
    private DatabaseReference myRef;
    private int count=0;
    private int sumapesos;
    private int maxpeso=100;
    private ArrayList<ArrayList<String>> Niveles = new ArrayList<>();

    User usr = new User("test");
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

        maxpeso = Integer.parseInt(porcentajeCat.substring(0,porcentajeCat.length()-2));

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
                if (sumapesos < maxpeso){
                    addElementoDialog(view);
                }else{
                    Snackbar.make(view, "Peso no puede ser mayor a "+maxpeso+"%", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
    public void getElementos(){
        //Constant Data retriever from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //REMOVE ALL
                Niveles = new ArrayList<ArrayList<String>>();
                parentLinearLayout3.removeAllViews();
                sumapesos = 0;
                //many stuff, obteniendo materias de un user
                ArrayList<String> l = new ArrayList<String>();;
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").child(rubrica).child(categoria).getChildren()){
                    // TODO: handle the post
                    l = new ArrayList<String>();
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
                        l.add(v.get("l1").toString());
                        l.add(v.get("l2").toString());
                        l.add(v.get("l3").toString());
                        l.add(v.get("l4").toString());
                        Niveles.add(l);
                        texto.setText(elem);
                        peso.setText(v.get("peso").toString()+" %");
                        descrip.setText("- "+v.get("descripcion").toString());
                        sumapesos = sumapesos + Integer.parseInt(v.get("peso").toString());
                        parentLinearLayout3.addView(rowView,parentLinearLayout3.getChildCount() - count);
                    }
                    Niveles.add(l);
                }

                Log.d("Msg", "NIVELES IS: " + Niveles);
                Log.d("Msg", "SUMAPESOS IS: " + sumapesos);
                getSupportActionBar().setTitle(categoria+"  ["+sumapesos+"/"+maxpeso+"]%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Msg", "Failed to read value.", error.toException());
            }
        });
    }
    public void onElementos(View v){
        editElementoDialog(v);
    }
    public void onDelete(View v){
        parentLinearLayout3.removeView((View) v.getParent());
        count--;
    }
    protected void editElementoDialog(final View v) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(SelectCategoriasActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_levels, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectCategoriasActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText1 = (EditText) promptView.findViewById(R.id.edittext1);
        final EditText editText2 = (EditText) promptView.findViewById(R.id.edittext2);
        final EditText editText3 = (EditText) promptView.findViewById(R.id.edittext3);
        final EditText editText4 = (EditText) promptView.findViewById(R.id.edittext4);
        //editText1.setText();
        //editText2.setText();
        //editText3.setText();
        //editText4.setText();
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        //usr.addRubric(editText.getText().toString());
                        Snackbar.make(v, "Añadido nuevo elemento.", Snackbar.LENGTH_LONG)
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
    protected void addElementoDialog(final View v){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(SelectCategoriasActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_elemento, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectCategoriasActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        final EditText editNum = (EditText) promptView.findViewById(R.id.editNumber);
        final EditText editDes = (EditText) promptView.findViewById(R.id.editdesc);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        try{
                            sumapesos = sumapesos + Integer.parseInt(editNum.getText().toString());
                        }catch(Error e){}
                        if (sumapesos < maxpeso){
                            Elemento ele = new Elemento(editNum.getText().toString(),editDes.getText().toString(), "Insatisfactorio", "Aceptable", "Satisfactorio", "Ejemplar");
                            // setup a dialog window
                            usr.addElementToCategory(rubrica,categoria,editText.getText().toString(),ele);
                            Snackbar.make(v, "Añadida nueva categoría", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else{
                            sumapesos = sumapesos - Integer.parseInt(editNum.getText().toString());
                            Snackbar.make(v, "Peso no puede ser mayor a "+maxpeso+"%", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
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
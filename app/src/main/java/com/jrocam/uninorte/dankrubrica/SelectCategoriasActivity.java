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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
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
    private ArrayList<String> elementos = new ArrayList<>();
    private ArrayList<String> pesos = new ArrayList<>();
    private ArrayList<String> descripciones = new ArrayList<>();

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
                if (sumapesos <= maxpeso){
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
                count = 0;
                elementos = new ArrayList<>();
                pesos = new ArrayList<>();
                descripciones = new ArrayList<>();
                //many stuff, obteniendo materias de un user
                ArrayList<String> l = new ArrayList<String>();;
                for (DataSnapshot postSnapshot: dataSnapshot.child("users").child("test").child("rubrics").child(rubrica).child(categoria).getChildren()){
                    // TODO: handle the post

                    String elem = postSnapshot.getKey();
                    if (!elem.equals("peso")){
                        //Put shit on components. yea boi. that's right
                        Map<String, Object> v = (Map<String, Object>) postSnapshot.getValue();
                        l = new ArrayList<String>();
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
                        elementos.add(elem);
                        peso.setText(v.get("peso").toString()+" %");
                        pesos.add(v.get("peso").toString());
                        descrip.setText("- "+v.get("descripcion").toString());
                        descripciones.add(v.get("descripcion").toString());
                        sumapesos = sumapesos + Integer.parseInt(v.get("peso").toString());
                        parentLinearLayout3.addView(rowView,parentLinearLayout3.getChildCount() - count);
                    }
                }

                Log.d("Msg", "NIVELES IS: "+count+" " + Niveles);
                Log.d("Msg", "ELEMENTOS IS: " + elementos);
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
        nivelesElementoDialog(v);
    }
    public void onDelete(final View v){
        LayoutInflater layoutInflater = LayoutInflater.from(SelectCategoriasActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_alerta, null);
        LinearLayout vi = (LinearLayout) v.getParent().getParent();
        TextView elem = (TextView) vi.findViewById(R.id.ele_name);
        final String elementoEli = elem.getText().toString();
        Log.d("Msg", "Elemento SELECT IS: " + elementoEli);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectCategoriasActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                    usr.deleteElement(rubrica,categoria,elementoEli);
                    Snackbar.make(v, "Eliminado elemento de "+categoria+".", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                    }
                })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    protected void nivelesElementoDialog(final View v) {
        // get prompts.xml view
        TextView nombre = (TextView) v.findViewById(R.id.ele_name);
        int positionElemento=0;
        for (int i=0;i < elementos.size();i++){
            if (nombre.getText().toString().equals(elementos.get(i))){
                positionElemento = i;
                break;
            }
        }
        final String elementoSelect = elementos.get(positionElemento);
        final String pesoElementoSelect = pesos.get(positionElemento);
        final String descElementoSelect = descripciones.get(positionElemento);
        Log.d("Msg", "POSITIONELEMENTO IS: " + positionElemento);
        ArrayList<String> e = Niveles.get(positionElemento);
        LayoutInflater layoutInflater = LayoutInflater.from(SelectCategoriasActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_levels, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectCategoriasActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText1 = (EditText) promptView.findViewById(R.id.edittext1);
        final EditText editText2 = (EditText) promptView.findViewById(R.id.edittext2);
        final EditText editText3 = (EditText) promptView.findViewById(R.id.edittext3);
        final EditText editText4 = (EditText) promptView.findViewById(R.id.edittext4);
        editText1.setText(e.get(0));
        editText2.setText(e.get(1));
        editText3.setText(e.get(2));
        editText4.setText(e.get(3));
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        Elemento eleme = new Elemento(pesoElementoSelect,descElementoSelect,
                                editText1.getText().toString(),
                                editText2.getText().toString(),
                                editText3.getText().toString(),
                                editText4.getText().toString());
                        usr.addElementToCategory(rubrica,categoria,elementoSelect,eleme);
                        Snackbar.make(v, "Se ha editado el elemento.", Snackbar.LENGTH_LONG)
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
                        if (sumapesos <= maxpeso){
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
    public void onEditElemento(final View v){
        // get elemento seleccionado
        LinearLayout vi = (LinearLayout) v.getParent().getParent();
        TextView elem = (TextView) vi.findViewById(R.id.ele_name);
        int positionElemento=0;
        for (int i=0;i < elementos.size();i++){
            if (elem.getText().toString().equals(elementos.get(i))){
                positionElemento = i;
                break;
            }
        }
        final ArrayList<String> e = Niveles.get(positionElemento);

        TextView descripcion = (TextView) vi.findViewById(R.id.ele_descrip);
        TextView peso = (TextView) vi.findViewById(R.id.ele_peso);
        final String elementoSel = elem.getText().toString();
        //get view
        LayoutInflater layoutInflater = LayoutInflater.from(SelectCategoriasActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_elemento, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectCategoriasActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        final EditText editNum = (EditText) promptView.findViewById(R.id.editNumber);
        final EditText editDes = (EditText) promptView.findViewById(R.id.editdesc);
        TextView titulo = (TextView) promptView.findViewById(R.id.textView3);
        titulo.setText("Editar elemento");
        editText.setText(elem.getText().toString());
        final String pesoAnt = peso.getText().toString().substring(0,peso.getText().toString().length()-2);
        editDes.setText(descripcion.getText().toString().substring(2,descripcion.getText().toString().length()));
        editNum.setText(peso.getText().toString().substring(0,peso.getText().toString().length()-2));

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        try{
                            sumapesos = sumapesos - Integer.parseInt(pesoAnt) + Integer.parseInt(editNum.getText().toString());
                        }catch(Error e){}
                        if (sumapesos <= maxpeso){
                            Elemento ele = new Elemento(editNum.getText().toString(),editDes.getText().toString(), e.get(0), e.get(1), e.get(2), e.get(3));
                            usr.deleteElement(rubrica,categoria,elementoSel);
                            // setup a dialog window
                            usr.addElementToCategory(rubrica,categoria,editText.getText().toString(),ele);
                            Snackbar.make(v, "Editado elemento de "+categoria+".", Snackbar.LENGTH_LONG)
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
        alert.show();;
    }
}

package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private int sumapesos;
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
                if (sumapesos <= 100){
                    onAddCategoria(view);
                }else{
                    Snackbar.make(view, "Peso no puede ser mayor a 100%", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
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
                sumapesos = 0;
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
                        sumapesos = sumapesos + Integer.parseInt(v.get("peso").toString());
                        parentLinearLayout2.addView(rowView,parentLinearLayout2.getChildCount() - count);
                    }
                }
                Log.d("Msg", "SUMAPESOS IS: " + sumapesos);
                getSupportActionBar().setTitle(rubrica+"  ["+sumapesos+"/"+100+"]%");
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
    public void onAddCategoria(View v){
        addCategoriaDialog(v);
    }
    public void addCategoriaDialog(final View v){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(SelectRubricaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_categoria, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectRubricaActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        final EditText editNum = (EditText) promptView.findViewById(R.id.editNumber);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        try{
                            sumapesos = sumapesos + Integer.parseInt(editNum.getText().toString());
                        }catch(Error e){}
                        if (sumapesos <= 100){
                            usr.addCategoryToRubric(rubrica,editText.getText().toString(),editNum.getText().toString());
                            Snackbar.make(v, "Añadida nueva categoría", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else{
                            sumapesos = sumapesos - Integer.parseInt(editNum.getText().toString());
                            Snackbar.make(v, "Peso no puede ser mayor a 100%", Snackbar.LENGTH_LONG)
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
    public void onDeleteCategoria(final View v){
        LayoutInflater layoutInflater = LayoutInflater.from(SelectRubricaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_alerta, null);
        LinearLayout vi = (LinearLayout) v.getParent().getParent();
        TextView elem = (TextView) vi.findViewById(R.id.cat_name);
        final String categoriaEli = elem.getText().toString();
        TextView texto = (TextView) promptView.findViewById(R.id.textView);
        texto.setText("¿Seguro que deseas eliminar esta categoría y todos sus elementos?");
        Log.d("Msg", "Category SELECT IS: " +categoriaEli);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectRubricaActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        usr.deleteCategory(rubrica,categoriaEli);
                        Snackbar.make(v, "Eliminada categoría de "+rubrica+".", Snackbar.LENGTH_LONG)
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
    public void onEditCategoria( final View v){
        // get elemento seleccionado
        LinearLayout vi = (LinearLayout) v.getParent().getParent();
        TextView cat = (TextView) vi.findViewById(R.id.cat_name);
        TextView peso = (TextView) vi.findViewById(R.id.cat_peso);
        final String catSel = cat.getText().toString();
        //get view
        LayoutInflater layoutInflater = LayoutInflater.from(SelectRubricaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_categoria_solo_peso, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectRubricaActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText editNum = (EditText) promptView.findViewById(R.id.editNumber);
        TextView categoria = (TextView) promptView.findViewById(R.id.textView);
        categoria.setText(catSel);
        TextView titulo = (TextView) promptView.findViewById(R.id.textView3);
        titulo.setText("Editar peso de categoría");
        final String pesoAnt = peso.getText().toString().substring(0,peso.getText().toString().length()-2);
        editNum.setText(peso.getText().toString().substring(0,peso.getText().toString().length()-2));

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {;
                        try{
                            sumapesos = sumapesos - Integer.parseInt(pesoAnt) + Integer.parseInt(editNum.getText().toString());
                        }catch(Error e){}
                        if (sumapesos <= 100){
                            usr.editPesoCategory(rubrica,catSel,editNum.getText().toString());
                            Snackbar.make(v, "Editada categoría de "+rubrica+".", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else{
                            sumapesos = sumapesos - Integer.parseInt(editNum.getText().toString());
                            Snackbar.make(v, "Peso no puede ser mayor a 100%", Snackbar.LENGTH_LONG)
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

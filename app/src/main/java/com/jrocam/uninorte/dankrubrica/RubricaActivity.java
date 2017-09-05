package com.jrocam.uninorte.dankrubrica;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RubricaActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    private int count=0;

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
    }


    public void onAddField(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_rubrica_boton, null);
        TextView texto = (TextView) rowView.findViewById(R.id.titulo_rubrica);
        count++;
        texto.setText("LO QUE ME DE LA GANA: "+count);

        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        User test = new User("test");

        //Ejemplossss
        //Add student
        //test.addStudentToClass("Calculo","Juancho");

        //Add Category to rubric :D
        //test.addCategoryToRubric("Rubric1","Categoria1",2);

        //Ejemplo agregar elemento a cagegoria
        //Elemento s = new Elemento(3,"l1","l2","l3","l4");
        //test.addElementToCategory("Rubric1", "Categoria1", "NombreElemento", s);


    }
    public void onDelete(View v){
        parentLinearLayout.removeView((View) v.getParent());
        count--;
    }

}

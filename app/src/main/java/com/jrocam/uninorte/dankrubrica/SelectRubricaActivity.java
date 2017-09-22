package com.jrocam.uninorte.dankrubrica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectRubricaActivity extends RubricaActivity {
    public String rubrica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_rubrica);
        Intent i = getIntent();
        rubrica = i.getStringExtra("rubrica");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(rubrica);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TryRubric();
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

    public void TryRubric(){
        // Get the widgets reference from XML layout
        GridView gv = (GridView) findViewById(R.id.gv);
        Button btn = (Button) findViewById(R.id.btn);

        // Initializing a new String Array
        String[] plants = new String[]{
                "Catalina ironwood",
                "Cabinet cherry",
                "Pale corydalis",
                "Pink corydalis",
                "Land cress",
                "Coast polypody",
                "Water fern"
        };

        // Populate a List from Array elements
        final List<String> plantsList = new ArrayList<String>(Arrays.asList(plants));

        // Create a new ArrayAdapter
        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, plantsList);

        // Data bind GridView with ArrayAdapter (String Array elements)
        gv.setAdapter(gridViewArrayAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add/insert item to ArrayAdapter
                // Insert at the end of ArrayAdapter
                // ArrayAdapter is zero based index
                plantsList.add(plantsList.size(),"Bamboozled");

                // Update the GridView
                gridViewArrayAdapter.notifyDataSetChanged();

                // Get the newly added item from ArrayAdapter
                String addedItemText = plantsList.get(plantsList.size()-1);

                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item added : " + addedItemText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

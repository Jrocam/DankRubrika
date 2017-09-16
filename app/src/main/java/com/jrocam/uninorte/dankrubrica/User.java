package com.jrocam.uninorte.dankrubrica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mateodaza on 9/4/17.
 */

public class User implements Serializable {

    public String username, stuff;

    public User(String username){
        this.username = username;
    }

    public void addNewClass(String name){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/class/"+name);
        //Instancia Materia
        Materia materia = new Materia(name);
        myRef.setValue(materia);
    }
    public void addStudentToClass(String className, String studentName){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/class/"+className+"/roster");
        //Instancia Materia
        myRef.push().setValue(studentName);
    }
    public void addExam(String className, String examName, String idRubrica){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/class/"+ className + "/exams");
        //Instancia Examen
        //Examen examen = new Examen(examName, idRubrica);
        myRef.child(examName).setValue(idRubrica);
    }
    public void addRubric(String rubricName){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/rubrics/"+ rubricName);
        //Instancia Rubrica
        Rubrica rubric = new Rubrica(rubricName);
        myRef.setValue(rubric);
    }
    public void addCategoryToRubric(String rubricName, String categoryName, int peso){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/rubrics/"+ rubricName +"/"+categoryName+"/");
        //Instancia Categoria
        Categoria category = new Categoria(peso);
        myRef.setValue(category);
    }
    public void addElementToCategory(String rubricName, String categoryName, String elementName,Elemento elemento){ //elemento[6]: Nombre+peso+L1+L2+L3+L4
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+this.username+"/rubrics/"+rubricName+"/"+categoryName+"/"+elementName+"/");
        myRef.setValue(elemento);
    }
}


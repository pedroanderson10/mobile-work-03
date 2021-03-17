package com.example.trabalho_03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AdicionarTarefa extends AppCompatActivity {

    public static int RESULT_ADD = 1;
    public static int RESULT_EDIT = 2;
    public static int RESULT_CANCEL = 3;

    EditText edtTarefa, edtTurno;

    boolean edit;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_tarefa);

        edtTarefa = findViewById(R.id.editTextTarefa);
        edtTurno = findViewById(R.id.editTextTurno);

        edit = false;

        if(getIntent().getExtras() != null){
            String nome = (String) getIntent().getExtras().get("nomeTarefa");
            String turno = (String) getIntent().getExtras().get("turnoTarefa");
            //String chaveTarefa = (String) getIntent().getExtras().get("chaveTarefa");
             id = (int) getIntent().getExtras().get("chaveTarefa");
            //id = Integer.parseInt(chaveTarefa);

            edtTarefa.setText(nome);
            edtTurno.setText(turno);

            edit = true;
        }

    }

    //Adicionar ou Editar Tarefa
    public void adicionarTarefa(View v){

        Intent intent = new Intent();

        String tarefa = edtTarefa.getText().toString();
        String turno = edtTurno.getText().toString();

        intent.putExtra("nomeTarefa", tarefa);
        intent.putExtra("turnoTarefa", turno);

        if(edit == true){
            intent.putExtra("chaveTarefa", id);
        }

        setResult(RESULT_ADD, intent);
        finish();
    }

    //Editar Tarefa
    public void editarTarefa(){
        //...
    }

    //Cancelar
    public void Cancelar(View v){
        setResult(RESULT_CANCEL);
        finish();
    }

    //Pop-up Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Pop-up Menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent1 = new Intent(getApplicationContext(), AdicionarTarefa.class);
                startActivity(intent1);
                return true;
            case R.id.voltar:
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
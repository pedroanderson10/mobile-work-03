package com.example.trabalho_03;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_ADD = 1;
    public static int REQUEST_EDIT = 2;

    EditText chave;
    int valorChave;
    int id;

    //Firebase
    FirebaseDatabase fbDatabase;
    DatabaseReference fbReference;

    //List View
    ArrayList<Tarefa> listaTarefas;
    ArrayAdapter adapter;
    ListView listViewTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaTarefas = new ArrayList<Tarefa>();

        //Criar List View
        /*
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTarefas );
        listViewTarefas = findViewById(R.id.listaTarefas);
        listViewTarefas.setAdapter(adapter);*/
        listViewTarefas = findViewById(R.id.listaTarefas);

        //Inicializar Firebase e buscar dados já salvos para ser visualizado
        startFirebase();
        buscarFirebase();

    }

    //Iniciar o Firebase
    public void startFirebase(){
        FirebaseApp.initializeApp(MainActivity.this);
        fbDatabase = FirebaseDatabase.getInstance();
        fbDatabase.setPersistenceEnabled(true);
        fbReference = fbDatabase.getReference();
    }

    //Buscar dados do Firebase
    public void buscarFirebase(){
        fbReference.child("Tarefa").addValueEventListener(new ValueEventListener() {

            //Auxiliar para atualizar o contador
            Tarefa atualizadorTarefa = new Tarefa();

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTarefas.clear();
                for(DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Tarefa tarefa = objSnapshot.getValue(Tarefa.class);

                    //Atualizar o contador
                    atualizadorTarefa.atualizarContador(tarefa.getChave() + 1);

                    listaTarefas.add(tarefa);
                }
                adapter = new ArrayAdapter<Tarefa>(MainActivity.this, android.R.layout.simple_list_item_1, listaTarefas );

                listViewTarefas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Adicionar Tarefa
    public void clicarAdicionarTarefa(View v){
        Intent intent = new Intent(this, AdicionarTarefa.class);
        startActivityForResult(intent, REQUEST_ADD);
    }

    //Deletar Tarefa
    public void clicarDeletarTarefa(View v){
        EditText textDel =  (EditText) findViewById(R.id.textDelete);
        int chaveDel = Integer.parseInt(textDel.getText().toString());

        Tarefa tarefaDel = listaTarefas.get(chaveDel);

        fbReference.child("Tarefa").child(String.valueOf(tarefaDel.getChave())).removeValue();

        //Atualizar chaves
        for (Tarefa tarefa: listaTarefas ){
            if(tarefa.getChave() > chaveDel) {
                tarefa.setNome(tarefa.getNome());
                tarefa.setTurno(tarefa.getTurno());
                tarefa.setChave(tarefa.getChave() - 1);
                fbReference.child("Tarefa").child(String.valueOf(tarefa.getChave())).setValue(tarefa);
                fbReference.child("Tarefa").child(String.valueOf(tarefa.getChave() + 1 )).removeValue();
            }
        }

    }

    //Editar Tarefa
    public void clicarEditarTarefa(View v){
        Intent intent = new Intent(this, AdicionarTarefa.class);


        chave =  (EditText) findViewById(R.id.textEdit);
        valorChave = Integer.parseInt(chave.getText().toString());

        Tarefa tarefa = listaTarefas.get(valorChave);

        intent.putExtra("chaveTarefa", tarefa.getChave());
        intent.putExtra("nomeTarefa", tarefa.getNome());
        intent.putExtra("turnoTarefa", tarefa.getTurno());

        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_ADD && resultCode == AdicionarTarefa.RESULT_ADD){

            String nomeTarefa = (String) data.getExtras().get("nomeTarefa");
            String turnoTarefa = (String) data.getExtras().get("turnoTarefa");

            Tarefa tarefa = new Tarefa(nomeTarefa, turnoTarefa);

            fbReference.child("Tarefa").child(String.valueOf(tarefa.getChave())).setValue(tarefa);

            listaTarefas.add(tarefa);
            adapter.notifyDataSetChanged();

        }else if(requestCode==REQUEST_EDIT && resultCode == AdicionarTarefa.RESULT_ADD){
            String nomeTarefa = (String) data.getExtras().get("nomeTarefa");
            String turnoTarefa = (String) data.getExtras().get("turnoTarefa");
            //String chaveTarefa = (String) data.getExtras().get("chaveTarefa");
            id = (int) data.getExtras().get("chaveTarefa");
            //int chave = Integer.parseInt(chaveTarefa);


            //Tarefa tarefa = listaTarefas.remove(1);

            for (Tarefa tarefa: listaTarefas ){
                if(valorChave == id){
                    tarefa.setNome(nomeTarefa);
                    tarefa.setTurno(turnoTarefa);
                    tarefa.setChave(id);
                    fbReference.child("Tarefa").child(String.valueOf(tarefa.getChave())).setValue(tarefa);
                    break;
                }
            }

            adapter.notifyDataSetChanged();


        }else if(resultCode == AdicionarTarefa.RESULT_CANCEL){
            Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
        }

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
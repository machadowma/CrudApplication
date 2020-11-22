package com.example.crudapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDados;
    public ListView listViewDados;
    public Button botao;
    public ArrayList<Integer> arrayIds;
    public Integer idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewDados = (ListView) findViewById(R.id.listViewDados);
        botao = (Button) findViewById(R.id.buttonCadastrar);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();
            }
        });

        listViewDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idSelecionado = arrayIds.get(i);
                confirmaExcluir();
                return true;
            }
        });

        criarBancoDados();
        //inserirDadosTemp();
        listarDados();
    }

    @Override
    protected void onResume(){
        super.onResume();
        listarDados();
    }

    public void criarBancoDados(){
        try {
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS coisa(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    " , nome VARCHAR)");
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarDados(){
        try {
            arrayIds = new ArrayList<>();
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT id, nome FROM coisa", null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listViewDados.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void inserirDadosTemp(){
        try{
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            String sql = "INSERT INTO coisa (nome) VALUES (?)";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);

            stmt.bindString(1,"Coisa 1");
            stmt.executeInsert();

            stmt.bindString(1,"Coisa abc");
            stmt.executeInsert();

            stmt.bindString(1,"Coisa Terceira");
            stmt.executeInsert();

            bancoDados.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirTelaCadastro(){
        Intent intent = new Intent(this,CadastroActivity.class);
        startActivity(intent);
    }

    public void confirmaExcluir() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
        msgBox.setTitle("Excluir");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Você realmente deseja excluir esse registro?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluir();
                listarDados();
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        msgBox.show();
    }

    public void excluir(){
        //Toast.makeText(this, i.toString(), Toast.LENGTH_SHORT).show();
        try{
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            String sql = "DELETE FROM coisa WHERE id =?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1, idSelecionado);
            stmt.executeUpdateDelete();
            listarDados();
            bancoDados.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
package br.com.dev.felipeferreira.calculadorasaudavel2;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ImcActivity extends AppCompatActivity {

    private EditText editHeight;
    private EditText editWeight;
    private EditText editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        TextView txtImcDesc = findViewById(R.id.txt_imc_desc);
        editHeight = findViewById(R.id.edit_imc_height);
        editWeight = findViewById(R.id.edit_imc_weight);
        editName = findViewById(R.id.edit_imc_name);
        Button btnSend = findViewById(R.id.btn_imc_send);

        txtImcDesc.setOnClickListener(view -> txtImcDesc.setMaxLines(10));

        btnSend.setOnClickListener(view -> {
            //Falha
            if (!validate()) {
                alert(R.string.fields_message);
                return;
            }
            //Sucesso
            String sHeight = editHeight.getText().toString();
            String sWeight = editWeight.getText().toString();
            String sName = editName.getText().toString();

            int height = Integer.parseInt(sHeight);
            int weight = Integer.parseInt(sWeight);

            // Retorna o valor do IMC
            double result = calclateImc(height, weight);
            Log.d("Teste", "Resultado: " + result);

            //Retorna a mensagem se esta acima ou não do peso
            int imcResponseId = imcResponse(result);

            AlertDialog dialog = new AlertDialog.Builder(ImcActivity.this)
                    //                  criando uma string dinâmica!!
                    .setTitle(getString(R.string.imc_response, sName, result))
                    .setMessage(imcResponseId)
                    .setPositiveButton(android.R.string.ok, (dialog1, which) -> dialog1.dismiss()) //Lambda é uma forma mais "bonitinha" de declarar objetos anonimos
                    .setNegativeButton(R.string.save, ((dialog2, which) -> {
                        SqlHelper sqlHelper = SqlHelper.getInstance(ImcActivity.this);

                        new Thread(() -> {
                            int updateId = 0;

                            // verifica se tem ID vindo da tela anterior quando é UPDATE
                            if (getIntent().getExtras() != null)
                                updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;
                            // verifica se é update ou create
                            if (updateId > 0) {
                                calcId = SqlHelper.getInstance(ImcActivity.this).updateItem("imc", result, sName, updateId);
                            } else {
                                calcId = SqlHelper.getInstance(ImcActivity.this).addItem("imc", result, sName);
                            }

                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    alert(R.string.calc_saved);
                                    openListCalc();
                                }
                            });
                        }).start();
                    })).create();

            dialog.show();

            //Escondendo o teclado após clicar em 'Calcular'
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(), 0);
        });
    }

    private void openListCalc() {
        Intent intent = new Intent(this, ListCalcActivity.class);
        intent.putExtra("type", "imc");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list :
                openListCalc();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean validate() {
        if (!editWeight.getText().toString().startsWith("0") &&
                !editHeight.getText().toString().startsWith("0") &&
                !editWeight.getText().toString().isEmpty() &&
                !editHeight.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private double calclateImc(int height, int weight) {
        // peseo / (altura * altura) .. Cast convertendo int para double
        // dividindo a altura por 100, pois ela esta em Centímetros e quando divido por 100...
        // ...transformo para metros!!!!
        return weight / (((double) height / 100) * ((double) height / 100));
    }

    //Anotação @StringRes = Obriga o dev a responder o método com uma varíavel do tipo Res
    //Ou seja se o dev tentar responder com um número, ele vai dar erro, pois só aceita variáveis..
    //.. que estão dentro do 'Resources', ou seja que esta dentro da pasta res "R."
    @StringRes
    private int imcResponse(double imc) {
        if (imc < 15)
            return R.string.imc_severely_low_weight;
        else if (imc < 16)
            return R.string.imc_very_low_weight;
        else if (imc < 18.5)
            return R.string.imc_low_weight;
        else if (imc < 25)
            return R.string.normal;
        else if (imc < 30)
            return R.string.imc_high_weight;
        else if (imc < 35)
            return R.string.imc_so_high_weight;
        else if (imc < 40)
            return R.string.imc_severely_high_weight;
        else
            return R.string.imc_extreme_weight;
    }

    private void alert(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
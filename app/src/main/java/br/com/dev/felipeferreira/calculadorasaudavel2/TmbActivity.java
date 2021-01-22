package br.com.dev.felipeferreira.calculadorasaudavel2;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TmbActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editHeight;
    private EditText editWeight;
    private EditText editAge;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmb);

        editName = findViewById(R.id.edit_tmb_name);
        editHeight = findViewById(R.id.edit_tmb_height);
        editWeight = findViewById(R.id.edit_tmb_weight);
        editAge = findViewById(R.id.edit_tmb_age);
        spinner = findViewById(R.id.spinner_tmb_life_style);
        Button btnSend = findViewById(R.id.btn_tmb_send);

        btnSend.setOnClickListener(view -> {

            if (!validate()) {
                alert(R.string.fields_message);
                return;
            }

            String sHeight = editHeight.getText().toString();
            String sWeight = editWeight.getText().toString();
            String sAge = editAge.getText().toString();
            String sName = editName.getText().toString();

            int height = Integer.parseInt(sHeight);
            int weight = Integer.parseInt(sWeight);
            int age = Integer.parseInt(sAge);

            // Retorna o valor do TMB
            double result = calclateTmb(height, weight, age);
            //Retorna a quantidade calorica que o corpo precisa
            double tmbResponseId = tmbResponse(result);

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(sName)
                    .setMessage(getString(R.string.tmb_response, tmbResponseId))
                    .setPositiveButton(android.R.string.ok, (dialog1, which) -> dialog1.dismiss())
                    .setNegativeButton(R.string.save, (dialog2, which) -> {

                        new Thread(() ->{
                            int updateId = 0;

                            if(getIntent().getExtras() != null)
                            updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;


                            if(updateId > 0) {
                                calcId = SqlHelper.getInstance(this).updateItem("tmb", result, sName, updateId);
                            } else {
                                calcId = SqlHelper.getInstance(this).addItem("tmb", result, sName);
                            }
                            runOnUiThread(() -> {
                                if(calcId > 0) {
                                    alert(R.string.calc_saved);
                                    openListCalc();
                                }
                            });
                        }).start();
                    }).create();
            alertDialog.show();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editHeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editAge.getWindowToken(), 0);
        });

    }

    private void alert(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private double calclateTmb(int height, int weight, int age) {
        // peseo / (altura * altura) .. Cast convertendo int para double
        // dividindo a altura por 100, pois ela esta em Centímetros e quando divido por 100...
        // ...transformo para metros!!!!
        return 66 + (weight * 13.8) + (5 * height) - (6.8 * age);
    }

    private double tmbResponse(double tmb) {
       int index = spinner.getSelectedItemPosition();

       switch (index) {
           case 0 : return tmb * 1.2;
           case 1 : return tmb * 1.375;
           case 2 : return tmb * 1.55;
           case 3 : return tmb * 1.725;
           case 4 : return tmb * 1.9;
           default:
               return 0;
       }
    }

    private boolean validate() {
        if (!editWeight.getText().toString().startsWith("0") &&
                !editHeight.getText().toString().startsWith("0") &&
                !editAge.getText().toString().startsWith("0") &&
                !editWeight.getText().toString().isEmpty() &&
                !editHeight.getText().toString().isEmpty() &&
                !editAge.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private void openListCalc() {
        Intent intent = new Intent(this, ListCalcActivity.class);
        intent.putExtra("type", "tmb");
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
            case R.id.menu_list:
                openListCalc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
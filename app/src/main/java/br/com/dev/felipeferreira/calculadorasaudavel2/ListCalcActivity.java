package br.com.dev.felipeferreira.calculadorasaudavel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListCalcActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calc);

        recyclerView = findViewById(R.id.recycler_view_list);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String type = extras.getString("type");

            new Thread(() -> { //Buscando os dados em uma thread separada
                List<Register> registers = SqlHelper.getInstance(this).getRegisterBy(type);

                runOnUiThread(() -> {
                    ListCalcAdapter adapter = new ListCalcAdapter(registers);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    adapter.setListener(new OnItemClickListnerAdapter() {
                        @Override
                        public void onClick(int id, String type) {
                            // verificar qual tipo de dado deve ser EDITADO na tela seguinte
                            switch (type) {
                                case "imc":
                                    Intent intent = new Intent(ListCalcActivity.this, ImcActivity.class);
                                    intent.putExtra("updateId", id);
                                    startActivity(intent);
                                    break;
                                case "tmb":
                                    Intent i = new Intent(ListCalcActivity.this, TmbActivity.class);
                                    i.putExtra("updateId", id);
                                    startActivity(i);
                                    break;
                            }
                        }

                        @Override
                        public void onLongClick(int id, String type, int position) {
                            AlertDialog dialog = new AlertDialog.Builder(ListCalcActivity.this)
                                    .setTitle(R.string.delete_message)
                                    .setPositiveButton(android.R.string.cancel, (cancel, which) -> {
                                    })
                                    .setNegativeButton(R.string.delete, (delete, i) -> {

                                        new Thread(() -> {
                                            SqlHelper sqlHelper = SqlHelper.getInstance(ListCalcActivity.this);
                                            long calcId = sqlHelper.deleteItem(type, id);

                                            runOnUiThread(() -> {
                                                if (calcId > 0) {
                                                    Toast.makeText(ListCalcActivity.this, R.string.calc_removed, Toast.LENGTH_LONG).show();
                                                    registers.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }).start();
                                    }).create();
                            dialog.show();
                        }
                    });
                });
            }).start();
        }
    }

    private class ListCalcAdapter extends RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder> {

        private final List<Register> registers;
        private OnItemClickListnerAdapter listener;

        public ListCalcAdapter(List<Register> registers) {
            this.registers = registers;
        }

        @NonNull
        @Override
        public ListCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ListCalcViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListCalcViewHolder viewHolder, int position) {
            Register register = registers.get(position);
            viewHolder.bind(register);
        }

        @Override
        public int getItemCount() {
            return registers.size();
        }

        public void setListener(OnItemClickListnerAdapter listener) {
            this.listener = listener;
        }


        private class ListCalcViewHolder extends RecyclerView.ViewHolder {

            public ListCalcViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bind(Register register) {
                String format = "";
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", new Locale("pt", "BR"));
                    Date dateSaved = dateFormat.parse(register.createdDate);
                    SimpleDateFormat dateFormatInvertido = new SimpleDateFormat("dd-MM-yyyy / HH:mm:ss", new Locale("pt", "BR"));
                    format = dateFormatInvertido.format(dateSaved);
                } catch (ParseException e) {

                }

                ((TextView) itemView).setText(getString(R.string.list_response, register.name, register.type, register.response, format));

                itemView.setOnClickListener(v -> listener.onClick(register.id, register.type));
                itemView.setOnLongClickListener(v -> {
                    listener.onLongClick(register.id, register.type, getAdapterPosition());
                    return false;
                });
            }
        }

    }
}
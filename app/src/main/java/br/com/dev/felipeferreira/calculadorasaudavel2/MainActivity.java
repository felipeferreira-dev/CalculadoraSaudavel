package br.com.dev.felipeferreira.calculadorasaudavel2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_rv);

        ArrayList<MainItem> mainItems = new ArrayList<>();
        mainItems.add(new MainItem(1, R.drawable.ic_calculate_24, R.string.imc, Color.WHITE));
        mainItems.add(new MainItem(2, R.drawable.ic_fit_calc, R.string.tmb, 0xFFDFDFDF));
        mainItems.add(new MainItem(3, R.drawable.ic_run_24, R.string.teste, 0xFFDFDFDF));
        mainItems.add(new MainItem(4, R.drawable.ic_groups_24, R.string.teste2, 0XFFFFFFFF));


        MainAdapter adapter = new MainAdapter(mainItems);
        recyclerView.setAdapter(adapter);

        adapter.setListener(id -> {
            switch (id) {
                case 1:
                    startActivity(new Intent(MainActivity.this, ImcActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, TmbActivity.class));
                    break;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        private final List<MainItem> mainItems;
        private OnItemClickListener listener;

        public MainAdapter(List<MainItem> mainItems) {
            this.mainItems = mainItems;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.main_item, parent, false);
            return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder viewHolder, int position) {
            MainItem mainItemCurrent = mainItems.get(position);
            viewHolder.bind(mainItemCurrent);
        }

        @Override
        public int getItemCount() {
            return mainItems.size();
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        //View da célula que esta dentro do RecyclerView
        private class MainViewHolder extends RecyclerView.ViewHolder {

            TextView txtName;
            ImageView imgIcon;
            LinearLayout btnImc;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
                btnImc = (LinearLayout) itemView.findViewById(R.id.btn_imc);
                txtName = itemView.findViewById(R.id.item_txt_name);
                imgIcon = itemView.findViewById(R.id.item_img_icon);
            }

            public void bind(MainItem item) {

                btnImc.setOnClickListener(view -> listener.onClick(item.getId()));

                btnImc.setBackgroundColor(item.getColor());
                txtName.setText(item.getTxtStringId());
                imgIcon.setImageResource(item.getDrawableId());
            }
        }
    }
}
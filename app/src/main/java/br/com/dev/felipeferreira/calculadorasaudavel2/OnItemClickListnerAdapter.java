package br.com.dev.felipeferreira.calculadorasaudavel2;

public interface OnItemClickListnerAdapter {
    void onClick(int id, String type);
    void onLongClick(int id, String type, int position);
}

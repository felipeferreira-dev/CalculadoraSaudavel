package br.com.dev.felipeferreira.calculadorasaudavel2;

public class MainItem {

    private int id;
    private int drawableId;
    private int txtStringId;
    private int color;

    public MainItem(int id, int drawableId, int txtStringId, int color) {
        this.id = id;
        this.drawableId = drawableId;
        this.txtStringId = txtStringId;
        this.color = color;
    }

    public int getTxtStringId() {
        return txtStringId;
    }

    public void setTxtStringId(int txtStringId) {
        this.txtStringId = txtStringId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

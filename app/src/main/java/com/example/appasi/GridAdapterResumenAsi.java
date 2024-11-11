package com.example.appasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class GridAdapterResumenAsi extends BaseAdapter {
    private Context context;
    private List<String> textList1;
    private List<String> textList2;
    private List<String> textList3;
    private List<String> textList4;
    private List<String> textList5;

    public GridAdapterResumenAsi(Context context, List<String> textList1, List<String> textList2
            , List<String> textList3, List<String> textList4, List<String> textList5) {
        this.context = context;
        this.textList1 = textList1;
        this.textList2 = textList2;
        this.textList3 = textList3;
        this.textList4 = textList4;
        this.textList5 = textList5;
    }

    @Override
    public int getCount() {
        return textList1.size(); // Número de elementos a mostrar
    }

    @Override
    public Object getItem(int position) {
        return textList1.get(position); // Elemento en la posición específica
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_grid_resumenasi, parent, false);
        }
        TextView textView1 = convertView.findViewById(R.id.column1);
        TextView textView2 = convertView.findViewById(R.id.column2);
        TextView textView3 = convertView.findViewById(R.id.column3);
        TextView textView4 = convertView.findViewById(R.id.column4);
        TextView textView5= convertView.findViewById(R.id.column5);
        textView1.setText(textList1.get(position));
        textView2.setText(textList2.get(position));
        textView3.setText(textList3.get(position));
        textView4.setText(textList4.get(position));
        textView5.setText(textList5.get(position));
        return convertView;
    }
}

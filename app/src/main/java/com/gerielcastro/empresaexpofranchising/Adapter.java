package com.gerielcastro.empresaexpofranchising;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Empresa> {
    private ArrayList<Empresa> empresas;
    private Context context;

    public Adapter(Context c, ArrayList<Empresa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.empresas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if (empresas != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.empresa, parent, false);

            TextView nomeEmpresa = (TextView) view.findViewById(R.id.nome_empresa_text);
            TextView data = (TextView) view.findViewById(R.id.data_text);
            TextView codigo = (TextView) view.findViewById(R.id.codigo_text);
            TextView chave = (TextView) view.findViewById(R.id.key_text);
            TextView email = (TextView) view.findViewById(R.id.email_text_1);

            Empresa empresa2 = empresas.get(position);
            nomeEmpresa.setText(empresa2.getNomeEmpresa());
            data.setText("Data e Hora: "+empresa2.getData());
            codigo.setText("Código: "+empresa2.getCode39());
            chave.setText(empresa2.getmKey());
            email.setText(empresa2.getEmail());
        }
        return view;
    }
}

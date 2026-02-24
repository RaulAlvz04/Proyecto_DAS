package com.example.proyecto_das;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogAnnadirPeli extends DialogFragment {
    ListenerDAP miListener;

    public interface ListenerDAP {
        void alPulsarAnnadir(String titulo, String genero, String anno, float valoracion);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener = (ListenerDAP) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vista = inflater.inflate(R.layout.fragment_dialog_annadir_peli, null);

        EditText etTitulo = vista.findViewById(R.id.etTitulo);
        EditText etAnno = vista.findViewById(R.id.etAnno);
        EditText etGenero = vista.findViewById(R.id.etGenero);
        RatingBar etValoracion = vista.findViewById(R.id.rbValoracion);

        builder.setView(vista).
                setTitle("Introduce los datos de la pelicula").
                setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String titulo = etTitulo.getText().toString();
                        String anno = etAnno.getText().toString();
                        String genero = etGenero.getText().toString();
                        float valoracion = etValoracion.getRating();

                        miListener.alPulsarAnnadir(titulo,genero,anno,valoracion);
                    }
                }).
                setNegativeButton("Cancelar", (dialog,id) -> dialog.dismiss());

        return builder.create();
    }

}

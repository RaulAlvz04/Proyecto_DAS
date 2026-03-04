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

public class DialogBorrarPeli extends DialogFragment {
    private int posicion;
    ListenerDBP miListener;

    public interface ListenerDBP {
        void alConfirmarBorrado(int posicion);
    }

    public DialogBorrarPeli(int posicion) {
        this.posicion = posicion;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener = (ListenerDBP) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.titulo_borrar));
        builder.setMessage(getString(R.string.msg_borrar));

        builder.setPositiveButton(getString(R.string.btn_eliminar), (dialog, id) -> {
            miListener.alConfirmarBorrado(posicion);
        });

        builder.setNegativeButton(getString(R.string.btn_cancelar), (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

}

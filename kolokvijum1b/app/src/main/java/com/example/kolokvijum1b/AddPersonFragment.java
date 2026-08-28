package com.example.kolokvijum1b;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.Slider;

public class AddPersonFragment extends DialogFragment {

    public interface OnPersonAddedListener {
        void onPersonAdded(Person person);
    }

    private OnPersonAddedListener listener;

    public void setOnPersonAddedListener(OnPersonAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_person, null);

        EditText etName = view.findViewById(R.id.etName);
        TextView tvAge = view.findViewById(R.id.tvAge);
        Slider sliderAge = view.findViewById(R.id.sliderAge);
        CheckBox cbActive = view.findViewById(R.id.cbActive);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Zivo azuriranje teksta dok se slider pomera
        sliderAge.addOnChangeListener((slider, value, fromUser) ->
                tvAge.setText("Godine: " + (int) value));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        builder.setTitle("Dodaj osobu");

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Unesi ime i prezime");
                return;
            }
            int age = (int) sliderAge.getValue();
            boolean active = cbActive.isChecked();

            Person person = new Person(name, age, active);
            if (listener != null) {
                listener.onPersonAdded(person);
            }
            dialog.dismiss();
        });

        return dialog;
    }
}
package com.example.chatapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ProfileDialog extends AppCompatDialogFragment {

    private EditText etDialogProfileName, etDialogProfilePhone;
    private ProfileDialogListener listener;
    private User user;

    /*public ProfileDialog(User user){
        this.user = user;
    }*/


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.profile_dialog, null);

        builder.setView(v)
                .setTitle("Update profile")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etDialogProfileName.getText().toString();
                        String phone = etDialogProfilePhone.getText().toString();
                        listener.updateUserInfo(name, phone);
                    }
                });

        etDialogProfileName = v.findViewById(R.id.et_dialog_profile_name);
        etDialogProfilePhone = v.findViewById(R.id.et_dialog_profile_phone);
        /*
        etDialogProfileName.setText(user.getName());
        etDialogProfilePhone.setText(user.getPhoneNumber());*/
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ProfileDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ProfileDialogListener");
        }
    }

    public interface ProfileDialogListener {
        void updateUserInfo(String name, String phone);
    }
}

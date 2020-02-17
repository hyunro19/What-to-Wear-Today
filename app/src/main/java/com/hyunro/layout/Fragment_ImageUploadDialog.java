package com.hyunro.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class Fragment_ImageUploadDialog extends DialogFragment {
    // 각종 뷰 변수 선언

    public Fragment_ImageUploadDialog() {
    }


//    public View OnCreateDialogu(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment__image_upload_dialog, container);
//
//        // 레이아웃 XML과 뷰 변수 연결
//
//        // remove dialog title
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        // remove dialog background
//        getDialog().getWindow().setBackgroundDrawable(
//                new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        return view;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("제목")
                .setItems(0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }
}
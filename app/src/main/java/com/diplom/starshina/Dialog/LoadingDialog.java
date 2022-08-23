package com.diplom.starshina.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.diplom.starshina.R;


public class LoadingDialog {

    private Context context;
    private AlertDialog alertDialog;

    public LoadingDialog(Context myContext) {
        context = myContext;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(LayoutInflater.from(context).inflate(R.layout.loading_layout, null));
        builder.setCancelable(false);



        alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }


    public void dismisDialog() {
        alertDialog.dismiss();
    }
}


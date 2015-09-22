package com.example.tenny.uitest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Tenny on 2015/9/20.
 */
public class Page2_OnClickListener implements View.OnClickListener {
    Context parentContent;
    String producs;

    public Page2_OnClickListener(Context c, String s) {
        parentContent = c;
        producs = s;
    }

    public void onClick(View v) {
        producs.replaceAll("\t", "\n");
        AlertDialog.Builder dialog = new AlertDialog.Builder(parentContent);
        dialog.setTitle("詳細資料");
        dialog.setMessage(producs);
        dialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialoginterface, int i) {
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(1);
                    }
                });
        dialog.show();
    }
}

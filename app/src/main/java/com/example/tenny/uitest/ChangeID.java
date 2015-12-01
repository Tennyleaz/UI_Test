package com.example.tenny.uitest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tenny on 2015/12/1.
 */
public class ChangeID extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_changer);
        ArrayList<String> idArray;
        final Spinner idSelect = (Spinner) findViewById(R.id.numberSelect);
        TextView name = (TextView) findViewById(R.id.name);
        TextView number = (TextView) findViewById(R.id.number);
        Button btn = (Button) findViewById(R.id.button);
        final SharedPreferences settings = getApplicationContext().getSharedPreferences("EC510", 0);
        Log.e("mylog", "change id start");

        // Get from the SharedPreferences
        name.setText("主管查詢");
        number.setText(settings.getString("board_ID", "1"));
        idArray = new ArrayList<String>();
        idArray.add("1");
        idArray.add("2");
        idArray.add("3");
        idArray.add("4");
        ArrayAdapter<String> idAdapter = new ArrayAdapter<String>(ChangeID.this,  android.R.layout.simple_spinner_dropdown_item, idArray);
        idSelect.setAdapter(idAdapter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, ID;
                ID = idSelect.getSelectedItem().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("board_ID", ID);
                // Apply the edits!
                editor.apply();
                Intent intent = new Intent(ChangeID.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}

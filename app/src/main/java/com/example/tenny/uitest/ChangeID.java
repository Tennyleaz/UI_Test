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
    private TextView name, number;
    private Button btn;
    private Spinner idSelect;
    private ArrayAdapter<String> idAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_changer);
        idSelect = (Spinner) findViewById(R.id.numberSelect);
        number = (TextView) findViewById(R.id.number);
        btn = (Button) findViewById(R.id.button);
        Log.e("mylog", "change id start");

        String[] idArray = {"1", "2", "3", "4", "5"};
        idAdapter = new ArrayAdapter<String>(ChangeID.this, android.R.layout.simple_spinner_dropdown_item, idArray);
        idSelect.setAdapter(idAdapter);
        // Get from the SharedPreferences
    }

    @Override
    protected void onStart(){
        super.onStart();
        final SharedPreferences settings = getApplicationContext().getSharedPreferences("EC510", 0);
        number.setText(settings.getString("board_ID", "1"));
        /*ArrayList<String> idArray;
        idArray = new ArrayList<String>();
        idArray.add("1");
        idArray.add("2");
        idArray.add("3");
        idArray.add("4");*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID;
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

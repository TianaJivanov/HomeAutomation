package com.ti.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    ImageView back1;
    Spinner mySpinner;
    ListView listView;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reports);

        back1 = findViewById(R.id.backicon1);
        mySpinner = findViewById(R.id.raportuldorit);
        listView = findViewById(R.id.liste);

        //Alegerea programului
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ReportsActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.rapoarte));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Alegeți raportul dorit")) {
                    return;
                }

                List<String> results = new ArrayList<>();
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Ați selectat: " + item, Toast.LENGTH_SHORT).show();

                if (parent.getItemAtPosition(position).equals("Raport pentru acces")) {
                    results = GetReport("SELECT id, UserId, DateTime FROM intrare");
                }
                if (parent.getItemAtPosition(position).equals("Raport de temperatură")) {
                    results = GetReport("SELECT id, temperatura2, data1 FROM stari_arduino");
                }
                if (parent.getItemAtPosition(position).equals("Raport de lumini")) {
                    results = GetReport("SELECT Id, Stare, DateTime FROM lumini");
                }

                ArrayAdapter<String> listArray = new ArrayAdapter<String>(parent.getContext(), R.layout.listview_item, results);
                listView.setAdapter(listArray);
                listView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReportsActivity.this,ControlActivity.class);
                startActivity(intent);
            }
        });
        //Data si timp
        Thread t = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tdate1 = (TextView) findViewById(R.id.date1);
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy\nhh:mm a");
                                String dateString = sdf.format(date);
                                tdate1.setText(dateString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    private List<String> GetReport(String query) {
        Connection con = DbConnection.connectionclass();
        List<String> results = new ArrayList<>();

        try {
            Statement sql = con.createStatement();
            ResultSet rs = sql.executeQuery(query);

            while (rs.next()) {
                String row = " ";
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row += "---" + rs.getString(i);
                }
                results.add(row);
            }

            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}

package com.ti.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.AutofillValue;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Calendar;


public class LightActivity extends AppCompatActivity {

    private Profil profil = Profil.getInstance();
    TextView textStare;
    Switch aSwitch;
    ImageView back;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_light);

        textStare = findViewById(R.id.textstarelumini);
        aSwitch = findViewById(R.id.switch1);
        back = findViewById(R.id.backicon);


        try {
            boolean st_act=LightActivitySelect();
            if(st_act==true) {
                aSwitch.setChecked(true);
                Toast.makeText(getApplicationContext(), "Lumina este aprinsă!", Toast.LENGTH_LONG).show();
            }
            else {
                aSwitch.setChecked(false);
                Toast.makeText(getApplicationContext(), "Lumina este stinsă!", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (aSwitch.isChecked()) {

                        String input1 = textStare.getText().toString();
                        LightActivityInsert(1);
                        //LightActivitySelect();
                        textStare.setText("Lumina se va aprinde!");
                    } else {

                        LightActivityInsert(0);
                        //LightActivitySelect();
                        textStare.setText("Lumina se va stinge!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LightActivity.this,ControlActivity.class);
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
                                TextView tdate2 = (TextView) findViewById(R.id.date2);
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy\nhh:mm a");
                                String dateString = sdf.format(date);
                                tdate2.setText(dateString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }

    public boolean LightActivitySelect() throws SQLException {
        Connection con = DbConnection.connectionclass();
        Statement sql;
        sql = con.createStatement();
        boolean ok = false;

        ResultSet rs;
        rs = sql.executeQuery("SELECT TOP 1 s_lumina FROM dbo.stari_arduino ORDER BY Id DESC");

        //PreparedStatement pstmt = sql.prepareStatement(query);
        // pstmt.setInt(1, (int)(System.currentTimeMillis() % 2000000000));
        //pstmt.setString(1, profil.username);

        // rs = sql.executeQuery();

        if(rs.next()) {

            ok = rs.getBoolean("s_lumina");
        }

        sql.close();

        return ok;
    }

    public boolean LightActivityInsert(int stare) throws SQLException {
        Connection sql;
        boolean ok = false;
        sql = DbConnection.connectionclass();

        java.util.Calendar calendar = Calendar.getInstance();
        java.util.Date currentTime = calendar.getTime();

        long time = currentTime.getTime();


        String query = "INSERT INTO dbo.lumini(UserId, Stare, DateTime) VALUES ( ?, ?, ?)";
        PreparedStatement pstmt = sql.prepareStatement(query);
        //pstmt.setInt(1, (int)(System.currentTimeMillis() % 2000000000));
        pstmt.setString(1, profil.username);
        pstmt.setInt(2, stare);
        pstmt.setTimestamp(3, new Timestamp(time));

        int rows = pstmt.executeUpdate();

        if(rows > 0) {
            ok = true;
        }

        sql.close();

        return ok;
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
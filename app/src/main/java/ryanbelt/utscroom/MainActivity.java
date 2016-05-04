package ryanbelt.utscroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ryanbelt.utscroom.JsonWrapper;

public class MainActivity extends AppCompatActivity implements AsyncResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button EmptyRoomButton = (Button)findViewById(R.id.EmptyRoomButton);
        Button RoomScheduleButton = (Button)findViewById(R.id.RoomSchedule);
        final Button Update = (Button)findViewById(R.id.UpdateButton);

        checkDataTime(Update);

        EmptyRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EmptyRoomInput.class));
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateJson(Update);
            }
        });
       // EmptyRoomButton.setOnClickListener();
    }

    @Override
    public void processFinish(String output) {
        Toast toast = Toast.makeText(this,output,Toast.LENGTH_LONG);
        toast.show();
    }

    public void updateJson (Button Update){
        JsonWrapper jsonWrap = new JsonWrapper(MainActivity.this,Update);
        jsonWrap.delegate=MainActivity.this;
        jsonWrap.execute();
        Update.setEnabled(false);
    }

    public void checkDataTime(Button Update){
        try {
            JSONObject jsonObject = new JSONObject(new RoomWraper().jsonReader(MainActivity.this));

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = df.parse(df.format(new Date()));
            String jsonDate = jsonObject.getString("date");
            Date convertedJsonDate = df.parse(jsonDate);

            if(dateobj.after(convertedJsonDate)){
                Toast toast = Toast.makeText(this,"Data outdated",Toast.LENGTH_SHORT);
                toast.show();
                updateJson(Update);
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(this,"File Not Found. Updating",Toast.LENGTH_LONG);
            toast.show();
            updateJson(Update);
        }
    }
}

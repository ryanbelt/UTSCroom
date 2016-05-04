package ryanbelt.utscroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ryanbelt.utscroom.JsonWrapper;

public class MainActivity extends AppCompatActivity implements AsyncResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button EmptyRoomButton = (Button)findViewById(R.id.EmptyRoomButton);
        Button RoomScheduleButton = (Button)findViewById(R.id.RoomSchedule);
        final Button Update = (Button)findViewById(R.id.UpdateButton);
        final JsonWrapper jsonWrap = new JsonWrapper(MainActivity.this,Update);
        jsonWrap.delegate=this;

        EmptyRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EmptyRoomInput.class));
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonWrap.execute();
                Update.setEnabled(false);
            }
        });
       // EmptyRoomButton.setOnClickListener();
    }

    @Override
    public void processFinish(String output) {
        Toast toast = Toast.makeText(this,output,Toast.LENGTH_LONG);
        toast.show();
    }
}

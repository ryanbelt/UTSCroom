package ryanbelt.utscroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button EmptyRoomButton = (Button)findViewById(R.id.EmptyRoomButton);
        Button RoomScheduleButton = (Button)findViewById(R.id.RoomSchedule);

        EmptyRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EmptyRoomInput.class));
            }
        });
       // EmptyRoomButton.setOnClickListener();
    }
}

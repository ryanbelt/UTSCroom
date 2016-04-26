package ryanbelt.utscroom;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by Ryan on 2016-04-25.
 */
public class EmptyRoomInput extends AppCompatActivity {
    private Button startButton;
    private Spinner daySpinner;
    private TimePicker timePick;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_empty_room_input);

        daySpinner = (Spinner)findViewById(R.id.DaySpinner);
        timePick = (TimePicker)findViewById(R.id.TimePicker);
        startButton = (Button)findViewById(R.id.StartButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePick.getCurrentHour();
                int min = timePick.getCurrentMinute();
                int day = daySpinner.getSelectedItemPosition()-1;
                Log.d("myCheck",String.format("%d   %d:%d", day, hour, min));
            }
        });
    }
}

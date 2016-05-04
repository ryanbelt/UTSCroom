package ryanbelt.utscroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ryan on 2016-04-26.
 */
public class EmptyRoomOutput extends AppCompatActivity{
    static String result;
    static String[] weeks={"Monday","Tuesday","Wednesday","Thursday","Friday",
    "Saturday","Sunday"};
    static int day;
    static String hour;
    static String min;
    static String weekDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_room_list);
        Bundle extras = getIntent().getExtras();
        int extraMin= extras.getInt("min");
        int extraHour= extras.getInt("hour");
        day= extras.getInt("day");
        hour = String.valueOf(extraHour);
        if(extraMin<30) {
            min = "00";
        }else{
            min = "30";
        }
        if(extraHour<10){
            hour = "0"+hour;
        }
        if (day==-1){
            todayDay();
        }
        weekDay=weeks[day];

        createTable(emptyRoom().split(";"));


    }

    public String emptyRoom(){
        String roomsStr = "";
        try {
            String current=String.format("%s:%s",hour,min);
            String time;
            Calendar now;
            JSONObject jsonObject = new JSONObject(new RoomWraper().jsonReader(EmptyRoomOutput.this));
            JSONArray jsonArray = jsonObject.getJSONArray("RoomList");
            for(int i=0; i<jsonArray.length();i++){
                JSONObject roomObject = jsonArray.getJSONObject(i);
                now = Calendar.getInstance();
                now.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour));
                now.set(Calendar.MINUTE,Integer.parseInt(min));
                do{
                    String reges;
                    time = new SimpleDateFormat("HH:mm").format(now.getTime());
                    try {
                        JSONArray timeSche = roomObject.getJSONArray(time);
                        JSONObject dayTimeSche = timeSche.getJSONObject(0);
                        reges = dayTimeSche.get(String.valueOf(day)).toString();
                    }catch(org.json.JSONException e){
                        reges="None";
                    }
                    now.add(Calendar.MINUTE, 30);
                    if (!reges.equals("None")) {
                        if(!time.equals(current)){
                            roomsStr += String.format("%s,%s,%s", roomObject.get("name"),time, reges);
                        }else{
                            roomsStr += String.format("%s,N/A,%s", roomObject.get("name"), reges);
                        }
                        break;
                    }
                    if(time.equals("23:30")){
                        roomsStr += String.format("%s,23:30,%s", roomObject.get("name"), reges);
                    }
                }while(!time.equals("23:30"));
                roomsStr+=";";
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        return roomsStr.substring(0,roomsStr.length()-1);
    }

    public void createTable(String[] rooms){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.EmptyTable);
        TableRow tableRow;
        TextView textView;
        int selectColor;
        int backgroundColor;
        //set column title
        textView=(TextView)findViewById(R.id.emptyTitle);
        textView.setText(String.format("%s:    %s:%s", weekDay, hour,min));
        textView=(TextView)findViewById(R.id.emptyTableRooms);
        textView.setPadding(10, 0, 10, 0);
        textView.setText("Rooms");
        textView.setGravity(Gravity.CENTER);
        textView=(TextView)findViewById(R.id.emptyTableTimes);
        textView.setPadding(10, 0, 10, 0);
        textView.setText("Avaliable Time");
        textView.setGravity(Gravity.CENTER);
        textView=(TextView)findViewById(R.id.emptyTableDescription);
        textView.setPadding(10, 0, 10, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setText("Next Class");
        //setup each content
        for(int i=0;i<rooms.length;i++) {
            String[] eachRoom=rooms[i].split(",");
            tableRow = new TableRow(this);
            //color select
            if(eachRoom[1].equals("N/A")){
                selectColor=Color.rgb(200, 0, 0);
            }else{
                selectColor=Color.rgb(34, 139, 34);
            }
            //background color select
            if(i%2==0){
                backgroundColor=Color.rgb(208,208,208);
            }else{
                backgroundColor=Color.WHITE;
            }
            //class room name
            textView = new TextView(this);
            textView.setText(eachRoom[0]);
            textView.setTextSize(13);
            textView.setTextColor(selectColor);
            textView.setPadding(10, 0, 10, 0);
            textView.setGravity(Gravity.CENTER);
            tableRow.addView(textView);

            //class room avaliable time
            textView = new TextView(this);
            textView.setText(eachRoom[1]);
            textView.setTextSize(13);
            textView.setTextColor(selectColor);
            textView.setPadding(10, 0, 10, 0);
            textView.setGravity(Gravity.CENTER);
            tableRow.addView(textView);

            //description
            textView = new TextView(this);
            textView.setText(eachRoom[2]);
            textView.setTextSize(13);
            textView.setTextColor(selectColor);
            textView.setPadding(10, 0, 10, 0);
            textView.setGravity(Gravity.CENTER);
            tableRow.addView(textView);

            tableRow.setBackgroundColor(backgroundColor);
            tableLayout.addView(tableRow);
        }
    }

    public void todayDay(){
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day==1){
            day=6;
        }
        else{
            day-=2;
        }
    }
}

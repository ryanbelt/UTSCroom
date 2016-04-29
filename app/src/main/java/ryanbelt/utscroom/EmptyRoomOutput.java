package ryanbelt.utscroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import ryanbelt.utscroom.OnlineWraper;

/**
 * Created by Ryan on 2016-04-26.
 */
public class EmptyRoomOutput extends AppCompatActivity implements AsyncResponse{
    static String result;
    static String[] weeks={"Monday","Tuesday","Wednesday","Thursday","Friday",
    "Saturday","Sunday"};
    static int day;
    static int hour;
    static int min;
    static String weekDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_room_list);
        Bundle extras = getIntent().getExtras();
        hour = extras.getInt("hour");
        min = extras.getInt("min");
        day= extras.getInt("day");
        if (day==-1){
            todayDay();
        }
        weekDay=weeks[day];
        OnlineWraper wraper = new OnlineWraper();
        try{
            wraper.delegate=this;
            wraper.execute(day,hour,min);
        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void processFinish(String output) {
        //wrap the table content from the website
        if(output.contains("code>")) {
            result = output.substring(output.indexOf("<table>") + 7, output.indexOf("</table>"));
            result = result.replace("<tr>", "").replace("<td>", "").replace("</td></tr>", ";").replace("</td>", ",");
            //make all result as arraylist
            String[] resultList = result.split(";");
            createTable(resultList);
        }else{
            Toast toast = Toast.makeText(this,"Fail: Make sure wifi is on.",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void createTable(String[] rooms){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.EmptyTable);
        TableRow tableRow;
        TextView textView;
        int selectColor;
        int backgroundColor;
        //set column title
        textView=(TextView)findViewById(R.id.emptyTitle);
        if(min<10) {
            textView.setText(String.format("%s:    %d:%s", weekDay, hour, "0" + String.valueOf(min)));
        }else{
            textView.setText(String.format("%s:    %d:%d", weekDay, hour, min));
        }
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
        for(int i=1;i<rooms.length-1;i++) {
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

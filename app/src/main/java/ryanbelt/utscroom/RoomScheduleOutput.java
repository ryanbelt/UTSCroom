package ryanbelt.utscroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ryan on 2016-05-07.
 */
public class RoomScheduleOutput extends AppCompatActivity {
    private final int halfHourDP=40;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_schedule);
        Bundle extras = getIntent().getExtras();
        createTable(extras.getString("room"));

    }

    public void createTable(String roomNumber){
        try {
            String none="{\"0\":\"None\",\"1\":\"None\",\"2\":\"None\","+
                    "\"3\":\"None\",\"4\":\"None\",\"5\":\"None\",\"6\":\"None\"}";
            JSONObject jsonObject = new JSONObject(new RoomWraper().jsonReader(RoomScheduleOutput.this));
            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            //calendar title
            String da = jsonObject.get("date").toString();
            DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            Date date=df.parse(da); // parse your string to date
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            TextView textView= (TextView)findViewById(R.id.CalendarTitle);
            textView.setText(String.format("%s:  %s Json",roomNumber,new SimpleDateFormat("MMM dd").format(now.getTime())));
            //calendar days
            JSONObject weekdate=jsonObject.getJSONObject("weekDate");
            textView = (TextView)findViewById(R.id.text0day);
            textView.setText(String.format("%s\nMon",weekdate.get("0").toString()));
            textView = (TextView)findViewById(R.id.text1day);
            textView.setText(String.format("%s\nTue",weekdate.get("1").toString()));
            textView = (TextView)findViewById(R.id.text2day);
            textView.setText(String.format("%s\nWed",weekdate.get("2").toString()));
            textView = (TextView)findViewById(R.id.text3day);
            textView.setText(String.format("%s\nThu",weekdate.get("3").toString()));
            textView = (TextView)findViewById(R.id.text4day);
            textView.setText(String.format("%s\nFri",weekdate.get("4").toString()));
            textView = (TextView)findViewById(R.id.text5day);
            textView.setText(String.format("%s\nSat",weekdate.get("5").toString()));
            textView = (TextView)findViewById(R.id.text6day);
            textView.setText(String.format("%s\nSun",weekdate.get("6").toString()));

            JSONArray roomList=jsonObject.getJSONArray("RoomList");
            for(int i=0; i<roomList.length();i++) {
                JSONObject roomObject = roomList.getJSONObject(i);
                if(roomObject.get("name").equals(roomNumber)){
                    now = Calendar.getInstance();
                    String time;
                    for(int j=0;j<7;j++) {
                        String lec = "None";
                        String record="";
                        now.set(Calendar.HOUR_OF_DAY,8);
                        now.set(Calendar.MINUTE,0);
                        int dp = 0;
                        LinearLayout dayLayout= (LinearLayout)findViewById(R.id.Linear0Layout);
                        switch(j){
                            case 0:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear0Layout);
                                break;
                            case 1:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear1Layout);
                                break;
                            case 2:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear2Layout);
                                break;
                            case 3:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear3Layout);
                                break;
                            case 4:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear4Layout);
                                break;
                            case 5:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear5Layout);
                                break;
                            case 6:
                                dayLayout = (LinearLayout)findViewById(R.id.Linear6Layout);
                                break;
                        }
                        do {
                            JSONObject dayTimeSche;
                            time = new SimpleDateFormat("HH:mm").format(now.getTime());
                            try {
                                dayTimeSche = roomObject.getJSONObject(time);
                            } catch (org.json.JSONException e) {
                                dayTimeSche = new JSONObject(none);
                            }
                            dp+=halfHourDP;
                            now.add(Calendar.MINUTE, 30);
                            lec = dayTimeSche.get(String.valueOf(j)).toString();
                            if(lec.equals("None")&&record.equals("")){
                                if(now.get(Calendar.MINUTE)==0){
                                    TextView newtext= new TextView(this);
                                    newtext.setBackgroundResource(R.drawable.gray_border);
                                    int pixels = (int) (dp * scale + 0.5f);
                                    newtext.setHeight(pixels);
                                    dayLayout.addView(newtext);
                                    dp=0;
                                }
                            }else{
                                if(record.equals("")){
                                    record=lec;
                                }else{
                                    TextView newtext= new TextView(this);
                                    if(!lec.equals(record)){
                                        dp-=40;
                                        newtext.setBackgroundResource(R.drawable.gray_border_lightblue_back);
                                        int pixels = (int) (dp * scale + 0.5f);
                                        newtext.setHeight(pixels);
                                        newtext.setText(record);
                                        newtext.setTextColor(Color.rgb(153,0,0));
                                        newtext.setGravity(Gravity.CENTER);
                                        dayLayout.addView(newtext);
                                        dp=40;
                                        record="";
                                        if(!lec.equals("None")){
                                            record=lec;
                                        }else{
                                            if(now.get(Calendar.MINUTE)==0){
                                                newtext= new TextView(this);
                                                newtext.setBackgroundResource(R.drawable.gray_border);
                                                pixels = (int) (dp * scale + 0.5f);
                                                newtext.setHeight(pixels);
                                                dayLayout.addView(newtext);
                                                dp=0;
                                            }
                                        }
                                    }
                                    if(time.equals("21:30")){
                                        newtext.setBackgroundResource(R.drawable.gray_border_lightblue_back);
                                        int pixels = (int) (dp * scale + 0.5f);
                                        newtext.setHeight(pixels);
                                        newtext.setText(record);
                                        newtext.setTextColor(Color.rgb(153,0,0));
                                        newtext.setGravity(Gravity.CENTER);
                                        dayLayout.addView(newtext);
                                        dp=0;
                                    }
                                }

                            }



                        } while (!time.equals("21:30"));
                    }
                    break;
                }
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }

    }
}

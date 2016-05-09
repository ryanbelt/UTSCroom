package ryanbelt.utscroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 2016-05-08.
 */
public class RoomScheduleInput extends AppCompatActivity {
    private Button goButton;
    private Spinner buildingSpinner;
    private Spinner roomSpinner;
    private JSONObject jsonObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_input);

        goButton = (Button)findViewById(R.id.tableButton);
        buildingSpinner =(Spinner)findViewById(R.id.BuildingSpinner);
        roomSpinner =(Spinner)findViewById(R.id.RoomNumberSpinner);

        jsonObject = roomContent();
        try {
            JSONArray buildingArray = jsonObject.getJSONArray("allBuilding");
            List<String> buildingList=new ArrayList<String>();
            buildingList.add("--select building--");
            for(int i=0;i<buildingArray.length();i++){
                buildingList.add((String)buildingArray.get(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, buildingList);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            buildingSpinner.setAdapter(adapter);
        }catch(Exception e){
            Toast toast = Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }

        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String selectedValue = arg0.getSelectedItem().toString();
                if (selectedValue.equalsIgnoreCase("--select building--")) {
                    roomSpinnerDefault();
                } else {
                    setRoomSpinner(selectedValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                roomSpinnerDefault();
            }
        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room= roomSpinner.getSelectedItem().toString();
                if(roomSpinner.getSelectedItemPosition()!=0) {
                    Intent i = new Intent(RoomScheduleInput.this, RoomScheduleOutput.class);
                    i.putExtra("room", room);
                    startActivity(i);
                }else{
                    Toast toast = Toast.makeText(RoomScheduleInput.this,"Select Building->Room",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
    public void setRoomSpinner(String selectedValue){
        List<String> roomList = new ArrayList<String>();
        roomList.add("--select room--");
        try {
            JSONArray roomArray = jsonObject.getJSONObject("SplitedRoom").getJSONArray(selectedValue);
            for(int i=0;i<roomArray.length();i++){
                roomList.add((String)roomArray.get(i));
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        ArrayAdapter<String> adpter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, roomList);
        roomSpinner.setAdapter(adpter);
    }

    public void roomSpinnerDefault(){
        List<String> roomList = new ArrayList<String>();
        roomList.add("--select room--");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, roomList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(adapter);
    }
    public JSONObject roomContent(){
        JSONObject returnObject = new JSONObject();
        JSONArray allBuilding = new JSONArray();
        try {
            returnObject.put("SplitedRoom",new JSONObject());
            JSONObject dataObject = new JSONObject(new RoomWraper().jsonReader(RoomScheduleInput.this));
            JSONArray roomList = dataObject.getJSONArray("RoomList");
            for(int i=0;i<roomList.length();i++){
                JSONObject roomObject=roomList.getJSONObject(i);
                String name=roomObject.getString("name");
                String[] nameSplit=name.split("-");
                JSONObject splitedroom=returnObject.getJSONObject("SplitedRoom");
                JSONArray singleBuilding;
                try{
                    singleBuilding= splitedroom.getJSONArray(nameSplit[0]);
                }catch(Exception e){
                    singleBuilding = new JSONArray();
                    allBuilding.put(nameSplit[0]);
                }
                singleBuilding.put(name);
                splitedroom.put(nameSplit[0],singleBuilding);
                returnObject.put("SplitedRoom",splitedroom);
            }
            returnObject.put("allBuilding",allBuilding);
        }catch(Exception e){
            Toast toast = Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }

        return returnObject;
    }
}

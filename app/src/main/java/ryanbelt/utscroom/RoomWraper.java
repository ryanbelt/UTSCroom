package ryanbelt.utscroom;

/**
 * Created by Ryan on 2016-05-03.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.*;

import org.json.*;

import javax.net.ssl.HttpsURLConnection;
public class RoomWraper {
    private final String USER_AGENT = "Chrome/5.0";
    private static final String ROOM_LIST_URL="https://www.utsc.utoronto.ca/~registrar/scheduling/room_schd";
    private static final String AVALIABLE_ROOM_URL="https://intranet.utsc.utoronto.ca/intranet2/RegistrarService";
    public static final String FILE_NAME="utscRoom.json";

    public String jsonReader(Context context) throws Exception{
        FileInputStream fis = context.openFileInput(FILE_NAME);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        isr.close();
        fis.close();
        return sb.toString();
    }

    public String[] scheduleDate(String date){
        Pattern datePat = Pattern.compile("<th>(.*?)<");
        Matcher matcher;
        matcher = datePat.matcher(date);
        List<String> dateList = new ArrayList<String>();
        while(matcher.find()){
            System.out.println("found");
            dateList.add(matcher.group(0));
            System.out.println(matcher.group(0));
        }
        String ret="";
        for(int i=1;i<dateList.size();i++){
            ret+=dateList.get(i).substring(dateList.get(i).length()-3,dateList.get(i).length()-1)+";";
        }
        return ret.replace(" ","").split(";");
    }
    public String jsonFormat(List<String> sceduleList, String[] dates) throws Exception{
        JSONObject jsonObject = new JSONObject();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();
        jsonObject.put("date",df.format(dateobj));
        JSONObject jsonDate = new JSONObject();
        for(int i=0;i<dates.length;i++){
            jsonDate.put(String.valueOf(i),dates[i]);
        }
        jsonObject.put("weekDate",jsonDate);
        JSONArray jsonRoomArray = new JSONArray();
        JSONObject jsonTimeObject;
        JSONObject  jsonMessage;
        for (String room: sceduleList){
            jsonMessage = new JSONObject();
            String[] el = room.split(";");
            jsonMessage.put("name",el[0]);
            for(int i=1; i<el.length;i++){
                jsonTimeObject = new JSONObject();
                String[] sche = el[i].split(",");
                for(int j=1;j<sche.length;j++) {
                    jsonTimeObject.put(String.valueOf(j-1),sche[j]);
                }
                jsonMessage.put(sche[0],jsonTimeObject);
            }
            jsonRoomArray.put(jsonMessage);
        }
        List<String> jsonValues = new ArrayList<String>();
        for (int i = 0; i < jsonRoomArray.length(); i++) {
            jsonValues.add(jsonRoomArray.getString(i));
        }
        Collections.sort(jsonValues);
        jsonRoomArray = new JSONArray();
        for(int i=0;i<jsonValues.size();i++) {
            JSONObject newObj = new JSONObject(jsonValues.get(i));
            jsonRoomArray.put(newObj);
        }
        jsonObject.put("RoomList",jsonRoomArray);
        return jsonObject.toString();
    }

    public String roomScedule(String rawRoom){
        String[] reges_list= rawRoom.split("<tr>");
        int counter=0;

        String roomNumber="";
        //get room name
        Pattern roomNumberPat= Pattern.compile("\"(.*?)\"");
        Matcher matcher = roomNumberPat.matcher(reges_list[0]);
        if(matcher.find()){
            roomNumber=matcher.group(0).substring(1,matcher.group(0).length()-1);
        }

        Pattern rowPat= Pattern.compile("rowspan=\'(.*?)\'");
        Pattern descPat= Pattern.compile(">(.*)");
        Pattern breakPat = Pattern.compile(">(.*)<");
        Matcher inMatcher;

        int[] freeCounter={0,0,0,0,0,0,0};
        String[] desCounter={"","","","","","",""};

        String[] avali;
        String subcontent="";
        int colCounter=1;
        int dayCounter=0;
        for(int i=2;i<reges_list.length;i++){
            avali= reges_list[i].replace("<\\/td>",";").split(";");
            subcontent+=avali[0].substring(4,avali[0].length());
            colCounter=1;
            dayCounter=0;
            while(dayCounter<7){
                subcontent+=",";
                //column is not token
                if(freeCounter[dayCounter]<=0){
                    matcher = rowPat.matcher(avali[colCounter]);
                    desCounter[dayCounter]="";
                    //column just find not empty
                    if(matcher.find()){
                        String row=matcher.group(0);
                        freeCounter[dayCounter]=Integer.parseInt(row.substring(row.indexOf("'")+1,row.length()-1));
                        matcher = descPat.matcher(avali[colCounter]);
                        if(matcher.find()){
                            row=matcher.group(0);
                            desCounter[dayCounter]=row.substring(1,row.length());
                            inMatcher = breakPat.matcher(row);
                            if(inMatcher.find()){
                                row=inMatcher.group(0);
                                desCounter[dayCounter]=row.substring(1,row.length()-1);
                            }
                        }
                        freeCounter[dayCounter]-=1;
                        subcontent+=desCounter[dayCounter];
                    }else{
                        subcontent+="None";
                    }
                    colCounter++;
                }else{
                    freeCounter[dayCounter]-=1;
                    subcontent+=desCounter[dayCounter];
                }
                dayCounter++;
            }
            subcontent+=";";
        }
        return roomNumber+";"+subcontent;


    }


    public String getRoom() throws Exception{
        String room_html=sendPost(ROOM_LIST_URL,"");
        //regular expression are welcome here.
        room_html= room_html.substring(room_html.indexOf("<div id=\"listRooms\""),room_html.indexOf("</table></div>"));
        room_html= room_html.replace(" ","").replace("</td></tr>","<;>");
        String[] rooms_list=room_html.split("<;>");

        String ret="";
        for(String room :rooms_list){
            String[] myroom=room.replace("value=\"","<,>").replace("\"/>","<,>").split("<,>");
            try{
                ret+=myroom[1].replace("%20","-")+",";
            }catch(Exception e){}
        }
        //remove last comma
        ret=ret.substring(0,ret.length()-1);
        return ret;
    }

    // HTTP POST request
    public String sendPost(String url,String urlParams) throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParams);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

}

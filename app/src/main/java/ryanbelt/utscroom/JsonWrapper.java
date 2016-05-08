package ryanbelt.utscroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.text.*;
import java.util.List;

/**
 * Created by Ryan on 2016-05-03.
 */
public class JsonWrapper extends AsyncTask<Void, String, String> {

    public AsyncResponse delegate = null;
    ProgressDialog progDia;
    Context context;
    Button button;
    String AVALIABLE_ROOM_URL="https://intranet.utsc.utoronto.ca/intranet2/RegistrarService";
    RoomWraper roomwrap;
    JsonWrapper(Context context, Button button){
        this.context = context;
        this.button = button;
    }

    @Override
    protected String doInBackground(Void... voids) {
        publishProgress("retrieving data");
        String rooms="";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();
        try {
            rooms=roomwrap.getRoom();
        }catch(Exception e){
            return "Wifi: unable to connect internet";
        }
        String avaliParams=String.format("room=%s&day=%s",rooms,df.format(dateobj));
        String reges="";
        try{
            reges=roomwrap.sendPost(AVALIABLE_ROOM_URL,avaliParams);
        }catch(Exception e){
            return e.toString();
        }

        publishProgress("process data");
        List<String> scheduleList = new ArrayList<String>();
        for(String room : reges.replace("\",\"","\";\"").split(";")){
            scheduleList.add(roomwrap.roomScedule(room));
        }

        String[] dates = roomwrap.scheduleDate(reges.replace("\",\"", "\";\"").split(";")[0].split("<tr>")[1]);

        publishProgress("writing data file");
        try {
            String content = roomwrap.jsonFormat(scheduleList, dates);
            FileOutputStream outputStream = context.openFileOutput(roomwrap.FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        }catch(Exception e){
            return e.toString();
        }

        return "Success: Data Update";
    }

    @Override
    protected void onPreExecute() {
        roomwrap = new RoomWraper();
        progDia = new ProgressDialog(context);
        progDia.setTitle("Updating data...");
        progDia.setMessage("initial");
        progDia.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        String progress=values[0];
        progDia.setMessage(progress);
    }

    @Override
    protected void onPostExecute(String s) {
        button.setEnabled(true);
        progDia.hide();
        delegate.processFinish(s);
    }
}

package ryanbelt.utscroom;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;
/**
 * Created by Ryan on 2016-04-26.
 */
public class OnlineWraper extends AsyncTask<Integer,Void,String> {
    public AsyncResponse delegate = null;
    private static String kujira_url="http://rooms.kujira.ca/";

    @Override
    protected String doInBackground(Integer... integers) {
        try {
            URL kujira = new URL(kujira_url+String.format("%d/%d%d",integers[0],integers[1],integers[2]));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(kujira.openStream()));
            String inputLine;
            String outputLine="";
            while ((inputLine = in.readLine()) != null)
                outputLine+=inputLine;
            in.close();
            return outputLine;
        }catch(Exception e){
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s);
    }
}

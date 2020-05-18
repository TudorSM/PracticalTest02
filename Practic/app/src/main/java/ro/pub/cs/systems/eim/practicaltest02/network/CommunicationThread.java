package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Timestamp;
import java.util.Date;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            String currency = reader.readLine();

            String rate_float;

            Date date= new Date();
            long time = date.getTime();
            java.sql.Timestamp ts = new Timestamp(time);
            boolean redo = false;
            if (currency.equals(Constants.EUR)) {
                redo = serverThread.shouldUpdateEURRate(ts);
            } else {
                redo = serverThread.shouldUpdateUSDRate(ts);
            }

            if (redo) {
                HttpClient client = new DefaultHttpClient();

                HttpGet request = new HttpGet(Constants.SITE + currency + Constants.ENDING);
                ResponseHandler<String> handler = new BasicResponseHandler();

                String page_source = client.execute(request, handler);

                JSONObject content = new JSONObject(page_source);

                Log.d(Constants.DEBUG, page_source);



                JSONObject bpi = new JSONObject(content.getString("bpi"));
                JSONObject curr = new JSONObject(bpi.getString(currency));
                Log.d(Constants.DEBUG, bpi.getString(currency));

                rate_float = curr.getString("rate");

                if (currency.equals(Constants.EUR)) {
                    serverThread.updateEURRate(rate_float, ts);
                } else {
                    serverThread.updateUSDRate(rate_float, ts);
                }
            } else {
                if (currency.equals(Constants.EUR)) {
                    rate_float = serverThread.getEURRate();
                } else {
                    rate_float = serverThread.getUSDRate();
                }
            }

            writer.println(rate_float);
            writer.flush();

            socket.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}

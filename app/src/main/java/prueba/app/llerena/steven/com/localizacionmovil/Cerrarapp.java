package prueba.app.llerena.steven.com.localizacionmovil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by STEVEN-VAIO on 23/05/2016.
 */
public class Cerrarapp extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long MillisActual = Calendar.getInstance().getTimeInMillis();
        editor.putLong("INICIADO5",MillisActual);
        editor.commit();
        notifyMgr.cancel(1010);

        Cerrar();        finish();
        int p = android.os.Process.myPid();                android.os.Process.killProcess(p);






    }
}

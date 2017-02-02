package prueba.app.llerena.steven.com.localizacionmovil;

import android.os.Bundle;
import android.os.Process;
//import com.google.android.gms.location.places.Place;
import java.util.Calendar;

public class Cerrarapp extends MainActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.editor.putLong("INICIADO5", Calendar.getInstance().getTimeInMillis());
        this.editor.commit();
        //this.notifyMgr.cancel(Place.TYPE_NATURAL_FEATURE);
        Cerrar();
        finish();
        Process.killProcess(Process.myPid());
    }
}

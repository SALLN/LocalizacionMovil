package prueba.app.llerena.steven.com.localizacionmovil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.drive.DriveFile;

public class ReceiverBoot extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(DriveFile.MODE_READ_ONLY);
        context.startActivity(i);
    }
}

package prueba.app.llerena.steven.com.localizacionmovil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by STEVEN-VAIO on 24/05/2016.
 */
public class Signin extends AppCompatActivity {

    NotificationManager notifyMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signin);
        Notificacion();


        aunten = (Button) findViewById(R.id.entrar_boton);
        inc = (TextView) findViewById(R.id.incorrecto);

        nombre = (EditText) findViewById(R.id.nombre_input);
        contra = (EditText) findViewById(R.id.contrasena_input);

        nombre.setText(prefs.getString("Usuario", ""));
        contra.setText(prefs.getString("Contra", ""));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() )
        {
            case R.id.mapa:
            {
                Toast.makeText(this, "Botón mapa pulsado", Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.config:
            {
                Intent intento2 = new Intent(this,MainActivity.class);
                startActivity(intento2);
            }
            break;

            default:
                Toast.makeText(this, "Botón config pulsado", Toast.LENGTH_SHORT).show();

                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void Notificacion() {

        notifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,Cerrarapp.class);
//        intent.setAction(Intent.ACTION_VIEW); // Para lanzar activity al pulsar en la notificacion
        PendingIntent piDismiss = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.marker)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.marker))
                        .setContentTitle("LOCALIZACION MOVIL")
                        .setContentText("SACA SACA SACALO")
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText("SACA SACA SACALO"))
                        .addAction(R.drawable.cerrar,
                                "OFF", piDismiss)
                        //.addAction(R.drawable.androideity2,
                        //      "IGNORAR", null)
                        .setAutoCancel(true);

        Notification notification = mBuilder.build();

        // Construir la notificación y emitirla
        notifyMgr.notify(1010,notification);

    }

}

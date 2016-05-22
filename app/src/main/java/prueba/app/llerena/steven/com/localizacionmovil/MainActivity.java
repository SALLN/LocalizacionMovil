package prueba.app.llerena.steven.com.localizacionmovil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    MyLocationListenerGps LocListenerGps;
    MyLocationListenerRed LocListenerRed;
    LocationManager LocManagerRed;
    LocationManager LocManagerGps;
    Button boton,aunten;
    EditText edit1,edit2,edit3,editurl,nombre,contra;
    TextView estado,inc;
    String Posiciones[][] = new String[500][5];
    String proveedor = null;
    String fecha_gps= null ;
    String hora_gps= null ;
    String posicion=null;
    String conectado="si";
    String mensaje=null;
    String ID_VEHICULO;
    String URLL;
    String respuestaservidor;
    String Usuario;
    String Contrasena;
    boolean Arrancar = true;
    HttpURLConnection con = null;
    URL url = null;
    String longi,Mensaje;
    String textbateria="MENSAJE";
    int Cantidad=1;
    int tiempo_espera = 30*1000;
    int metros_espera = 0; // DEFINICIONES
    SharedPreferences prefs;
    Handler handler;
    boolean Interval=true;
    IntentFilter filter;
    Intent batteryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_signin);
        aunten = (Button) findViewById(R.id.entrar_boton);
        inc = (TextView) findViewById(R.id.incorrecto);
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);

        nombre = (EditText) findViewById(R.id.nombre_input);
        contra = (EditText) findViewById(R.id.contrasena_input);

        nombre.setText(prefs.getString("Usuario", ""));
        contra.setText(prefs.getString("Contra", ""));

        //filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //edit3= (EditText) findViewById(R.id.id_vehiculo);

        //handler = new Handler();
        //handler.postDelayed(updateData,5000);
    }

    public void BotonEntrar(View v2) {

        CheckBox checkk = (CheckBox) findViewById(R.id.recordar_check);
        boolean regis = checkk.isChecked();
        Usuario=nombre.getText().toString();
        Contrasena=contra.getText().toString();

        if(regis) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Usuario", Usuario);
            editor.putString("Contra", Contrasena);
            editor.commit();
        }
        aunten.setText("AUTENTICANDO...");

        IniciarSesion iniciarSesion = new IniciarSesion();
        iniciarSesion.execute();
}

    public void Autenticado(){
        SistemaGeo();
    }

    public void SistemaGeo(){

        setContentView(R.layout.activity_main);
        boton = (Button) findViewById(R.id.botonactivar);
        edit1= (EditText) findViewById(R.id.tiempo);
        edit2= (EditText) findViewById(R.id.distancia);
        edit3= (EditText) findViewById(R.id.id_vehiculo);
        editurl = (EditText) findViewById(R.id.editurl);
        edit3.setText(prefs.getString("ID", ""));

        estado = (TextView) findViewById(R.id.estado);

        LocManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocManagerRed = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocListenerGps = new MyLocationListenerGps();
        LocListenerRed = new MyLocationListenerRed();
    }

    public void ActivarSistema(View v) {

        Interval=false;

        estado.setText("SISTEMA ACTIVADO");
        estado.setBackgroundColor(Color.parseColor("#0bf43d"));

        tiempo_espera = Integer.parseInt(edit1.getText().toString())*1000 ;
        metros_espera = Integer.parseInt(edit2.getText().toString());

        URLL = editurl.getText().toString();
        ID_VEHICULO = edit3.getText().toString();
        edit1.setActivated(false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ID",ID_VEHICULO);
        editor.putString("URL",URLL);
        editor.commit();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED                & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){            return;        }

        LocManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempo_espera, metros_espera, LocListenerGps);

        boolean status_gps = LocManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!status_gps) StartRed();

    }

    public void DesactivarSistema(View vi){

        estado.setText("SISTEMA DESACTIVADO");
        estado.setBackgroundColor(Color.parseColor("#f40b49"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED         && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)         {return;        }

        LocManagerRed.removeUpdates(LocListenerRed);
        LocManagerGps.removeUpdates(LocListenerGps);

    }

    public void PauseRed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED         && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)         {return;        }

        LocManagerRed.removeUpdates(LocListenerRed);
    }

    public void StartRed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)        {return;}

        LocManagerRed.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tiempo_espera, metros_espera, LocListenerRed);

    }

    public void Ejecutar() {

        Enviar_Mysql myTask = new Enviar_Mysql();
        myTask.execute();
    }

    public class MyLocationListenerGps implements LocationListener {

        @Override public void onStatusChanged(String provider, int status, Bundle extras){}
        @Override public void onProviderEnabled(String provider) { PauseRed(); }
        @Override public void onProviderDisabled(String provider) { StartRed();}
        @Override public void onLocationChanged(Location LocGps) {

            ObtenerFecha obtenerfecha = new ObtenerFecha();

            fecha_gps = obtenerfecha.fecha_gps;
            hora_gps = obtenerfecha.hora_gps;
            proveedor ="GPS";
            posicion = LocGps.getLatitude() + ","+ LocGps.getLongitude();
            Ejecutar();
        }
    }

    public class MyLocationListenerRed implements LocationListener {
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) { }
        @Override public void onProviderDisabled(String provider) { }
        @Override public void onLocationChanged(Location LocationRed) {

            ObtenerFecha obtenerfecha = new ObtenerFecha();

            fecha_gps = obtenerfecha.fecha_gps;
            hora_gps = obtenerfecha.hora_gps;
            proveedor = "RED";
            posicion = LocationRed.getLatitude() + "," + LocationRed.getLongitude();
            Ejecutar();
        }
    }

    public class Enviar_Mysql extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute()  {    super.onPreExecute();   }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            onCancelled();  }

        @Override
        protected String doInBackground(String... params) {

            Posiciones[Cantidad][1]=posicion;
            Posiciones[Cantidad][2]=fecha_gps;
            Posiciones[Cantidad][3]=hora_gps;
            Posiciones[Cantidad][4]=proveedor;
            Cantidad++;

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();

                conectado = "si";
            } catch (Exception e) { conectado = "no"; } // VERIF CONEXION

            Arrancar = conectado == "si";

            while (Arrancar) {

                try {
                    String[] posicioness = Posiciones[1][1].split(",");
                    String Latitud_sep = posicioness[0];
                    String Longitud_sep = posicioness[1];

                    String timest = Posiciones[1][2]+" "+Posiciones[1][3]; // hfecha y hora

                    url = new URL(URLL);
                    mensaje = "Id_vehiculo=" + URLEncoder.encode(ID_VEHICULO, "UTF-8")
                            + "&Latitud_gps=" + URLEncoder.encode(Latitud_sep, "UTF-8")
                            + "&Longitud_gps=" + URLEncoder.encode(Longitud_sep, "UTF-8")
                            + "&Fecha_Hora_gps=" + URLEncoder.encode(timest, "UTF-8")
                            + "&Usuario=" + URLEncoder.encode(Usuario, "UTF-8")
                            + "&Proveedor=" + URLEncoder.encode(Posiciones[1][4], "UTF-8");
                    assert url != null;
                    con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true); // Activar método POST
                    con.setFixedLengthStreamingMode(mensaje.getBytes().length); // Tamaño previamente conocido
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  // Establecer application/x-www-form-urlencoded debido al formato clave-valor

                } catch (IOException e) { } // PUBLICACION POSICION PHP PARTE 1

                try {
                    assert con != null;
                    OutputStream out = new BufferedOutputStream(con.getOutputStream());
                    assert mensaje != null;
                    out.write(mensaje.getBytes());
                    out.flush();
                    out.close();
                    con.disconnect();

                } catch (IOException e) { } // PUBLICACION POSICION PHP PARTE 2   OUT.

                Cantidad--;

                for (int j = 1; j <= Cantidad - 1; j++) {
                    Posiciones[j][1] = Posiciones[j + 1][1];
                    Posiciones[j][2] = Posiciones[j + 1][2];
                    Posiciones[j][3] = Posiciones[j + 1][3];
                    Posiciones[j][4] = Posiciones[j + 1][4];
                }



                if (Cantidad == 1) Arrancar = false;

            }// WHiLE

            return null;
        }

        @Override
        protected void onCancelled()  {  super.onCancelled();   }
    }

    public class IniciarSesion extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute()  {    super.onPreExecute();   }

        @Override
        protected void onPostExecute(String result) {super.onPostExecute(result);
            if (respuestaservidor.equals("OK")){
                Autenticado();
            }else{
                inc.setText("Usuario o contraseña Incorrectos");
                aunten.setText("AUTENTICAR");
            }
            onCancelled();  }

        @Override
        protected String doInBackground(String... params) {

            try {

                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://ticollcloud.ddns.net/AWS/Android_Autenticar.php").openConnection());

                mensaje = "Usuario=" + URLEncoder.encode(Usuario, "UTF-8")
                        + "&Contrasena=" + URLEncoder.encode(Contrasena, "UTF-8");

                urlc.setDoOutput(true); // Activar método POST
                urlc.setFixedLengthStreamingMode(mensaje.getBytes().length); // Tamaño previamente conocido
                urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  // Establecer application/x-www-form-urlencoded debido al formato clave-valor

                OutputStream out = new BufferedOutputStream(urlc.getOutputStream());
                out.write(mensaje.getBytes());
                out.flush();

                InputStream is2 = urlc.getInputStream();
                int ch2;
                StringBuffer b2 =new StringBuffer();
                while( ( ch2 = is2.read() ) != -1 ){
                    b2.append( (char)ch2 );
                }
                respuestaservidor=b2.toString();
                out.close();
                urlc.disconnect();

            } catch (IOException e) { } // PUBLICACION POSICION PHP PARTE 1
            return null;
        }

        @Override
        protected void onCancelled()  {  super.onCancelled();   }
    }

    public void Click_Imagen(View view){

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("About");
        dialogo1.setMessage("Built on February 15, 2016\nVersion 1.0\nDeveloped By: Steven Llerena\nEmail: stevenllerena@hotmail.com");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            }
        });
        dialogo1.show();
    }

    private Runnable updateData = new Runnable(){

        public void run(){
            Log.d("MENSAJE","ENTRA EN RUNNABLE");
            batteryStatus = registerReceiver(null, filter);

            int nivel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int voltaje = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int acodc =  batteryStatus.getIntExtra(String.valueOf(BatteryManager.EXTRA_PLUGGED), -1);
            textbateria = acodc+"---"+nivel+"---"+voltaje;
            texto_ombe();
            if (Interval){ handler.postDelayed(updateData,1000); }    else { Interval=true; }
        }
    };

public void texto_ombe(){    edit3.setText(textbateria);    }

}


package prueba.app.llerena.steven.com.localizacionmovil;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback    {

    private GoogleMap mMap;
    MyLocationListenerGps LocListenerGps;           MyLocationListenerRed LocListenerRed;
    LocationManager LocManagerGps,       LocManagerRed;
    Button boton_activar,aunten;
    View dividerview;
    EditText edit_tiempo,edit_distancia,edit_idvehiculo,nombre,contra;
    TextView text_estado,inc,textrespuesta,fechacoordenada;
    String Posiciones[][] = new String[500][5];
    String proveedor = null, fecha_gps= null, hora_gps= null, posicion=null, conectado="si", mensaje=null;
    String ID_VEHICULO, respuestaservidor="",  respuestaservidor_vehiculos="", Usuario, Contrasena;
    String longi,Mensaje, textbateria="MENSAJE";
    String[] strArray, Datos_Vehiculo;
    String Vehiculo = null;
    String ReporteCoordenadas = "http://ticollcloud.ddns.net/AWS/Android_Coordenadas.php";
    String Autenticar= "http://ticollcloud.ddns.net/AWS/Android_Autenticar.php";
    String CargarUsuarios = "http://ticollcloud.ddns.net/AWS/MySQL/Vehicles_User.php";
    String ConsultaCoordenadas = "http://ticollcloud.ddns.net/AWS/MySQL/ConsultaMarker_APP.php";
    int duracion=300, Vista=1, Contador_layouts=1;
    int Cantidad=1, tiempo_espera = 30*1000, metros_espera = 0;
    float init_x;
    HttpURLConnection con = null;       URL url = null;
    SharedPreferences prefs;    SharedPreferences.Editor editor;
    Handler handler;     IntentFilter filter;    Intent batteryStatus;
    boolean Interval=true, Inicio_Sesion=false, Sistema_Activado=false, MarkerGoogle = false, Arrancar = true;
    NotificationManager notifyMgr;
    ViewFlipper vf;
    RelativeLayout.LayoutParams Params_text;
    RelativeLayout.LayoutParams Params_check;
    RelativeLayout.LayoutParams Params_Selec;
    RelativeLayout layout;
    Switch swit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        editor = prefs.edit();
//        long MillisActual = Calendar.getInstance().getTimeInMillis();
//        long MillisViejos = prefs.getLong("INICIADO5",(long) 0);
//
//        if (MillisActual-MillisViejos<500) {
//        finish();
//        int p = android.os.Process.myPid();
//        android.os.Process.killProcess(p);        }

        setContentView(R.layout.flipper);


        //Notificacion();

        //IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //registerReceiver(battery_receiver, filter);
        //handler = new Handler();
        //handler.postDelayed(updateData2,500);

        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        vf.setOnTouchListener(new ListenerTouchViewFlipper());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Inicializar();
        BotonEntrar(null);

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        MarkerGoogle = false;
        swit.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        mMap = map;
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));

    }

    private Runnable updateData = new Runnable(){

        public void run(){
            if (MarkerGoogle){
            CargarCoordenadas cargar = new CargarCoordenadas();
            cargar.execute();
            handler.postDelayed(updateData,2000);       }
        }
    };

    private class ListenerTouchViewFlipper implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: //Cuando el usuario toca la pantalla por primera vez
                    init_x=event.getX();
                    return true;
                case MotionEvent.ACTION_UP: //Cuando el usuario deja de presionar//
                    float distance =init_x-event.getX();
                    if(distance<-100)
                    {
                        vf.setInAnimation(inFromLeftAnimation());
                        vf.setOutAnimation(outToRightAnimation());
                        vf.showPrevious();
                        if (Vista==3 || Vista==2){     Vista--;   }else{    Vista=3;    }
                    }

                    if(distance>100)
                    {
                        vf.setInAnimation(inFromRightAnimation());
                        vf.setOutAnimation(outToLeftAnimation());
                        vf.showNext();
                        if (Vista==1 || Vista==2){     Vista++;   }else{    Vista=1;    }
                    }

                default:
                    break;
            }
            return false;
        }
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f );

        inFromRight.setDuration(duracion);
        inFromRight.setInterpolator(new AccelerateInterpolator());

        return inFromRight;

    }
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(duracion);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(duracion);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(duracion);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    public void BotonEntrar(View v2) {

        if(!Inicio_Sesion) {
            CheckBox checkk = (CheckBox) findViewById(R.id.recordar_check);
            boolean regis = checkk.isChecked();
            Usuario = nombre.getText().toString();
            Contrasena = contra.getText().toString();

            if (regis) {
                editor.putString("Usuario", Usuario);
                editor.putString("Contra", Contrasena);
                editor.commit();
            }
            aunten.setText("INICIANDO SESION...");

            IniciarSesion iniciarSesion = new IniciarSesion();
            iniciarSesion.execute();
        }else{
            if (Sistema_Activado){                ActivarSistema(null);            }

            aunten.setText("INICIAR SESION");
            aunten.setBackgroundColor(Color.parseColor("#E91E63"));
            Inicio_Sesion=false;
            nombre.setEnabled(true);
            contra.setEnabled(true);
        }

}

    public void ActivarSistema(View v) {

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return;}
    Sistema_Activado=!Sistema_Activado;

        if (Sistema_Activado) {

            if (Inicio_Sesion) {
                boton_activar.setText("DESACTIVAR SISTEMA");
                boton_activar.setBackgroundColor(Color.parseColor("#0bf43d"));

        tiempo_espera = Integer.parseInt(edit_tiempo.getText().toString()) * 1000;
        metros_espera = Integer.parseInt(edit_distancia.getText().toString());

        ID_VEHICULO = edit_idvehiculo.getText().toString();

        editor.putString("ID", ID_VEHICULO);
        editor.commit();

        LocManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, tiempo_espera, metros_espera, LocListenerGps);

        boolean status_gps = LocManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!status_gps) StartRed();
                edit_tiempo.setEnabled(false);
                edit_distancia.setEnabled(false);
                edit_idvehiculo.setEnabled(false);
            }
            else{
                Sistema_Activado=!Sistema_Activado;
                Toast.makeText(getBaseContext(),"Debe iniciar sesion para encender el sistema",Toast.LENGTH_LONG).show();
            }
        }
        else{

            boton_activar.setText("ACTIVAR SISTEMA");
            boton_activar.setBackgroundColor(Color.parseColor("#E91E63"));

            LocManagerRed.removeUpdates(LocListenerRed);
            LocManagerGps.removeUpdates(LocListenerGps);

            edit_tiempo.setEnabled(true);
            edit_distancia.setEnabled(true);
            edit_idvehiculo.setEnabled(true);
        }
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
        @Override public void onProviderDisabled(String provider) {

            StartRed();}
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
                    url = new URL(ReporteCoordenadas);
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
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (respuestaservidor.equals("OK")){
                Inicio_Sesion=true;
                aunten.setText(R.string.cerrar);
                aunten.setBackgroundColor(Color.parseColor("#0bf43d"));
                inc.setText("");
                nombre.setEnabled(false);
                contra.setEnabled(false);
            if (!Sistema_Activado){                ActivarSistema(null);            }

        }else if(respuestaservidor.equals("NO")){
            inc.setText("Usuario o contraseña Incorrectos");
            aunten.setText(R.string.iniciar);
            aunten.setBackgroundColor(Color.parseColor("#E91E63"));
            Inicio_Sesion=false;
        }
            else{
                Toast.makeText(getBaseContext(),"No hay conexión a internet",Toast.LENGTH_LONG).show();
                aunten.setText(R.string.iniciar);
                aunten.setBackgroundColor(Color.parseColor("#E91E63"));
                Inicio_Sesion=false;
            }
            onCancelled();
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(Autenticar).openConnection());

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

    public class CargarVehiculosPHP extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute()  {    super.onPreExecute();   }

        @Override
        protected void onPostExecute(String result) {super.onPostExecute(result);
            textrespuesta.setText(respuestaservidor_vehiculos+"--"+strArray[0]+"--"+strArray[1]);
            CrearLayouts();
            onCancelled();  }

        @Override
        protected String doInBackground(String... params) {

            try {

                HttpURLConnection urlc = (HttpURLConnection) (new URL(CargarUsuarios).openConnection());

                mensaje = "Usuario=" + URLEncoder.encode(Usuario, "UTF-8")
                        + "&App=" + URLEncoder.encode("APP", "UTF-8");

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

                respuestaservidor_vehiculos=b2.toString();
                strArray = respuestaservidor_vehiculos.substring(1,respuestaservidor_vehiculos.length()-1).split(",");
                for (int jl=0;jl<strArray.length;jl++){
                    strArray[jl]=strArray[jl].substring(1,strArray[jl].length()-1);

                }

                out.close();
                urlc.disconnect();

            } catch (IOException e) { } // PUBLICACION POSICION PHP PARTE 1
            return null;
        }

        @Override
        protected void onCancelled()  {  super.onCancelled();   }
    }

    public class CargarCoordenadas extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute()  {    super.onPreExecute();   }

        @Override
        protected void onPostExecute(String result) {super.onPostExecute(result);
           fechacoordenada.setText(respuestaservidor_vehiculos);
            mMap.clear();
            LatLng pos = new LatLng(Float.parseFloat(Datos_Vehiculo[0]),Float.parseFloat(Datos_Vehiculo[1]));
            mMap.addMarker(new MarkerOptions().position(pos).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,17));

            onCancelled();  }

        @Override
        protected String doInBackground(String... params) {

            try {

                HttpURLConnection urlc = (HttpURLConnection) (new URL(ConsultaCoordenadas).openConnection());

                mensaje = "Usuario=" + URLEncoder.encode(Usuario, "UTF-8")
                        + "&Vehiculo=" + URLEncoder.encode(Vehiculo, "UTF-8");

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
                respuestaservidor_vehiculos=b2.toString();
                Datos_Vehiculo = respuestaservidor_vehiculos.substring(1,respuestaservidor_vehiculos.length()-1).split(",");
                out.close();
                urlc.disconnect();

            } catch (IOException e) { } // PUBLICACION POSICION PHP PARTE 1
            return null;
        }

        @Override
        protected void onCancelled()  {  super.onCancelled();   }
    }

    public void CargarVehiculos(View v3){

        CargarVehiculosPHP cargar  = new CargarVehiculosPHP();
        cargar.execute();


    }

    public void CrearLayouts(){

    Params_text = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    Params_check = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    Params_Selec = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv1 = new TextView(this);
        tv1.setText(strArray[Contador_layouts-1]);
        tv1.setId(Integer.parseInt(String.valueOf(Contador_layouts)));
        if (Contador_layouts==1)
        {
            layout = (RelativeLayout) findViewById(R.id.config_mapa);
            Params_text.addRule(RelativeLayout.BELOW, R.id.botoncargar);
            Params_check.addRule(RelativeLayout.BELOW, R.id.botoncargar);
        }
        else
        {
            Params_text.addRule(RelativeLayout.BELOW,Contador_layouts-1);
            Params_check.addRule(RelativeLayout.BELOW,Contador_layouts-1);
        }
        Params_Selec.addRule(RelativeLayout.BELOW,Contador_layouts);
        Params_Selec.setMargins(0,13,0,0);

        Params_text.setMargins(0,30,0,0);
        Params_check.setMargins(10,20,0,0);
        layout.addView(tv1,Params_text);

        CheckBox check = new CheckBox(this);
        check.setId(Integer.parseInt(String.valueOf(Contador_layouts+100)));
        Params_check.addRule(RelativeLayout.RIGHT_OF,Contador_layouts);
        layout.addView(check,Params_check);
        dividerview.setLayoutParams(Params_Selec);
        Params_Selec.height=8;

        if (Contador_layouts<strArray.length) {            Contador_layouts++;            CrearLayouts();        }
    }

    public void MarkerONOFF(View vdf){

        swit = (Switch) findViewById(R.id.switch1);

        if(swit.isChecked()){

            int Contador=101;
            boolean bol = true;

            while(bol) {
                CheckBox choricera = (CheckBox) findViewById(Integer.parseInt(String.valueOf(Contador)));
                bol = !choricera.isChecked();
                Contador++;
            }
            if (!bol){            Vehiculo=strArray[Contador-102];        }

            handler = new Handler();
            MarkerGoogle = true;
            handler.postDelayed(updateData,500);
        }
        else{
            MarkerGoogle=false;

        }
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
                        .setContentText("parte superior")
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText("parte inferior"))
                        .addAction(R.drawable.cerrar,
                                "SWITCH OFF", piDismiss)
                        //.addAction(R.drawable.androideity2,
                          //      "IGNORAR", null)
                        .setAutoCancel(true);

        Notification notification = mBuilder.build();

        // Construir la notificación y emitirla
        notifyMgr.notify(1010,notification);

    }

    public void Cerrar(){        finish();    }

    public void Inicializar(){

        aunten = (Button) findViewById(R.id.entrar_boton);
        dividerview = (View) findViewById(R.id.divider34);
        inc = (TextView) findViewById(R.id.incorrecto);

        nombre = (EditText) findViewById(R.id.nombre_input);
        contra = (EditText) findViewById(R.id.contrasena_input);

        nombre.setText(prefs.getString("Usuario", ""));
        contra.setText(prefs.getString("Contra", ""));

        boton_activar = (Button) findViewById(R.id.botonactivar);
        edit_tiempo= (EditText) findViewById(R.id.tiempo);
        edit_distancia= (EditText) findViewById(R.id.distancia);
        edit_idvehiculo= (EditText) findViewById(R.id.id_vehiculo);
        edit_idvehiculo.setText(prefs.getString("ID", "Steven"));

        textrespuesta = (TextView) findViewById(R.id.respuesta);

        swit = (Switch) findViewById(R.id.switch1);
        fechacoordenada = (TextView) findViewById(R.id.fechacoordenada);
        LocManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocManagerRed = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocListenerGps = new MyLocationListenerGps();
        LocListenerRed = new MyLocationListenerRed();
    }

    public void texto_ombe(){    edit_idvehiculo.setText(textbateria);    }

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

            case R.id.activity_main:
                if (Vista==3){
                    vf.setInAnimation(inFromRightAnimation());
                    vf.setOutAnimation(outToLeftAnimation());
                    vf.showNext();
                }else if(Vista==2){
                    vf.setInAnimation(inFromLeftAnimation());
                    vf.setOutAnimation(outToRightAnimation());
                    vf.showPrevious();
                }
                Vista=1;
                break;

            case R.id.mapa:
                if (Vista==1){
                    vf.setInAnimation(inFromRightAnimation());
                    vf.setOutAnimation(outToLeftAnimation());
                    vf.showNext();
                }else if(Vista==3){
                    vf.setInAnimation(inFromLeftAnimation());
                    vf.setOutAnimation(outToRightAnimation());
                    vf.showPrevious();
                }
                Vista=2;
                break;

            case R.id.config_mapa:
                if (Vista==1){
                    vf.setInAnimation(inFromLeftAnimation());
                    vf.setOutAnimation(outToRightAnimation());
                    vf.showPrevious();
                }else if(Vista==2){
                    vf.setInAnimation(inFromRightAnimation());
                    vf.setOutAnimation(outToLeftAnimation());
                    vf.showNext();
                }
                Vista=3;
            break;





            default:
                Toast.makeText(this, "Botón config pulsado", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable updateData2 = new Runnable(){

        public void run(){
            handler.postDelayed(updateData2,1000);
        }
    };

    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int plugged = intent.getIntExtra("plugged", -1);
            if (plugged==1){

               // Toast.makeText(getBaseContext(),"ENCHUFADO CON AC",Toast.LENGTH_SHORT).show();
            }else {
                //Toast.makeText(getBaseContext(),"ENCHUFADO CON DC O NO ENCHUFADO",Toast.LENGTH_SHORT).show();
            }
            int rawlevel = intent.getIntExtra("level", -1);
            int level = 0;

            Bundle bundle = intent.getExtras();

            //Log.i("BatteryLevel", bundle.toString());


        }
    };

}


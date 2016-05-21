package prueba.app.llerena.steven.com.localizacionmovil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by STEVEN_LLERENA on 17/01/2016.
 */

 public class ObtenerFecha {

    Calendar c = Calendar.getInstance();

    private SimpleDateFormat ano = new SimpleDateFormat("y");
    private SimpleDateFormat mes = new SimpleDateFormat("M");
    private SimpleDateFormat dia = new SimpleDateFormat("d");
    private SimpleDateFormat hora = new SimpleDateFormat("HH");
    private SimpleDateFormat minuto = new SimpleDateFormat("mm");
    private SimpleDateFormat segundo = new SimpleDateFormat("ss");

    private int ano_actual = Integer.parseInt(ano.format(c.getTime()));
    private int mes_actual = Integer.parseInt(mes.format(c.getTime()));
    private int dia_actual = Integer.parseInt(dia.format(c.getTime()));
    private int hora_actual = Integer.parseInt(hora.format(c.getTime()));
    private int minuto_actual = Integer.parseInt(minuto.format(c.getTime()));
    private int segundo_actual = Integer.parseInt(segundo.format(c.getTime()));

    public String fecha_gps = ano_actual + "-" + mes_actual + "-" + dia_actual;
    public String hora_gps = hora_actual + ":" + minuto_actual + ":" + segundo_actual;

}

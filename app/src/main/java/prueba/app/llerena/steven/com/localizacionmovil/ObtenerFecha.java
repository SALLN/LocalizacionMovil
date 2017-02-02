package prueba.app.llerena.steven.com.localizacionmovil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ObtenerFecha {
    private SimpleDateFormat ano;
    private int ano_actual;
    Calendar f3c;
    private SimpleDateFormat dia;
    private int dia_actual;
    public String fecha_gps;
    private SimpleDateFormat hora;
    private int hora_actual;
    public String hora_gps;
    private SimpleDateFormat mes;
    private int mes_actual;
    private SimpleDateFormat minuto;
    private int minuto_actual;
    private SimpleDateFormat segundo;
    private int segundo_actual;

    public ObtenerFecha() {
        this.f3c = Calendar.getInstance();
        this.ano = new SimpleDateFormat("y");
        this.mes = new SimpleDateFormat("M");
        this.dia = new SimpleDateFormat("d");
        this.hora = new SimpleDateFormat("HH");
        this.minuto = new SimpleDateFormat("mm");
        this.segundo = new SimpleDateFormat("ss");
        this.ano_actual = Integer.parseInt(this.ano.format(this.f3c.getTime()));
        this.mes_actual = Integer.parseInt(this.mes.format(this.f3c.getTime()));
        this.dia_actual = Integer.parseInt(this.dia.format(this.f3c.getTime()));
        this.hora_actual = Integer.parseInt(this.hora.format(this.f3c.getTime()));
        this.minuto_actual = Integer.parseInt(this.minuto.format(this.f3c.getTime()));
        this.segundo_actual = Integer.parseInt(this.segundo.format(this.f3c.getTime()));
        this.fecha_gps = this.ano_actual + "-" + this.mes_actual + "-" + this.dia_actual;
        this.hora_gps = this.hora_actual + ":" + this.minuto_actual + ":" + this.segundo_actual;
    }
}

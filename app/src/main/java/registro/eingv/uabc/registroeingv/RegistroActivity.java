package registro.eingv.uabc.registroeingv;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import registro.eingv.uabc.registroeingv.db.DaoMaster;
import registro.eingv.uabc.registroeingv.db.Registro;
import registro.eingv.uabc.registroeingv.lista.ListActivity;


public class RegistroActivity extends ActionBarActivity implements LocationListener,View.OnClickListener,TextToSpeech.OnInitListener {
    private LocationManager locationManager;
    private ArrayList<Float> puntos;
    private EditText  descripcion;
    private TextView coordenadasText;
    private Button guardarBoton,botonMap;
    private EditText nombreLugar;

    private Intent intent;
    final static int constante=0;
    private Bitmap bmp=null;
    private GoogleMap mapa=null;
    private int vista=0;
    private TextToSpeech engine;
    private String frase;
    private ImageView imageView;
    private  byte[] byteArray=null;


    //Insertar datos de Registro
    Registro registro=new Registro();


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombreLugar= (EditText) findViewById(R.id.nombreLugarId);
        descripcion= (EditText) findViewById(R.id.descripcionId);
        coordenadasText=(TextView)findViewById(R.id.textViewCordenadas);
        guardarBoton= (Button) findViewById(R.id.buttonRegistro);
        guardarBoton.setOnClickListener(this);
        botonMap= (Button) findViewById(R.id.botonMap);
        botonMap.setOnClickListener(this);
        engine = new TextToSpeech(this, this);





        initDataBase();
    //    List<Registro> reg=SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();
    //for(Registro registro:reg){
    //System.out.println(registro.getLugar());
    //}
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 2, this);
        }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonRegistro:regiss();break;
            case R.id.botonMap: todd();break;
        }
    }

    public  void  regiss(){
        //Validar y llenar

      //  Bitmap imagenAndroid = BitmapFactory.decodeResource(getResources(), R.drawable.uabc);

        //ImageView imageView= (ImageView) findViewById(R.id.imID);

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
  //      imagenAndroid.compress(Bitmap.CompressFormat.PNG,0,stream);

    //    byte[] bitmapdata = stream.toByteArray();//EN bitmapdata se encuentra almacenada la imagen en biytes

   //     Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
     //   imageView.setImageBitmap(bitmap);


        if(puntos!= null){
            if(!puntos.isEmpty()) {
                if(nombreLugar.getText().length()>=2 && descripcion.getText().length() > 2 ) {

                    toma();//toma foto e inserta en la base de datos

                }else{
                    miNotificacion("Ingresa un nombre mayor a 5 caracteres",Toast.LENGTH_SHORT);
                }
            }else{
                //Se crea una notificacion
                miNotificacion("Espera que carguen las coordenadas",Toast.LENGTH_SHORT);
            }
        }else{
            miNotificacion("Espera que carguen las coordenadas",Toast.LENGTH_SHORT);
        }
    }
    private void todd() {
        Intent intent=new Intent(getApplicationContext(),ActivityMaps.class);
        startActivity(intent);
    }
    private void toma() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, constante);
    }

        protected void onActivityResult(int request, int resultCode, Intent data) {
            super.onActivityResult(request, resultCode, data);


            if (resultCode== Activity.RESULT_OK){
                List<Registro> lista;
                lista=SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();
                System.out.printf(String.valueOf(lista.size())+" dato ");
                int contador=0;
                contador=lista.size() + 1;

                Bundle ext=data.getExtras();
                bmp=(Bitmap)ext.get("data");//en bmp se localiza la foto tomada
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG,0,stream);
                byteArray=stream.toByteArray();
                //valida que byteArray este lleno para que se almacene en la base de datos,
                //ya que si se almacena la imagen en la base como null causa error al visualizar
                if (byteArray!=null){
                    registro.set_id((long) contador);
                    registro.setImagen(byteArray);
                    registro.setLugar(nombreLugar.getText().toString());
                    registro.setDescripcion(descripcion.getText().toString());
                    registro.setLatitud(puntos.get(0));
                    registro.setLongitud(puntos.get(1));
                    SingletonDB.getInstance().getDaoSession().getRegistroDao().insert(registro);
                    miNotificacion("Dato almacenado", Toast.LENGTH_SHORT);
                    nombreLugar.setText("");
                }

            }

        }




    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    public void miNotificacion(String texto, int duracion){
        Toast toast = Toast.makeText(this.getApplicationContext(),
                texto, duracion);
        //Aqui se muestra la notificacion
        toast.show();
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Deseas habilitar tu GPS?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onLocationChanged(Location location) {

        coordenadasText.setText("Latitud: [" + location.getLatitude() + "]\n Longitud: [" + location.getLongitude() +" ]");//+ "]\n Altitud: [" + location.getAltitude() + "]");
        puntos=new ArrayList<>();
        puntos.add((float) location.getLatitude());
        puntos.add((float) location.getLongitude());
       // puntos.add((float) location.getAltitude());&& (puntos.get(1)<=(-115.068))



        //Estacionamiento
        if((puntos.get(0)<=(32.30268)) && (puntos.get(1)<=(-115.07672)))
        {
            speech("Estas en el estacionamiento");
            miNotificacion("Estas en el estacionamiento ",Toast.LENGTH_LONG);
        }

        ////lAB LIS/////
        //Latitu 32.303064 Longitud -115.076603
        if (puntos.get(0)<=32.303084 && (puntos.get(0)>=32.303044) && (puntos.get(1)<=-115.076623) &&(puntos.get(1)>=-115.076580)){
            speech("Estas en Laboratorio de ingenieria de software ");
            miNotificacion("Estas en Laboratorio de ingenieria de software ",Toast.LENGTH_LONG);
        }
        //Sala de Musica//// Latitud 32.302205, -115.075982
        if (puntos.get(0)<=32.302225 && (puntos.get(0)>=32.302185) && (puntos.get(1)<=-115.076000) &&(puntos.get(1)>=-115.075960)){
            speech("Estas en el Salon de Musica");
            miNotificacion("Estas en el Salon de Musica",Toast.LENGTH_LONG);
        }
        //salon de Danza// //32.302272, -115.075939
        if (puntos.get(0)<=32.302292 && (puntos.get(0)>=32.302262) && (puntos.get(1)<=-115.075950) &&(puntos.get(1)>=-115.075920)){
            speech("Estas en El salon de Danza");
            miNotificacion("Estas en El salon de Danza",Toast.LENGTH_LONG);
        }
        //Laboratorio B// 32.302295, -115.076434
        if (puntos.get(0)<=32.302310 && (puntos.get(0)>=32.302275) && (puntos.get(1)<=-115.076454) &&(puntos.get(1)>=-115.076414)){
            speech("Estas en el laboratorio B");
            miNotificacion("Estas en el Laboratorio B ",Toast.LENGTH_LONG);
        }
        //Laboratorio de Ciencias Basicas 32.302785,-115.076254
        if (puntos.get(0)<=32.302730 && (puntos.get(0)>=32.302265) && (puntos.get(1)<=-115.076274) &&(puntos.get(1)>=-115.076234)){
            speech("Estas en Laboratorio de Ciencias Basicas");
            miNotificacion("Estas en Laboratorio de Ciencias Basicas",Toast.LENGTH_LONG);
        }
        //Sala de Maestros 32.303010, -115.076273
        if (puntos.get(0)<=32.302730 && (puntos.get(0)>=32.302265) && (puntos.get(1)<=-115.076274) &&(puntos.get(1)>=-115.076234)){
            speech("Estas en Sala de Maestros");
            miNotificacion("Estas en Sala de Maestros",Toast.LENGTH_LONG);

        }   //Cafeteria 32.303146,-115.076789
        if (puntos.get(0)<=32.303166 && (puntos.get(0)>=32.303146) && (puntos.get(1)<=-115.076805) &&(puntos.get(1)>=-115.076760)){
            speech("Estas en la Cafeteria");
            miNotificacion("Estas en la Cafeteria",Toast.LENGTH_LONG);
        }
        //CEDEM 32.302946,-115.076679
        if (puntos.get(0)<=32.302966 && (puntos.get(0)>=32.302926) && (puntos.get(1)<=-115.076699) &&(puntos.get(1)>=-115.076660)){
            speech("Estas en CEDEM");
            miNotificacion("Estas en CEDEM",Toast.LENGTH_LONG);
        }
        //Estacionamiento 32.302790,-115.077101
        if (puntos.get(0)<=32.302810 && (puntos.get(0)>=32.302770) && (puntos.get(1)<=-115.077120) &&(puntos.get(1)>=-115.077080)){
            speech("Estas en el Estacionamiento");
            miNotificacion("Estas en el Estacionamiento",Toast.LENGTH_LONG);
        }
        //Canchas deportivas 32.300680 -155.072767
        if (puntos.get(0)<=32.300699 && (puntos.get(0)>=32.300660) && (puntos.get(1)<=-115.072789) &&(puntos.get(1)>=-115.072747)){
            speech("Estas en las Canchas Deportivas");
            miNotificacion("Estas las Canchas Deportivas",Toast.LENGTH_LONG);
        }



    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status){
            case LocationProvider.AVAILABLE:
                System.err.println("AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                System.err.println("OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                System.err.println("TEMPORARILY_UNAVAILABLE");
                break;
        }
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    //Instancia de base de datos
    private static SQLiteDatabase db;
    //Clase ayudante para abrir y crear la base de datos
    SQLiteOpenHelper helper;
    private void initDataBase() {
        helper = new DaoMaster.DevOpenHelper(this, "registro", null);
        db = helper.getWritableDatabase();
        setDb(helper.getWritableDatabase());
    }
    public static SQLiteDatabase getDb() {
        return db;
    }
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }
    @Override
    protected void onPause() {
        if(engine !=null){
            engine.stop();
            engine.shutdown();
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            Locale spanish = new Locale("es", "ES");
           engine.setLanguage(spanish);
           //engine.setLanguage(Locale.ENGLISH);
        }
    }
    private void speech(String frase) {
            //  engine.speak((dato.getDescripcion()).toString(),TextToSpeech.QUEUE_ADD,null); ////lee todos los datos
            //  engine.speak(dato.getLugar().toString(), TextToSpeech.QUEUE_FLUSH,null);
            engine.speak(frase.toString(), TextToSpeech.QUEUE_FLUSH,null);
            engine.stop();
        }
}

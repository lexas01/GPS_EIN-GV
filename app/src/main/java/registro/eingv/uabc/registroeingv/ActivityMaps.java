package registro.eingv.uabc.registroeingv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;
import java.util.Locale;

import registro.eingv.uabc.registroeingv.db.Registro;
import registro.eingv.uabc.registroeingv.dialogo.Dialogo;


public class ActivityMaps extends android.support.v4.app.FragmentActivity implements TextToSpeech.OnInitListener {
    private GoogleMap mapa = null;
    private int vista = 0;
    private TextToSpeech engine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_maps);

        engine = new TextToSpeech(this, this);

        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        amarcador();
        animm();

/*        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                Projection proj = mapa.getProjection();
                Point coord = proj.toScreenLocation(point);

                Toast.makeText(
                        ActivityMaps.this,
                        "Click\n" +
                                "Lat: " + point.latitude + "\n" +
                                "Lng: " + point.longitude + "\n" +
                                "X: " + coord.x + " - Y: " + coord.y, Toast.LENGTH_SHORT).show();
            }
        });*/

        mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                Projection proj = mapa.getProjection();
                Point coord = proj.toScreenLocation(point);

                Toast.makeText(
                        ActivityMaps.this,
                        "Click Largo\n" +
                                "Lat: " + point.latitude + "\n" +
                                "Lng: " + point.longitude + "\n" +
                                "X: " + coord.x + " - Y: " + coord.y,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*mapa.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                Toast.makeText(
                        ActivityMaps.this,
                        "Cambio Camara\n" +
                                "Lat: " + position.target.latitude + "\n" +
                                "Lng: " + position.target.longitude + "\n" +
                                "Zoom: " + position.zoom + "\n" +
                                "Orientacion: " + position.bearing + "\n" +
                                "angulo: " + position.tilt,
                        Toast.LENGTH_SHORT).show();
            }
        });*/

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                     // engine.speak(marker.getSnippet().toString(), TextToSpeech.QUEUE_FLUSH,null);
                Toast.makeText(
                        ActivityMaps.this,
                        "Marcador pulsado:\n" +
                                marker.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.menu_vista:
                alternarVista();
                break;
            case R.id.menu_mover:
                //Crea una lista vacia de Registro
                List<Registro> lista;
                //Obtener la lista de Registros en la BD
                lista = SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();
            for (Registro dato:lista){
            CameraUpdate camUpd1 =
            CameraUpdateFactory.newLatLng(new LatLng(dato.getLatitud(), dato.getLongitud()));
            mapa.moveCamera(camUpd1);
}
                break;
            case R.id.menu_animar:
        animm();
                break;
            case R.id.menu_3d:
               //Crea una lista vacia de Registro
                List<Registro> lista2;
                //Obtener la lista de Registros en la BD
                lista2 = SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();

                for (Registro dato:lista2) {
                LatLng lugar=new LatLng(dato.getLatitud(),dato.getLongitud());
                    CameraPosition camPos = new CameraPosition.Builder()
                    .target(lugar).zoom(19).bearing(45).tilt(70).build();
                    CameraUpdate cameraUpdate=CameraUpdateFactory.newCameraPosition(camPos);
                    mapa.animateCamera(cameraUpdate);
                }

                break;

            /*case R.id.menu_posicion:

               CameraPosition camPos2 = mapa.getCameraPosition();
               LatLng pos = camPos2.target;
                Toast.makeText(ActivityMaps.this,
                        "Lat: " + pos.latitude + " - Lng: " + pos.longitude,
                        Toast.LENGTH_LONG).show();
            break;*/


            //case R.id.menu_marcadores:
              //  amarcador();
                //break;
            case R.id.menu_lineas:
                mostrarLineas();break;
            case R.id.menu_hablar:

               speech();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void animm(){
        //Centramos el mapa en Espania y con nivel de zoom 5
        //Crea una lista vacia de Registro
        List<Registro> lista1;
        //Obtener la lista de Registros en la BD
        lista1 = SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();

        for (Registro dato:lista1)
        {
            CameraUpdate camUpd2 =
                    CameraUpdateFactory.newLatLngZoom(new LatLng(dato.getLatitud(), dato.getLongitud()), 8F);
            mapa.animateCamera(camUpd2);
        }

    }

    public void amarcador(){
        //Crea una lista vacia de Registro
        List<Registro> lista3;
        //Obtener la lista de Registros en la BD
        lista3 = SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();

        Bitmap bitmap;
        //= BitmapFactory.decodeByteArray(this.listaRegistro.get(position).getFoto(), 0, this.listaRegistro.get(position).getFoto().length);//recupero el bitmap

//        foto.setImageBitmap(this.listaRegistro.get(position).getFoto());

        for (Registro dato:lista3){

         bitmap=BitmapFactory.decodeByteArray((dato.getImagen()), 0, (dato.getImagen()).length);

            LatLng re = new LatLng(dato.getLatitud(), dato.getLongitud());
            mapa.addMarker(new MarkerOptions()
                            .title(dato.getLugar())
                            .snippet(dato.getDescripcion())
                            .position(re)
                  // .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

            );
            }
        }
    private void mostrarLineas(){
        PolygonOptions lineas=new PolygonOptions()
        //      .add(new LatLng(registro.getLatitud(),registro.getLongitud()));
        ///lineas.strokeWidth(8);
        ///lineas.strokeColor(Color.RED);
        ///mapa.addPolygon(lineas)
        ;

 }
   private void alternarVista() {
        vista = (vista + 1) % 4;
        switch(vista)
        {
            case 0:
                mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3:
                mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
           }
    }
    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
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
        private void speech() {
            List<Registro> list;
            list=SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();
            for (Registro dato:list){
//                engine.speak((dato.getDescripcion()).toString(),TextToSpeech.QUEUE_ADD,null); ////lee todos los datos
              //  engine.speak(dato.getLugar().toString(), TextToSpeech.QUEUE_FLUSH,null);
                engine.speak(dato.getDescripcion().toString(), TextToSpeech.QUEUE_FLUSH,null);
            }
//                engine.speak(s.toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}

package registro.eingv.uabc.registroeingv;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import registro.eingv.uabc.registroeingv.lista.ListActivity;

public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Obtener el texto del XML
        //final TextView textoEnPantalla = (TextView) findViewById(R.id.textView);
        //Obtener el boton del XML
        engine = new TextToSpeech(this, this);
        Button registrarBoton = (Button)findViewById(R.id.buttonRegistrar);
        Button salirBoton = (Button) findViewById(R.id.buttonSalir);
        Button exportar= (Button) findViewById(R.id.botonExportar);
     //   Button sp = (Button) findViewById(R.id.bSpeech);

        //Agregar un listener al ser presionado
        registrarBoton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cambiarVistaRegistro();
            }
        });
        salirBoton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportarBD();
            }
        });

    }

    private void say() {

        engine.speak(("Hola"), TextToSpeech.QUEUE_FLUSH,null);
        engine.stop();

    }

    public void cambiarVistaRegistro(){

        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);

    }

    public void exportarBD(){

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        final String inFileName = "data/data/registro.eingv.uabc.registroeingv/databases/registro";

        File dbFile = new File(inFileName);

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(dbFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/BASE_DE_DATOS_APP_REGISTRO/";
        Log.e("ruta:", "" + directorio);
        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
        }
        String outFileName = directorio + "registro_"+timeStamp+".sqlite";
        OutputStream output = null;
        try {
            output = new FileOutputStream(outFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Intent enviarArchivo = new Intent();
            File outFile=new File(outFileName);
            enviarArchivo.setAction(Intent.ACTION_SEND);
            enviarArchivo.setType("text/plain");
            enviarArchivo.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outFile));
            startActivity(enviarArchivo);
        }
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
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            Locale spanish = new Locale("es", "ES");
            engine.setLanguage(spanish);
            //engine.setLanguage(Locale.ENGLISH);
        }
    }



}

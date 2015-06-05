package registro.eingv.uabc.registroeingv.lista;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import registro.eingv.uabc.registroeingv.R;
import registro.eingv.uabc.registroeingv.SingletonDB;
import registro.eingv.uabc.registroeingv.db.Registro;

public class ListActivity extends Activity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener{
    private  ListView listView;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.lista);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        context=this.getApplicationContext();

        runOnUiThread(new Runnable() {
            public void run() {
                //Crea una lista vacia de Registro
                List<Registro> lista;
                //Obtener la lista de Registros en la BD
                lista = SingletonDB.getInstance().getDaoSession().getRegistroDao().loadAll();
                if (lista != null) {
                    //Agrega el contexto y lista a la vista ListView
                    ListaRegistroAdapter listaAdapter = new ListaRegistroAdapter(context, lista);
                    listView.setAdapter(listaAdapter);
                } else {
                    lista = new ArrayList<>();
                    ListaRegistroAdapter listaAdapter = new ListaRegistroAdapter(context, lista);
                    listView.setAdapter(listaAdapter);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        ListaRegistroAdapter listaClavesAdapter_ =(ListaRegistroAdapter) adapterView.getAdapter();
        Registro reg = (Registro) listaClavesAdapter_.getItem(i);

       //Intent intent = new Intent(this, DescripcionClave.class).putExtra("registro", reg.get_id());
        //startActivity(intent);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        ListaRegistroAdapter listaClavesAdapter_ =(ListaRegistroAdapter) parent.getAdapter();

        Registro reg = (Registro) listaClavesAdapter_.getItem(position);

        buildAlertMessageConfiguration(reg);

      //Toast toast = Toast.makeText(this.onItemLongClick());
        return false;
    }
    private void buildAlertMessageConfiguration(final Registro reg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Que deceas hacer?")
                .setCancelable(false)
                .setPositiveButton("Editar", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {
                        //Intent pantallaEdicion = new Intent(getApplicationContext(), ActualizarActivity.class);
                        //pantallaEdicion.putExtra("registroAeditar", reg);
           }
                })
                .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        SingletonDB.getInstance().getDaoSession().getRegistroDao().deleteByKey(reg.get_id());
                        miNotificacion("Eliminado!");
                        dialog.cancel();
                    }
                })
                .setNeutralButton("No hacer nada", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        //dialog.cancel();
                    }
                })
        ;
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void miNotificacion(String texto){
        Toast toast = Toast.makeText(this.getApplicationContext(),
                texto, Toast.LENGTH_SHORT);
        //Aqui se muestra la notificacion
        toast.show();
    }
}

package registro.eingv.uabc.registroeingv.lista;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import registro.eingv.uabc.registroeingv.R;
import registro.eingv.uabc.registroeingv.db.Registro;

/**
 * Created by xhendor on 2/24/15.
 */
public class ListaRegistroAdapter extends BaseAdapter {

    private Context context;
    private List<Registro> listaRegistro;

    public ListaRegistroAdapter(Context context,List<Registro> listaRegistro){

        super();
        this.context=context;
        this.listaRegistro=listaRegistro;
    }


    @Override
    public int getCount() {
        return listaRegistro.size();
    }

    @Override
    public Object getItem(int position) {
        return listaRegistro.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_vew_each_item, null);


        if(!this.listaRegistro.isEmpty()) {
            Bitmap bitmap;

            bitmap=BitmapFactory.decodeByteArray(
                  (this.listaRegistro.get(position).getImagen())
               ,0,(this.listaRegistro.get(position).getImagen()).length);
            if (bitmap!=null){

                ImageView imageView= (ImageView) convertView.findViewById(R.id.imagId);
                imageView.setImageBitmap(Bitmap.createBitmap(bitmap));

                TextView textViewClave=(TextView)convertView.findViewById(R.id.textViewClave);
                TextView textViewCoordenadas=(TextView)convertView.findViewById(R.id.textViewCoordenadas);

                textViewClave.setText(this.listaRegistro.get(position).get_id()+": "+this.listaRegistro.get(position).getLugar());
                textViewCoordenadas.setText("[ "+this.listaRegistro.get(position).getLatitud() + " ] , [ "  + this.listaRegistro.get(position).getLongitud()+ "]");
            }
            else {
                TextView textViewClave=(TextView)convertView.findViewById(R.id.textViewClave);
                TextView textViewCoordenadas=(TextView)convertView.findViewById(R.id.textViewCoordenadas);

                textViewClave.setText(this.listaRegistro.get(position).get_id()+": "+this.listaRegistro.get(position).getLugar());

                textViewCoordenadas.setText("[ "+this.listaRegistro.get(position).getLatitud() + " ] , [ "  + this.listaRegistro.get(position).getLongitud()+ "]");

            }

        }
        return convertView;
    }
}

package registro.eingv.uabc.registroeingv.dialogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;


public class Dialogo extends Toast {
    private Context context;

    public Dialogo(Context cont, int duracion) {
        super(cont);
        context = context;
        this.setDuration(duracion);
    }

    public void show(CharSequence text) {
        LayoutInflater li = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
}
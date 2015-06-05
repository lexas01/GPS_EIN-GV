package registro.eingv.uabc.registroeingv;

import android.database.sqlite.SQLiteDatabase;

import registro.eingv.uabc.registroeingv.db.DaoMaster;
import registro.eingv.uabc.registroeingv.db.DaoSession;

/**
 * Created by xhendor on 2/24/15.
 */
public class SingletonDB {



    private static SingletonDB ourInstance = new SingletonDB();

    public static SingletonDB getInstance() {
        return ourInstance;
    }

    private SingletonDB() {

      SQLiteDatabase db=  RegistroActivity.getDb();

        openDatabase(db);
    }

    //Instancia de base de datos
    private SQLiteDatabase db;
    //Instancia de DaoMaster
    private DaoMaster daoMaster;
    //Instancia de Sesion
    private DaoSession daoSession;

    public void openDatabase(SQLiteDatabase db){
        this.db=db;
        //Agrega la base de datos al Master
        daoMaster = new DaoMaster(db);
        setDaoSession(daoMaster.newSession());
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }
}

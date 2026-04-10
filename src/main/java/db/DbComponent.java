package db;
import java.util.Map;

public class DbComponent<T extends IAdapter> {
    private T adapter;
    
    public DbComponent(Class<T> adapterClass, String url, String user, String pass, String queryPath) throws Exception {
        this.adapter = adapterClass.getDeclaredConstructor().newInstance();
        this.adapter.connect(url, user, pass);
    }

    public void executeAction(String actionName, Map<String, Object> params) {
        System.out.println("Ejecutando acción en DB: " + actionName);
    }
}
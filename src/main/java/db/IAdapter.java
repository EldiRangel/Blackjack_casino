package db;
import java.util.List;
import java.util.Map;

public interface IAdapter {
    void connect(String url, String user, String pass);
    List<Map<String, Object>> query(String sql, Map<String, Object> params);
}
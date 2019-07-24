package Analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NodeInfo {
    private HashMap<String, Integer> permissions = new HashMap<>();
    public int highestLevel = 0;

    private void calculateLevel(){
        if(permissions.size() == 0){
            highestLevel = 0;
        } else {
            Iterator iter = permissions.entrySet().iterator();
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
            if(entry.getValue() > highestLevel){
                highestLevel = entry.getValue();
            }
        }
    }

    public void addPermission(String permission){
        if(!permissions.containsKey(permission)){
            permissions.putIfAbsent(permission, Permissions.checkPermissionLevel(permission));
            calculateLevel();
        }
    }

    public HashMap<String, Integer> getPermissions(){
        return permissions;
    }
}

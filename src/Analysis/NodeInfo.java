package Analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NodeInfo {

    /*
    A Node in the Call graph (constructed by Flowdroid) represents a method body loaded from the APK into the Scene by Soot
     */

    /*
    permissions object is a HashMap of all the permissions in this current method body

    structure of the permission HashMap:
    {
        permission1: permission level number
        permission2: permission level number
        ...
    }
     */
    private HashMap<String, Integer> permissions = new HashMap<>();

    // keep counter on the highest permission level of the permissions added to the node information
    public int highestLevel = 0;

    // add a permission key to the HashMap
    public void addPermission(String permission){

        // if the key doesn't exist
        if(!permissions.containsKey(permission)){

            // add it to as a key
            permissions.putIfAbsent(permission, Permissions.checkPermissionLevel(permission));
            calculateLevel(); //recalculate the highest level of permissions added to the HashMap
        }
    }

    // calculate the highest level of permissions added to the HashMap
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

    public HashMap<String, Integer> getPermissions(){
        return permissions;
    }
}

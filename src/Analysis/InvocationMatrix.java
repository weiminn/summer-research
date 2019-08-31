package Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InvocationMatrix {

    /*
    Invocation maxtrix is a HashMap object where
    api1's pseudo signature: 1 or 0
    api2's pseudo signature: 1 or 0
    ... and so on

    where 1 denotes the API being invoked and 0 denotes that it is not invoked
     */
    public HashMap<String, Integer> appMatrix = new HashMap<>();

    //initialize the appMatrix variable with a HashMap according to the format described above
    public InvocationMatrix(){

        //intialize iterator to traverse through a hashmap of Android APIs loaded in GlobalRef
        Iterator iter = GlobalRef.androidApiMap.entrySet().iterator();
        while(iter.hasNext()){

            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<String> mArr = (ArrayList<String>) entry.getValue();
            Iterator<String> _iter = mArr.iterator();

            while(_iter.hasNext()){
                String method = (String) _iter.next();

                //insert the API's pseudo signature
                appMatrix.put(entry.getKey() + " " + method, 0);
            }
        }
    }
}

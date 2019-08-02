package Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InvocationMatrix {

    public HashMap<String, Integer> appMatrix = new HashMap<>();

    public InvocationMatrix(){
        Iterator iter = GlobalRef.androidApiMap.entrySet().iterator();
        while(iter.hasNext()){

            Map.Entry entry = (Map.Entry) iter.next();
            ArrayList<String> mArr = (ArrayList<String>) entry.getValue();
            Iterator<String> _iter = mArr.iterator();

            while(_iter.hasNext()){
                String method = (String) _iter.next();
                appMatrix.put(entry.getKey() + " " + method, 0);
            }
        }

        System.out.println();
    }
}

package Analysis;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class AndroidAPI {

    public AndroidAPI(){

        /*
        initialize the GlobalRef's androidAPIMap with a HashMap object that has a structure of
        {
            classname1: Arraylist of method names
            classname2: Arraylist of method names
            ... and so on
        }
         */

        try {

            //read all the permission-required APIs from the apis.json file
            InputStream stream = this.getClass().getResourceAsStream("apis.json");
            String readFiles = IOUtils.toString(stream, Charset.defaultCharset());
            stream.close();

            //parse the .json string value into an array of API objects
            Gson gson = new Gson();
            API[] apis = gson.fromJson(readFiles, API[].class);

            //iterate through the list of APIs and store them in GlobalRef
            for(int i = 0; i < apis.length; i++){

                //check if an overloaded version of the method already exists (same method name)
                if(!GlobalRef.androidApiMap.containsKey(apis[i].getClassName())){

                    //create a new array list
                    ArrayList<String> list = new ArrayList<>();
                    list.add(apis[i].getApi());
                    GlobalRef.androidApiMap.put(apis[i].getClassName(), list);

                } else {

                    //if the arraylist is already created for the class, then just add the method name into the arraylist
                    if(!GlobalRef.androidApiMap.get(apis[i].getClassName()).contains(apis[i].getApi())){
                        GlobalRef.androidApiMap.get(apis[i].getClassName()).add(apis[i].getApi());
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Exception at Android APIs extractor: " + e);
        }
    }

    // not used - helper function in case of need
    public boolean hasEntry(String cName, String mName){
        if(GlobalRef.androidApiMap.containsKey(cName)){
            Iterator<String> iter = GlobalRef.androidApiMap.get("cName").iterator();
            while (iter.hasNext()){
                String method = iter.next();
                if(method.equals(mName)){
                    return true;
                }
            }
        }
        return false;
    }
}


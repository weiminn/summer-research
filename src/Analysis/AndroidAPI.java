package Analysis;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class AndroidAPI {

    public AndroidAPI(){
        try {
            InputStream stream = this.getClass().getResourceAsStream("apis.json");
            String readFiles = IOUtils.toString(stream, Charset.defaultCharset());
            stream.close();

            Gson gson = new Gson();
            API[] apis = gson.fromJson(readFiles, API[].class);

            for(int i = 0; i < apis.length; i++){
                if(!GlobalRef.androidApiMap.containsKey(apis[i].getClassName())){
                    ArrayList<String> list = new ArrayList<>();
                    list.add(apis[i].getApi());
                    GlobalRef.androidApiMap.put(apis[i].getClassName(), list);
                } else {
                    if(!GlobalRef.androidApiMap.get(apis[i].getClassName()).contains(apis[i].getApi())){
                        GlobalRef.androidApiMap.get(apis[i].getClassName()).add(apis[i].getApi());
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Exception at Android APIs extractor: " + e);
        }
    }

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


package Analysis.Transformers;

import Analysis.GlobalRef;
import Analysis.NodeInfo;
import soot.*;
import soot.jimple.Stmt;

import java.util.Map;

public class GraphNodeReaderTransformer extends BodyTransformer {

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        // add a new NodeInfo object to record the nodes of the current APK in the GlobalRef
        String sig = body.getMethod().getSignature();
        GlobalRef.currentNodes.putIfAbsent(sig, new NodeInfo());

        // traverse through the units of the body of the method and check if any permission string is used
        for(Unit u: body.getUnits()){
            Stmt stmt = (Stmt) u;

            String stmtStr = stmt.toString();

            // if there is an instance of ".permission" substring
            if (stmtStr.contains(".permission.")){

                //get the name of the permission
                int pos = stmtStr.indexOf(".permission.") + ".permission.".length();
                String pName = "";

                for(int i = pos; i < stmtStr.length(); i++){
                    if(stmtStr.charAt(i) == '"' || stmtStr.charAt(i) == '\\'){
                        break;
                    } else {
                        pName += stmtStr.charAt(i);
                    }
                }

                GlobalRef.currentNodes.get(sig).addPermission(pName); // add the permission name to the NodeInfo object
            }
        }
    }
}

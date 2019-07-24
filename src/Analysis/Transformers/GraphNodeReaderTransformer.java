package Analysis.Transformers;

import Analysis.GlobalRef;
import Analysis.Transformers.CallGraph.Permissions;
import soot.*;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Map;

public class GraphNodeReaderTransformer extends BodyTransformer {

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        GlobalRef.currentNodes.append(body.getMethod().getSignature() + "\n");

        int pLevel = 0;
        ArrayList<String> permissions = new ArrayList<>();

        for(Unit u: body.getUnits()){
            Stmt stmt = (Stmt) u;

            String stmtStr = stmt.toString();

            if (stmtStr.contains(".permission.")){
                int pos = stmtStr.indexOf(".permission.") + ".permission.".length();
                String pName = "";

                for(int i = pos; i < stmtStr.length(); i++){
                    if(stmtStr.charAt(i) == '"' || stmtStr.charAt(i) == '\\'){
                        break;
                    } else {
                        pName += stmtStr.charAt(i);
                    }
                }

                permissions.add(pName);
                int _pLevel = Permissions.checkPermissionLevel(pName);
                if(_pLevel > pLevel){
                    pLevel = _pLevel;
                }
            }
        }

        for (int i = 0; i < permissions.size(); i++){
            GlobalRef.currentNodes.append(permissions.get(i) + ": level " + Permissions.checkPermissionLevel(permissions.get(i)) + "\n");
        }

        GlobalRef.currentNodes.append("Overall Permission level " + pLevel + "\n" + "===========================================\n");
    }
}

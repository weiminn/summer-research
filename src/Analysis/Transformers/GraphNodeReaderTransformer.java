package Analysis.Transformers;

import Analysis.GlobalRef;
import Analysis.NodeInfo;
import soot.*;
import soot.jimple.Stmt;

import java.util.Map;

public class GraphNodeReaderTransformer extends BodyTransformer {

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        String sig = body.getMethod().getSignature();
        GlobalRef.currentNodes.putIfAbsent(sig, new NodeInfo());

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

                GlobalRef.currentNodes.get(sig).addPermission(pName);
            }
        }
    }
}

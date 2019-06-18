package Analysis.Transformers;

import soot.*;
import soot.jimple.ArrayRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;
import soot.toolkits.graph.ExceptionalUnitGraph;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyBodyTransformer extends BodyTransformer {

    private String ts;
    public MyBodyTransformer(String ts){
        this.ts = ts;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(body);
        try {
            FileWriter writerNone = new FileWriter("./analysisOutput/logs/" + ts + "-None.txt", true);
            FileWriter writerNorm = new FileWriter("./analysisOutput/logs/" + ts + "-Normal.txt", true);
//            FileWriter writerSign = new FileWriter("./analysisOutput/logs/" + ts + "-Signature.txt", true);
//            FileWriter writerDang = new FileWriter("./analysisOutput/logs/" + ts + "-Dangerous.txt", true);

            String mName = body.getMethod().getName();
            String cName = body.getMethod().getDeclaringClass().getName();
            String title = cName + " | " + mName;

            String toLog =
                    "=====================================\n" +
                    title +
                    "\n-------------------------------------\n";

            FileWriter currentWriter;

            if(mName.equals("checkPermission")){
                //insert jimple code for logging
            }

            Boolean invokesPermission = false;

            for(Unit u: body.getUnits()){
                Stmt stmt = (Stmt) u;
                toLog += stmt + "\n";

                if (stmt.toString().contains("android.permission.")){
                    System.out.println(stmt);
                    invokesPermission = true;
                }

            }

            writerNone.write(toLog);
            if(invokesPermission){
                writerNorm.write(toLog);
            }

            writerNone.close();
//            writerNorm.close();
//            writerSign.close();
//            writerDang.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static int checkPermissionLevel(String pName){

        return 0;
    }
}

package Analysis.Transformers;

import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;
import soot.toolkits.graph.ExceptionalUnitGraph;
import java.io.FileWriter;
import java.io.IOException;
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
            FileWriter writerNone = new FileWriter("./sootOutput/logs/" + ts + "-None.txt", true);
//            FileWriter writerNorm = new FileWriter("./sootOutput/logs/" + ts + "-Normal.txt", true);
//            FileWriter writerSign = new FileWriter("./sootOutput/logs/" + ts + "-Signature.txt", true);
//            FileWriter writerDang = new FileWriter("./sootOutput/logs/" + ts + "-Dangerous.txt", true);

            String mName = body.getMethod().getName();
            String cName = body.getMethod().getDeclaringClass().getName();
            String title = "\n" +cName + " | " + mName + "\n";

            String toLog = title;

            FileWriter currentWriter;

            for(Unit u: body.getUnits()){
                Stmt stmt = (Stmt) u;
                toLog += u + "\n";
                System.out.println(u);

                if (stmt.toString().contains("android.permission.")){
                    InvokeExpr expr = stmt.getInvokeExpr();
                    ImmediateBox immediateBox = (ImmediateBox) expr.getArgBox(1);
                    Value boxValue = immediateBox.getValue();
                    String permissionName = boxValue.toString();
                    System.out.println();
                }

            }

            writerNone.write(toLog);

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

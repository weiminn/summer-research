package Analysis.Transformers;

import Analysis.Transformers.CallGraph.Permissions;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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

            FileWriter writer;

            SootMethod method = body.getMethod();

            String mName = method.getName();
            String cName = method.getDeclaringClass().getName();
            String returnType = method.getReturnType().toString();
            String parameters = method.getParameterTypes().toString();
            String title = cName + " | " + mName + " " + parameters + " : " + returnType;

            String toLog =
                    "=====================================\n" +
                    title +
                    "\n-------------------------------------\n";

            if(mName.equals("checkPermission")){
                //insert instrument for permission logging
//                Unit firstUnit = body.getUnits().getFirst();
//
//                //insert tmpRef = android.util.log.i
//                Local logRef = addTempRef(body, "tmpRef", "android.util.log");
//                AssignStmt assignStmt = Jimple.v().newAssignStmt(
//                        logRef,
//                        Jimple.v().newStaticFieldRef(
//                                Scene.v().getField("<android.util.log: int i>").makeRef())
//                );
//                body.getUnits().insertBefore(assignStmt, firstUnit);

//                Local arrRef = add


            }

//            if(method.getParameterTypes().contains("android.content.intent")){
//                //insert instrument for intent call logging
//            }

//            if(mName.equals("ContentResolver"))

            String pLevel = "none";
            Boolean contentProvider = false;

            for(Unit u: body.getUnits()){
                Stmt stmt = (Stmt) u;
                toLog += stmt + "\n";


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

                    String _pLevel = Permissions.checkPermissionLevel(pName);
                    if(Permissions.PermissionLevels.get(_pLevel) > Permissions.PermissionLevels.get(pLevel)){
                        pLevel = _pLevel;
                    }

                    System.out.println(pName + " : " + pLevel);
                }

                if(stmtStr.contains("content://")){
                    int pos = stmtStr.indexOf("content://") + "content://".length();
                    String cpName = "";

                    for(int i = pos; i < stmtStr.length(); i++){
                        if(stmtStr.charAt(i) == '"' || stmtStr.charAt(i) == '\\'){
                            break;
                        } else {
                            cpName += stmtStr.charAt(i);
                        }
                    }

                    contentProvider = true;
                    System.out.println(cpName);
                }
            }

            writer = new FileWriter("./analysisOutput/logs/" + ts + "-all.txt", true);
            writer.write(toLog + "\n");
            writer.close();

            writer = new FileWriter("./analysisOutput/logs/" + ts + "-" + pLevel + ".txt", true);
            writer.write(toLog + "\n");
            writer.close();

            if(contentProvider){
                writer = new FileWriter("./analysisOutput/logs/" + ts + "-ContentProviders.txt", true);
                writer.write(toLog + "\n");
                writer.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        body.validate();

    }

    private static Local addTempRef(Body body, String name, String type){
        Local tmpRef = Jimple.v().newLocal(name, RefType.v(type));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }
//    private static Local addArrayRef(Body body, String name){
//        Local tmpString = Jimple.v().newParameterRef(name, RefType.v("java.util.Arrays"));
//        body.getLocals().add(tmpString);
//        return tmpString;
//    }


}

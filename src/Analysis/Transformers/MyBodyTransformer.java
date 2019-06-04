package Analysis.Transformers;

//import org.graphstream.graph.Graph;
import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.internal.JIdentityStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MyBodyTransformer extends BodyTransformer {

    private String ts;
    public MyBodyTransformer(String ts){
        this.ts = ts;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {


        Chain<Unit> unitChain = body.getUnits().getNonPatchingChain();
//        Iterator<Unit> units = (Iterator<Unit>) body.getUnits().iterator();

        try {
            FileWriter writer = new FileWriter("./sootOutput/logs/" + ts + ".txt", true);
            String mName = body.getMethod().getName();
            writer.write("\n" + mName + "\n");

            //get array of the Chain
            Object[] uArr = unitChain.toArray();
            for(int i = 0; i < uArr.length; i++) {
                String u = uArr[i].toString();
                writer.write(u + "\n");
                System.out.println(u);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

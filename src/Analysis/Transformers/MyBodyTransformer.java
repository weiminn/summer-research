package Analysis.Transformers;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Iterator;
import java.util.Map;

public class MyBodyTransformer extends BodyTransformer {
    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

//        Iterator<Unit> units = body.getUnits().iterator();
//        if(units.hasNext()){
//            Unit u = units.next();
//            System.out.println(u);
//        }

        ExceptionalUnitGraph euGraph = new ExceptionalUnitGraph(body);
        System.out.println(euGraph);
    }
}

package Analysis.Transformers;

import fj.data.Set;
import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;

import java.util.HashMap;
import java.util.Map;

public class MySceneTransformer extends SceneTransformer {
    private static String ts;
    public MySceneTransformer(String ts){
        this.ts = ts;
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> map) {

//        CallGraph cg = Scene.v().getCallGraph();
//        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
//        IFDSTabulationProblem problem = new IFDSReachingDefinitions(icfg);
//
//        icfg.getOrCreateUnitGraph();
//
//
//        IFDSSolver solver = new IFDSSolver(problem);
//        System.out.println("Starting solver...");
//        solver.solve();
//        System.out.println("Done");
    }
}

package Analysis.Transformers.CallGraph;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.Map;

public class CHATransformer extends SceneTransformer {

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        soot.jimple.toolkits.callgraph.CHATransformer.v().transform();
        SootMethod src = Scene.v().getMainClass().getMethodByName("main");
        CallGraph cg = Scene.v().getCallGraph();
    }
}

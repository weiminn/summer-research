package Analysis.Transformers.CallGraph;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.HashMap;
import java.util.Map;

public class SPARKTransformer extends SceneTransformer {
    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        SootClass c = Scene.v().getMainClass();
        c.setApplicationClass();

//        HashMap opt = new HashMap();
//        opt.put("verbose","true");
//        opt.put("propagator","worklist");
//        opt.put("simple-edges-bidirectional","false");
//        opt.put("on-fly-cg","true");
//        opt.put("set-impl","array");
//        opt.put("double-set-old","hybrid");
//        opt.put("double-set-new","hybrid");
//
//        SparkTransformer.v().transform("", opt);
//        CallGraph cg = Scene.v().getCallGraph();
    }
}

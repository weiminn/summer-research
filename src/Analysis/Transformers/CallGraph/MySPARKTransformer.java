package Analysis.Transformers.CallGraph;

import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MySPARKTransformer extends SceneTransformer {
    @Override
    protected void internalTransform(String s, Map<String, String> map) {

        HashMap opt = new HashMap();
        opt.put("verbose","true");
        opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        opt.put("on-fly-cg","true");
        opt.put("set-impl","array");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");

        SparkTransformer.v().transform("", opt);
        CallGraph cg = Scene.v().getCallGraph();


        DotGraph canvas = new DotGraph("call-graph");
        Iterator<Edge> edges = cg.iterator();

        int i = 0;
        while (edges.hasNext()) {
            Edge next = edges.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();

            canvas.drawEdge(src.toString(), tgt.toString());
            G.v().out.println(i++);
            G.v().out.println(
                    src.toString() +
                            " to " +
                            tgt.toString() + "\n");
        }

        G.v().out.println("Graph size: " + cg.size());

        String fileName = "./sootOutput/graphs/" + new Date().toString() + DotGraph.DOT_EXTENSION;
        canvas.plot(fileName);
    }
}

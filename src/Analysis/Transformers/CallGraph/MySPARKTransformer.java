package Analysis.Transformers.CallGraph;

import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MySPARKTransformer extends SceneTransformer {
    @Override
    protected void internalTransform(String s, Map<String, String> map) {

        System.out.println("[spark] Starting analysis ...");

        HashMap opt = new HashMap();
        opt.put("enabled","true");
        opt.put("verbose","true");
        opt.put("ignore-types","false");
        opt.put("force-gc","false");
        opt.put("pre-jimplify","false");
        opt.put("vta","false");
        opt.put("rta","false");
        opt.put("field-based","false");
        opt.put("types-for-sites","false");
        opt.put("merge-stringbuffer","true");
        opt.put("string-constants","false");
        opt.put("simulate-natives","true");
        opt.put("simple-edges-bidirectional","false");
        opt.put("on-fly-cg","true");
        opt.put("simplify-offline","false");
        opt.put("simplify-sccs","false");
        opt.put("ignore-types-for-sccs","false");
        opt.put("propagator","worklist");
        opt.put("set-impl","double");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");
        opt.put("dump-html","false");
        opt.put("dump-pag","false");
        opt.put("dump-solution","false");
        opt.put("topo-sort","false");
        opt.put("dump-types","true");
        opt.put("class-method-var","true");
        opt.put("dump-answer","false");
        opt.put("add-tags","false");
        opt.put("set-mass","false");

        SparkTransformer.v().transform("", opt);

        System.out.println("[spark] Done!");

        CallGraph cg = Scene.v().getCallGraph();

        DotGraph canvas = new DotGraph("call-graph");
        Iterator<Edge> edges = cg.iterator();

        System.out.println("Graph Edges:");
        int i = 0;
        while (edges.hasNext()) {
            Edge next = edges.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();

            DotGraphNode srcNode = canvas.drawNode(src.toString());
            Body srcBody = src.method().getActiveBody();
            srcNode.setShape("rectangle");
            srcNode.setAttribute("color", "red");

            DotGraphNode tgtNode = canvas.drawNode(src.toString());
//            tgtNode.setShape("oval");
            tgtNode.setAttribute("color", "blue");

            DotGraphEdge edge = canvas.drawEdge(src.toString(), tgt.toString());
//            edge.setLabel(next.srcUnit().toString());
            G.v().out.println(i++);
            G.v().out.println(src + " to " + tgt + "\n");
        }

        G.v().out.println("Graph size: " + cg.size());

        String fileName = "./sootOutput/graphs/spark: " + new Date().toString() + DotGraph.DOT_EXTENSION;
        canvas.plot(fileName);
    }
}

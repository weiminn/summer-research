package Analysis.Transformers.CallGraph;

import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class MyCHATransformer extends SceneTransformer {

    @Override
    protected void internalTransform(String s, Map<String, String> map) {

        G.v().out.println("Inititaing CHA transformation... ");
        CHATransformer.v().transform();
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

        String fileName = "./sootOutput/graphs/cha: " + new Date().toString() + DotGraph.DOT_EXTENSION;
        canvas.plot(fileName);
    }
}

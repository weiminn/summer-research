package Analysis;

import Analysis.Transformers.CallGraph.MyCHATransformer;
import Analysis.Transformers.CallGraph.MySPARKTransformer;
import Analysis.Transformers.MyBodyTransformer;
import de.bodden.tamiflex.playout.ClassDumper;
import fj.Hash;
import soot.*;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.IManifestHandler;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class RunAnalysis {
    public static void main(String[] args){

        initializeSoot(
                Options.src_prec_apk,
                true,
                "sample/apk/" +
                        "textra" +
                        ".apk",
                Options.output_format_dex,
                "dex",
                true);

    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, String processDir, int outputFormat, String outputDir, boolean wholeProgram){

        G.v().reset();

        String ts = new Date().toString();

        outputManifest(processDir, outputDir, ts);

        soot.G.reset();

        Options.v().set_process_dir(Collections.singletonList(processDir));
        Options.v().set_src_prec(srcPrec);
        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir("./analysisOutput/" + outputDir + "/");

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        Options.v().set_android_jars("/home/wei/Android/Sdk/platforms");
        Options.v().set_allow_phantom_refs(phantomRefs);
        Options.v().set_no_bodies_for_excluded(false);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        Scene.v().loadNecessaryClasses();

        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
        config.setSootIntegrationMode(InfoflowAndroidConfiguration.SootIntegrationMode.UseExistingInstance);
        config.getAnalysisFileConfig().setTargetAPKFile(processDir);
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        config.setEnableReflection(true);

        SetupApplication app = new SetupApplication(
                config
        );

        app.constructCallgraph();

        CallGraph cg_ = Scene.v().getCallGraph();

        Collection<Pack> allPacks = PackManager.v().allPacks();

        generateGraph();
    }

    private static HashMap<String, Object> getRequestedPermissions(String apkPath){

        HashMap<String, Object> toReturn = new HashMap<>();
        Set<String> permissions = null;
        List<AXmlNode> providers = null;

        try {
            ProcessManifest manifest = new ProcessManifest(apkPath);
            permissions = manifest.getPermissions();
            providers = manifest.getProviders();
            List<AXmlNode> activities = manifest.getActivities();
        } catch (Exception e){
            System.err.println(e);
        }

        toReturn.put("permissions", permissions);
        toReturn.put("providers", providers);

        return toReturn;
    }

    private static void outputManifest(String processDir, String outputDir, String ts){
        HashMap manifestInfo = getRequestedPermissions(processDir);

        try{

            File logsDir = new File("./analysisOutput/logs");
            File outDir = new File("./analysisOutput/" + outputDir);

            if(!logsDir.exists()){
                logsDir.mkdir();
            }
            if(!outDir.exists()){
                outDir.mkdir();
            }

            FileWriter mWriter = new FileWriter("./analysisOutput/logs/" + ts + "-ManifestAnalysis.txt", true);

            Set<String> pSet = (Set<String>) manifestInfo.get("permissions");
            Iterator<String> pSetItr = pSet.iterator();

            List<AXmlNode> cpList = (List<AXmlNode>) manifestInfo.get("providers");
            Iterator<AXmlNode> cpListItr = cpList.iterator();

            mWriter.write("=====================================\nPermissions Requested\n-------------------------------------\n");

            while(pSetItr.hasNext()){
                mWriter.write(pSetItr.next() + "\n");
            }

            mWriter.write("\n=====================================\nContent Providers Registered\n-------------------------------------\n");

            while(cpListItr.hasNext()){
                Map<String, AXmlAttribute<?>> attributes = cpListItr.next().getAttributes();

                String cpName = attributes.get("authorities").getValue().toString();
                mWriter.write(cpName + "\n");
            }
            mWriter.close();

        } catch (Exception e){
            System.err.println(e);
        }
    }

    private static void generateGraph(){
        CallGraph cg = Scene.v().getCallGraph();

        DotGraph canvas = new DotGraph("call-graph");
        Iterator<Edge> edges = cg.iterator();

//        System.out.println("Graph Edges:");
        int i = 0;
        while (edges.hasNext()) {
            Edge next = edges.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();

            DotGraphNode srcNode = canvas.drawNode(src.toString());
//            srcNode.setShape("rectangle");
//            srcNode.setAttribute("color", "red");

            DotGraphNode tgtNode = canvas.drawNode(src.toString());
//            tgtNode.setShape("oval");
//            tgtNode.setAttribute("color", "blue");

            DotGraphEdge edge = canvas.drawEdge(src.toString(), tgt.toString());
//            edge.setLabel(next.srcUnit().toString());
//            G.v().out.println(i++);
//            G.v().out.println(src + " to " + tgt + "\n");
        }

        G.v().out.println("Generated Dot Graph size: " + cg.size());

        String fileName = "./analysisOutput/graphs/spark: " + new Date().toString() + DotGraph.DOT_EXTENSION;
        canvas.plot(fileName);
    }

}

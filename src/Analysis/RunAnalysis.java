package Analysis;

import Analysis.Transformers.GraphNodeReaderTransformer;
import soot.*;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RunAnalysis {
    public static void main(String[] args){

        AndroidAPI apis = new AndroidAPI();

        analyzeAPKDirectory("sample/apk/test_dataset/");

//        analyzeAPK(
//                "com.ncbhk.mortgage.android.hk.apk"
////                "air.com.jeuxdefille.ChickenCookinggame.apk"
//            , "sample/apk/dataset/"
//        );
    }

    private static void analyzeAPK(String apk){

        GlobalRef.currentApk = apk;

        initializeSoot( Options.src_prec_apk, true, Options.output_format_dex,
                "analysisOutput/" + GlobalRef.ts + "/",
                true);

        PackManager.v().getPack("jtp").add(new Transform("jtp.readbody", new GraphNodeReaderTransformer()));

        try { PackManager.v().runPacks(); } catch (Exception e){ System.out.println(e); }

        initializeFlowdroid( GlobalRef.inputDir + GlobalRef.currentApk );

    }

    private static void analyzeAPK(String apk, String dir){

        GlobalRef.inputDir = dir;
        GlobalRef.currentApk = apk;

        initializeSoot( Options.src_prec_apk, true, Options.output_format_dex,
                "analysisOutput/" + GlobalRef.ts + "/",
                true);

        PackManager.v().getPack("jtp").add(new Transform("jtp.readbody", new GraphNodeReaderTransformer()));

        try { PackManager.v().runPacks(); } catch (Exception e){ System.out.println(e); }

        initializeFlowdroid( GlobalRef.inputDir + GlobalRef.currentApk );

    }

    private static void analyzeAPKDirectory(String dir){

        GlobalRef.inputDir = dir;

        File folder = new File(GlobalRef.inputDir);
        File[] fileList = folder.listFiles();

        for(int i = 0; i < fileList.length; i++){
            if(fileList[i].getName().endsWith(".apk")){
                GlobalRef.apks.add(fileList[i].getName());
            }
        }

        Iterator<String> iter = GlobalRef.apks.iterator();
        while(iter.hasNext()){
            GlobalRef.currentApk = iter.next();
            analyzeAPK(GlobalRef.currentApk);
        }

        generateCSV();
    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, int outputFormat, String outputDir, boolean wholeProgram){

        GlobalRef.outputDir = outputDir;

        if (!new File(GlobalRef.outputDir).isDirectory()) { new File(GlobalRef.outputDir).mkdir(); }

        G.reset();

        String apk = GlobalRef.inputDir + GlobalRef.currentApk;
        Options.v().set_process_dir(Collections.singletonList(apk));
        Options.v().set_src_prec(srcPrec);
        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir(GlobalRef.outputDir);

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_validate(true);
        Options.v().set_android_jars("/home/wei/Android/Sdk/platforms");
        Options.v().set_allow_phantom_refs(phantomRefs);
        Options.v().set_no_bodies_for_excluded(false);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        Scene.v().loadNecessaryClasses();
    }

    private static void initializeFlowdroid(String processDir){
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
        config.setSootIntegrationMode(InfoflowAndroidConfiguration.SootIntegrationMode.UseExistingInstance);
        config.getAnalysisFileConfig().setTargetAPKFile(processDir);
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        config.setEnableReflection(true);

        SetupApplication app = new SetupApplication(config);

        boolean sparkException = false;

        try {
            app.constructCallgraph();
        } catch (Exception e){
            System.out.println(e);
            sparkException = true;
        }

        if(sparkException == false){

            try {

                FileWriter writer = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - Nodes.txt", true);

                Iterator iter = GlobalRef.currentNodes.entrySet().iterator();
                while(iter.hasNext()){

                    Map.Entry<String, NodeInfo> entry = (Map.Entry<String, NodeInfo>) iter.next();
                    writer.write(entry.getKey() + "\n");

                    NodeInfo info = (NodeInfo) entry.getValue();

                    Iterator iter2 = info.getPermissions().entrySet().iterator();
                    while(iter2.hasNext()){

                        Map.Entry<String, Integer> per = (Map.Entry<String, Integer>) iter2.next();
                        writer.write(per.getKey() + " - " + per.getValue() + "\n");
                    }

                    writer.write("Highest permission level - " + info.highestLevel+ "\n\n");
                    writer.flush();
                }

                writer.close();
                GlobalRef.currentNodes.clear();

            } catch (Exception e){System.out.println(e);}

            outputManifest();

            generateGraph();
        }
    }

    private static HashMap<String, Object> readManifest(){

        HashMap<String, Object> toReturn = new HashMap<>();
        Set<String> permissions = null;
        List<AXmlNode> providers = null;

        try {

            ProcessManifest manifest = new ProcessManifest(GlobalRef.inputDir + GlobalRef.currentApk);
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

    private static void outputManifest(){

        HashMap manifestInfo = readManifest();

        try{

            FileWriter mWriter = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - ManifestAnalysis.txt", true);

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

        } catch (Exception e){ System.err.println(e); }
    }

    private static void generateGraph(){

        CallGraph cg = Scene.v().getCallGraph();

        DotGraph canvas = new DotGraph("call-graph");
        Iterator<Edge> edges = cg.iterator();

        try {

            InvocationMatrix matrix = new InvocationMatrix();
            int counter = 0;

            FileWriter writer = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - Edges.txt", true);

            while (edges.hasNext()) {

                Edge next = edges.next();

                MethodOrMethodContext src = next.getSrc();
                MethodOrMethodContext tgt = next.getTgt();
                writer.write(src.toString() + " -> " + tgt.toString() + "\n");

                String fullClassMethod = tgt.method().getDeclaringClass().getName() + " " + tgt.method().method().getName();
                if(matrix.appMatrix.containsKey(fullClassMethod)){
                    if(matrix.appMatrix.get(fullClassMethod) == 0){
                        matrix.appMatrix.put(fullClassMethod, 1);
                        counter++;
                    }
                }

                DotGraphNode srcNode = canvas.drawNode(src.toString());
                DotGraphNode tgtNode = canvas.drawNode(tgt.toString());
                DotGraphEdge edge = canvas.drawEdge(src.toString(), tgt.toString());
            }

            GlobalRef.invocationMatrices.put(GlobalRef.currentApk, matrix);

            writer.flush();
            writer.close();

            System.out.println("Generated Dot Graph size: " + cg.size());

            String fileName = GlobalRef.outputDir + GlobalRef.currentApk + DotGraph.DOT_EXTENSION;
            canvas.plot(fileName);

        } catch (Exception e){ System.out.println(e); }
    }

    private static void generateCSV(){

        ArrayList<ArrayList<String>> toPrint = new ArrayList<>();

        InvocationMatrix matrix = new InvocationMatrix();
        ArrayList<String> topRow = new ArrayList<>();
        topRow.add("apk");
        Iterator topIter = matrix.appMatrix.entrySet().iterator();
        while(topIter.hasNext()){
            Map.Entry entry = (Map.Entry) topIter.next();
            topRow.add((String) entry.getKey());
        }

        toPrint.add(topRow);

        for(Iterator i = GlobalRef.invocationMatrices.entrySet().iterator(); i.hasNext();){

            ArrayList<String> newRow = new ArrayList<>();

            Map.Entry apkInvokMap = (Map.Entry) i.next();
            String apkName = (String) apkInvokMap.getKey();
            newRow.add(apkName);
            for(Iterator j = topRow.iterator(); j.hasNext();){
                String colName = (String) j.next();
                if(!colName.equals("apk")){
                    newRow.add(
                        GlobalRef.invocationMatrices.get(apkName).appMatrix.get(colName).toString()
                    );
                }
            }
            toPrint.add(newRow);
        }

        StringBuilder sb = new StringBuilder();

        for(Iterator i = toPrint.iterator(); i.hasNext();){
            for(Iterator j = ((ArrayList<String>)i.next()).iterator(); j.hasNext();){
                sb.append(j.next());
                if(j.hasNext()){
                    sb.append(',');
                }
            }
            sb.append("\n");
        }

        try {
            Files.write(Paths.get(GlobalRef.outputDir + "APIs.csv"), sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

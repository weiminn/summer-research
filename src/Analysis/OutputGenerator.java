package Analysis;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class OutputGenerator {

    // output manifest information to .txt file
    public static void outputManifest(){

        //call readManifest function that extracts permissions and content providers informations from the APK
        HashMap manifestInfo = readManifest();

        //output the manifest info
        try{

            //create the .txt file for manifest information
            FileWriter mWriter = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - ManifestAnalysis.txt", true);

            /*
            iterating through manifestInfo object to get the list of requested permission and content providers
             */
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

    // helper method to read manifest information of an APK file
    private static HashMap<String, Object> readManifest(){

        /*
        create a hashmap object to store two attributes with
        1. list of permissions
        2. list of providers
         */
        HashMap<String, Object> toReturn = new HashMap<>();
        Set<String> permissions = null;
        List<AXmlNode> providers = null;

        try {

            //get the manifest of the APK using InfoFlow library
            ProcessManifest manifest = new ProcessManifest(GlobalRef.inputDir + GlobalRef.currentApk);

            //get the requested permissions and content providers by the app
            permissions = manifest.getPermissions();
            providers = manifest.getProviders();
            List<AXmlNode> activities = manifest.getActivities();

        } catch (Exception e){
            System.err.println(e);
        }

        //store the requested values inside the hashmap
        toReturn.put("permissions", permissions);
        toReturn.put("providers", providers);

        return toReturn;
    }

    // generate Nodes file
    public static void generateNodes() {
        /*
            get all the bodies stored and output them to a - Nodes.txt file
             */
        try {


            FileWriter writer = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - Nodes.txt", true);

            //iterate through all the recorded method bodies
            Iterator iter = GlobalRef.currentNodes.entrySet().iterator();
            while(iter.hasNext()){

                Map.Entry<String, NodeInfo> entry = (Map.Entry<String, NodeInfo>) iter.next();
                writer.write(entry.getKey() + "\n");

                NodeInfo info = (NodeInfo) entry.getValue();

                Iterator iter2 = info.getPermissions().entrySet().iterator();
                while(iter2.hasNext()){

                    //write requested permissions, if exists, in each unit of the body
                    Map.Entry<String, Integer> per = (Map.Entry<String, Integer>) iter2.next();
                    writer.write(per.getKey() + " - " + per.getValue() + "\n");
                }

                //write the highest requested permission levels for each body
                writer.write("Highest permission level - " + info.highestLevel+ "\n\n");
                writer.flush();
            }

            writer.close();

            //clear the stored permission values in the GlobalRef
            GlobalRef.currentNodes.clear();

        } catch (Exception e){System.out.println(e);}
    }

    // generate Graph edges file and .dot file
    public static void generateGraph(){

        //load the Call Graph constructed by Flowdroid in the Soot Scene
        CallGraph cg = Scene.v().getCallGraph();

        //initialize a DotGraph object
        DotGraph canvas = new DotGraph("call-graph");

        //initialize iterator for iterating through the call graph
        Iterator<Edge> edges = cg.iterator();

        try {

            // create an InvocationMatrix object to store the API invocation details
            InvocationMatrix matrix = new InvocationMatrix();

            FileWriter writer = new FileWriter(GlobalRef.outputDir + GlobalRef.currentApk + " - Edges.txt", true);

            while (edges.hasNext()) {

                Edge next = edges.next();

                // get both ends of the edge
                MethodOrMethodContext src = next.getSrc();
                MethodOrMethodContext tgt = next.getTgt();

                // draw the edges on on the DotGraph
                DotGraphNode srcNode = canvas.drawNode(src.toString());
                DotGraphNode tgtNode = canvas.drawNode(tgt.toString());
                DotGraphEdge edge = canvas.drawEdge(src.toString(), tgt.toString());

                // write the ends to the .txt file
                writer.write(src.toString() + " -> " + tgt.toString() + "\n");

                // check if the target node being called is one of the permission-requiring Android APIs
                String fullClassMethod = tgt.method().getDeclaringClass().getName() + " " + tgt.method().method().getName();
                if(matrix.appMatrix.containsKey(fullClassMethod)){
                    if(matrix.appMatrix.get(fullClassMethod) == 0){
                        matrix.appMatrix.put(fullClassMethod, 1);
                    }
                }
            }

            // record the InvocationMatrix of the current APK in the GlobalRef
            GlobalRef.invocationMatrices.put(GlobalRef.currentApk, matrix);

            writer.flush();
            writer.close();

            System.out.println("Generated Dot Graph size: " + cg.size());

            String fileName = GlobalRef.outputDir + GlobalRef.currentApk + DotGraph.DOT_EXTENSION;
            canvas.plot(fileName); // output the DotGraph to .dot file

        } catch (Exception e){ System.out.println(e); }
    }

    // print out the invocation matricces of all APKs in a single CSV file
    public static void generateCSV(){

        // if the invocation matricces are not empty
        if(GlobalRef.invocationMatrices.size() > 0){

            //ArrayList of ArrayList i.e. rows
            ArrayList<ArrayList<String>> toPrint = new ArrayList<>();

            /*
             add the header row as the First arrayList
             i.e.
             [apk, API1, API2, API3, ...]
             */
            InvocationMatrix matrix = new InvocationMatrix(); //create an invocation matrix object to refer to for the header row
            ArrayList<String> topRow = new ArrayList<>();
            topRow.add("apk");
            Iterator topIter = matrix.appMatrix.entrySet().iterator();
            while(topIter.hasNext()){
                Map.Entry entry = (Map.Entry) topIter.next();
                topRow.add((String) entry.getKey());
            }

            // add the header row to the toPrint ArrayList
            toPrint.add(topRow);

            // iterate through the ArrayList of InnovcationMatrices
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
}

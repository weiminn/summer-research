package Analysis;

import Analysis.Transformers.GraphNodeReaderTransformer;
import soot.*;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
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
        String[] _args = new String[3];

        //Load path to android platforms to GlobalRef's variable
        GlobalRef.androidPlatforms = _args[2];

//        _args[0] = "-f"; _args[1] = "sample/apk/apk_selected/a.dr.br.ambedkar.hindiandenglish.read.apk"; _args[2] = "C:\\Users\\mgwei\\AppData\\Local\\Android\\Sdk\\platforms";
        _args[0] = "-d"; _args[1] = "sample/apk/dataset"; _args[2] = "C:\\Users\\mgwei\\AppData\\Local\\Android\\Sdk\\platforms";

        String ford = _args[0]; // file or directory option
        String fordV = _args[1]; // file or directory value

        //Load names of methods from Android APIs from the JSON
        AndroidAPI apis = new AndroidAPI();


        //check if the option is file or directory
        //individual file feature may not be working on Windows machine
        if(ford.equals("-f")){
            int lastSlash = fordV.lastIndexOf('/');
            String apkName = fordV.substring(lastSlash + 1);
            String dirName = fordV.substring(0, lastSlash + 1);
            analyzeAPK(apkName, dirName);
        } else if (ford.equals("-d")){
            /*
                if the option is directory
                analyze the whole directory
             */
            analyzeAPKDirectory(fordV);
        }

        //output all the APK invocation matrixes in the .csv file
        OutputGenerator.generateCSV();
    }

    // function to analyze individual APK
    private static void analyzeAPK(String apkName){

        //remember the current APK name globally
        GlobalRef.currentApk = apkName;

        /*
            Initialize soot environment
            includes source file format, output destination, etc
            also also loads classes from the APK into the scene
         */
        initializeSoot( Options.src_prec_apk, true, Options.output_format_dex,
            "analysisOutput/" + GlobalRef.ts + "/",
            true);

        /*
            Initialize Flowdroid using current Soot instance
            to get Call graph with DummyMainClass from entry points
            and DummyMainMethods for all activities
         */
        initializeFlowdroid( GlobalRef.inputDir + GlobalRef.currentApk );

    }

    // function to analyze individual APK with specified directory
    private static void analyzeAPK(String apkName, String dir){

        GlobalRef.inputDir = dir;
        analyzeAPK(apkName);

    }

    // analyze the whole directory
    private static void analyzeAPKDirectory(String dir){

        //check if the directory name is properly formatted
        if(dir.charAt(dir.length()-1) == '/'){
            GlobalRef.inputDir = dir;
        } else {
            GlobalRef.inputDir = dir + '/';
        }


        //get the names of all files in the directory
        File folder = new File(GlobalRef.inputDir);
        File[] fileList = folder.listFiles();

        //keep the filenames ending with .apk
        for(int i = 0; i < fileList.length; i++){
            if(fileList[i].getName().endsWith(".apk")){
                GlobalRef.apks.add(fileList[i].getName());
            }
        }

        //analyze all the APKs in the list
        Iterator<String> iter = GlobalRef.apks.iterator();
        while(iter.hasNext()){
            GlobalRef.currentApk = iter.next();
            analyzeAPK(GlobalRef.currentApk); //function that analyzes the individual apk
        }
    }

    // configure and initialize Soot environment
    private static void initializeSoot(int srcPrec, boolean phantomRefs, int outputFormat, String outputDir, boolean wholeProgram){

        GlobalRef.outputDir = outputDir;

        //create directory if not exists
        if (!new File(GlobalRef.outputDir).isDirectory()) { new File(GlobalRef.outputDir).mkdir(); }

        G.reset(); //reset

        //input file/directory option
        String apk = GlobalRef.inputDir + GlobalRef.currentApk;
        Options.v().set_process_dir(Collections.singletonList(apk));
        Options.v().set_src_prec(srcPrec);
        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir(GlobalRef.outputDir);

        //options for processing APK
        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_validate(true);
        Options.v().set_android_jars(GlobalRef.androidPlatforms);
        Options.v().set_allow_phantom_refs(phantomRefs);
        Options.v().set_no_bodies_for_excluded(false);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_validate(false);

        //load all the classes of the APK into the scene
        Scene.v().loadNecessaryClasses();
    }

    // configure and initialize analysis for Android analysis
    private static void initializeFlowdroid(String processDir){

        //create configuration object to be fed into Flowdroid's Application Setup
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();

        //configure Flowdroid to use the previously initialized and existing Soot instance
        config.setSootIntegrationMode(InfoflowAndroidConfiguration.SootIntegrationMode.UseExistingInstance);

        //target the current APK file
        config.getAnalysisFileConfig().setTargetAPKFile(processDir);

        //Pointer transformation for the Call graph using SPARK algorithm
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);

        //rudimentarily analyze reflection calls
        config.setEnableReflection(true);

        //setup the Dataflow analysis object with the configurations
        SetupApplication app = new SetupApplication(config);

        boolean sparkException = false;

        //try constructing call graph
        try {
            app.constructCallgraph();
        } catch (Exception e){

            //stop everything and skip to next APK if any error with call graph construction
            System.out.println("Call graph SPARK transformation failed for " + GlobalRef.currentApk + " : " + e);
            GlobalRef.sparkErrors++; //for tracking errant APKs for debuging purpose
            sparkException = true;
        }

        //if the APK call graph construction and SPARK transformation went smoothly
        if(sparkException == false){

            //write out permission and content provider requests from the manifest
            OutputGenerator.outputManifest();

            //output graph edges from the call graph
            OutputGenerator.generateGraph();

//            PhaseOptions.v().setPhaseOption("wjtp.icfg", "enabled:false");

            //add the custom transformer that reads all bodies (nodes of the graph) in all classes in the scene
            PackManager.v().getPack("jtp").add(new Transform("jtp.readbody", new GraphNodeReaderTransformer()));

            //run all transformation packs by Soot
            try { PackManager.v().runPacks(); } catch (Exception e){ System.out.println("Run Pack Exceptions: " + e); }

            //output all nodes from the methods of all classes in the scene
            OutputGenerator.generateNodes();

        }
    }

}

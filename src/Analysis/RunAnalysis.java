package Analysis;

import Analysis.Transformers.CallGraph.MySPARKTransformer;
import Analysis.Transformers.MyBodyTransformer;
import de.bodden.tamiflex.playout.ClassDumper;
import fj.Hash;
import soot.*;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.IManifestHandler;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.FileWriter;
import java.util.*;

public class RunAnalysis {
    public static void main(String[] args){

        initializeSoot(
                Options.src_prec_apk,
                true,
                "./sample/apk/" +
                        "contacts+" +
                        ".apk",
                Options.output_format_dex,
                "dex",
                true);

    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, String processDir, int outputFormat, String outputDir, boolean wholeProgram){

        soot.G.reset();

        String ts = new Date().toString();

        SetupApplication app = new SetupApplication(
                "/home/wei/Android/Sdk/platforms/",
                processDir
        );

        HashMap manifestInfo = getRequestedPermissions(processDir);

        try{
            FileWriter mWriter = new FileWriter("./analysisOutput/logs/" + ts + "-ManifestAnalysis.txt", true);

            Set<String> pSet = (Set<String>) manifestInfo.get("permissions");
            Iterator<String> pSetItr = pSet.iterator();

            List<AXmlNode> cpList = (List<AXmlNode>) manifestInfo.get("providers");
            Iterator<AXmlNode> cpListItr = cpList.iterator();

            mWriter.write("=====================================\nPermissions Requested\n-------------------------------------\n");

            while(pSetItr.hasNext()){
                mWriter.write(pSetItr.next() + "\n");
            }

            mWriter.write("\n=====================================\nContent Providers Accessed\n-------------------------------------\n");

            while(cpListItr.hasNext()){
                Map<String, AXmlAttribute<?>> attributes = cpListItr.next().getAttributes();

                String cpName = attributes.get("authorities").getValue().toString();
//                try{
//                    String prName = attributes.get("permission").getValue().toString();
//                } catch (Exception e){
//
//                }

                mWriter.write(cpName + "\n");
            }
            mWriter.close();

        } catch (Exception e){
            System.err.println(e);
        }

        Options.v().set_process_dir(Collections.singletonList(processDir));
        Options.v().set_src_prec(srcPrec);
        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir("./analysisOutput/" + outputDir + "/");

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        Options.v().set_android_jars("/home/wei/Android/Sdk/platforms/");
        Options.v().set_allow_phantom_refs(phantomRefs);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        Options.v().set_verbose(true);

        Scene.v().loadNecessaryClasses();
//        app.constructCallgraph();

//        PackManager.v().getPack("wjtp").add(
////            new Transform("wjtp.mycha", new MyCHATransformer())
//            new Transform("wjtp.mySpark", new MySPARKTransformer())
//        );
        PackManager.v().getPack("jtp").add(
                new Transform("jtp.myAnalysis", new MyBodyTransformer(ts))
        );

        PackManager.v().runPacks();
//        PackManager.v().writeOutput();

//        PointsToAnalysis pta2 = Scene.v().getPointsToAnalysis();

//        generateCallgraph();
    }

    private static HashMap<String, Object> getRequestedPermissions(String apkPath){

        HashMap<String, Object> toReturn = new HashMap<>();
        Set<String> permissions = null;
        List<AXmlNode> providers = null;

        try {
            ProcessManifest manifest = new ProcessManifest(apkPath);
            permissions = manifest.getPermissions();
            providers = manifest.getProviders();
        } catch (Exception e){
            System.err.println(e);
        }

        toReturn.put("permissions", permissions);
        toReturn.put("providers", providers);

        return toReturn;
    }

}

package Analysis;

import Analysis.Transformers.CallGraph.MyCHATransformer;
import Analysis.Transformers.MyBodyTransformer;
import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.*;

public class RunAnalysis {
    public static void main(String[] args){

//        initializeSoot(
//                Options.src_prec_apk,
//                true,
//                "./sample/apk/HelloWorld.apk",
//                Options.output_format_jimple,
//                "jimple",
//                true);

        initializeSoot(
                Options.src_prec_java,
                true,
                "./sample/java",
                Options.output_format_jimple,
                "jimple",
                true);


    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, String processDir, int outputFormat, String outputDir, boolean wholeProgram){

        G.v().reset();

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);

        Options.v().set_soot_classpath("/usr/local/lib/jdk1.7.0_80/jre/lib/rt.jar");
        Options.v().set_force_android_jar("./lib/android/android.jar");
        Options.v().set_allow_phantom_refs(phantomRefs);
        Options.v().set_no_bodies_for_excluded(true);

        if(wholeProgram){
            // Enable whole-program mode
            Options.v().set_whole_program(true);
            Options.v().set_app(true);

//            // Call-graph options
//            Options.v().setPhaseOption("cg", "safe-newinstance:true");
//            Options.v().setPhaseOption("cg.cha","enabled:false");
//
//            // Enable SPARK call-graph construction
//            Options.v().setPhaseOption("cg.spark","enabled:true");
//            Options.v().setPhaseOption("cg.spark","verbose:true");
//            Options.v().setPhaseOption("cg.spark","on-fly-cg:true");
//            Options.v().setPhaseOption("cg.spark","set-impl:array");
        }

        Options.v().set_process_dir(Collections.singletonList(processDir));
        Options.v().set_src_prec(srcPrec);

        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir("./sootOutput/" + outputDir + "/");

        Scene.v().loadNecessaryClasses();

        if(wholeProgram){
            runWholeProgramPack();
        } else {
            runPack();
        }

    }

    private static void runPack(){
        String ts = new Date().toString();
        PackManager.v().getPack("jtp").add(
                new Transform("jtp.myAnalysis", new MyBodyTransformer(ts))
        );

        PackManager.v().runPacks();
//        PackManager.v().writeOutput();
    }

    private static void runWholeProgramPack(){
        String ts = new Date().toString();
        PackManager.v().getPack("wjtp").add(
            new Transform("wjtp.mycha", new MyCHATransformer())
//            new Transform("wjtp.spark", new SPARKTransformer())
        );

        PackManager.v().runPacks();
//        PackManager.v().writeOutput();
    }

    private static void inspect(){
        SootClass sc = Scene.v().getSootClass("TestApp");
        System.out.println(sc);

        SootMethod sm = sc.getMethodByName("test");
        System.out.println(sm);

        Body body = sm.retrieveActiveBody();
        System.out.println("body");
        System.out.println(body);

        Chain units = body.getUnits();
        System.out.println(units);
    }
}

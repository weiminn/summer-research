package Analysis;

import Analysis.Transformers.CallGraph.MySPARKTransformer;
import soot.*;
import soot.jimple.infoflow.android.SetupApplication;
import soot.options.Options;
import java.util.*;

public class RunAnalysis {
    public static void main(String[] args){

        initializeSoot(
                Options.src_prec_apk,
                true,
                "./sample/apk/" +
                        "contacts+" +
                        ".apk",
                Options.output_format_jimple,
                "jimple",
                true);

    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, String processDir, int outputFormat, String outputDir, boolean wholeProgram){

        G.reset();

        SetupApplication app = new SetupApplication(
                "/home/wei/Android/Sdk/platforms/",
                processDir
        );

        soot.options.Options.v().set_process_dir(Collections.singletonList(processDir));
        soot.options.Options.v().set_src_prec(srcPrec);
        soot.options.Options.v().set_output_format(outputFormat);
        soot.options.Options.v().set_output_dir("./sootOutput/" + outputDir + "/");

        soot.options.Options.v().set_process_multiple_dex(true);
        soot.options.Options.v().set_prepend_classpath(true);
        soot.options.Options.v().set_validate(true);
        soot.options.Options.v().set_android_jars("/home/wei/Android/Sdk/platforms/");
        soot.options.Options.v().set_allow_phantom_refs(phantomRefs);
        soot.options.Options.v().set_no_bodies_for_excluded(true);
        soot.options.Options.v().set_whole_program(true);
        soot.options.Options.v().set_app(true);

        soot.options.Options.v().set_verbose(true);

        Options.v().setPhaseOption("cg.spark", "enabled:true");

        Scene.v().loadNecessaryClasses();
        app.constructCallgraph();

        PointsToAnalysis pta = Scene.v().getPointsToAnalysis();

        PackManager.v().getPack("wjtp").add(
//            new Transform("wjtp.mycha", new MyCHATransformer())
            new Transform("wjtp.mySpark", new MySPARKTransformer())
        );

//        String ts = new Date().toString();
//        PackManager.v().getPack("jtp").add(
//                new Transform("jtp.myAnalysis", new MyBodyTransformer(ts))
//        );

        Collection<Pack> packs = PackManager.v().allPacks();
        PackManager.v().runPacks();

        PointsToAnalysis pta2 = Scene.v().getPointsToAnalysis();

//        generateCallgraph();
    }
}

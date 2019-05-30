package Analysis;

import Analysis.Transformers.MySceneTransformer;
import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Collections;
import java.util.Map;

public class RunAnalysis {
    public static void main(String[] args){

        initializeSoot(
                Options.src_prec_apk,
                true,
                "./sample/apk/HelloWorld.apk",
                Options.output_format_jimple,
                "jimple");

        runPack();

    }

    private static void initializeSoot(int srcPrec, boolean phantomRefs, String processDir, int outputFormat, String outputDir){

        G.v().reset();

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);

        Options.v().set_soot_classpath("/usr/local/lib/jdk1.7.0_80/jre/lib/rt.jar");
        Options.v().set_force_android_jar("./lib/android/android.jar");
        Options.v().set_allow_phantom_refs(phantomRefs);

        Options.v().set_process_dir(Collections.singletonList(processDir));
        Options.v().set_src_prec(srcPrec);

        Options.v().set_output_format(outputFormat);
        Options.v().set_output_dir("./sootOutput/" + outputDir + "/");

        Scene.v().loadNecessaryClasses();

    }

    private static void runPack(){
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myAnalysis", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {
                //new code in for customized jimple transformation
                G.v().out.println(map);
            }
        }));

        PackManager.v().runPacks();
        PackManager.v().writeOutput();
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

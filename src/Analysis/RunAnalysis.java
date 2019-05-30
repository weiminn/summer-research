package Analysis;

import Analysis.Transformers.MySceneTransformer;
import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Collections;
import java.util.Map;

public class RunAnalysis {
    public static void main(String[] args){

        initializeSoot();


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

    public static void initializeSoot(){

        G.v().reset();

        Options.v().set_process_multiple_dex(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);

        //android apk process
//        Options.v().set_output_format(Options.output_format_class);
//        Options.v().set_process_dir(Collections.singletonList("./sample/apk/demo.apk"));
//        Options.v().set_force_android_jar("./lib/android/android.jar");
//        Options.v().set_src_prec(Options.src_prec_apk);

        Options.v().set_soot_classpath("/usr/local/lib/jdk1.7.0_80/jre/lib/rt.jar");
        Options.v().set_process_dir(Collections.singletonList("./sample/source"));
        Options.v().set_src_prec(Options.src_prec_java);

        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_output_dir("./sootOutputCustom/");

        Scene.v().loadNecessaryClasses();

    }

    public static void runPack(){
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myAnalysis", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {
                //new code in for customized jimple transformation
            }
        }));

        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }
}

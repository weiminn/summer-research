package Analysis;

import soot.*;
import soot.dava.DavaPrinter;
import soot.jimple.JasminClass;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.util.Chain;
import soot.util.JasminOutputStream;

import java.io.*;
import java.util.Arrays;

public class GenerateSootClass {
    private static int oFormat = Options.output_format_jimple;

    public static void main(String[] args){


        SootClass sClass = generateClass("main");
        write(sClass, oFormat);

        System.out.println("Done!");
    }

    private static SootClass generateClass(String cName){

        Scene.v().loadClassAndSupport("java.lang.Object");
        Scene.v().loadClassAndSupport("java.lang.System");

        SootClass sClass = new SootClass(cName, Modifier.PUBLIC);
        sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
        Scene.v().addClass(sClass);

        generateMainMethod(sClass);

        return sClass;
    }

    private static void generateMainMethod(SootClass sClass){
        SootMethod sMethod = new SootMethod("main",
                Arrays.asList(new Type[]{ArrayType.v(RefType.v("java.lang.String"), 1)}),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);

        sClass.addMethod(sMethod);

        JimpleBody jimpleBody = createJimpleBody(sMethod);
        sMethod.setActiveBody(jimpleBody);
    }

//    private static void generateCustomMethod(SootClass sClass, String methodName){
//        SootMethod sMethod = new SootMethod("main",
//                Arrays.asList(new Type[]{ArrayType.v(RefType.v("java.lang.String"), 1)}),
//                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
//
//        sClass.addMethod(sMethod);
//
//        JimpleBody jimpleBody = createJimpleBody(sMethod);
//        sMethod.setActiveBody(jimpleBody);
//    }

    private static JimpleBody createJimpleBody(SootMethod method){
        JimpleBody body = Jimple.v().newBody(method);

        method.setActiveBody(body);

        //locals chain
        Chain locals = body.getLocals();

        //local for holding method arguments
        Local arg = Jimple.v().newLocal("l0",
                ArrayType.v(RefType.v("java.lang.String"), 1));
        locals.add(arg);

        //local for holding print stream out
        Local tmpRef = Jimple.v().newLocal("tmpRef",
                RefType.v("java.io.PrintStream"));
        locals.add(tmpRef);

        //unit chain
        Chain units = body.getUnits();

        //unit for method that assigns formal params to local arguments
        Unit fpUnit =  Jimple.v().newIdentityStmt(arg,
                Jimple.v().newParameterRef(ArrayType.v
                        (RefType.v("java.lang.String"), 1), 0));
        units.add(fpUnit);

        //create soot method call that calls println
        SootMethod toCall = Scene.v().getMethod(
                "<java.io.PrintStream: void println(java.lang.String)>");
        Unit cUnit = Jimple.v().newInvokeStmt(
                Jimple.v().newVirtualInvokeExpr(
                        tmpRef, toCall.makeRef(), StringConstant.v("Hello, World!")
                ));
        units.add(cUnit);

        units.add(Jimple.v().newReturnVoidStmt());

        return body;
    }


    private static void write(SootClass sClass, int output_format) {
        OutputStream streamOut = null;
        try {
            String filename = SourceLocator.v().getFileNameFor(sClass, output_format);
            if (output_format == Options.output_format_class)
                streamOut = new JasminOutputStream(new FileOutputStream(filename));
            else
                streamOut = new FileOutputStream(filename);
            PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
            if (output_format == Options.output_format_class) {
                JasminClass jasClass = new JasminClass(sClass);
                jasClass.print(writerOut);
            } else if (output_format == Options.output_format_jimple)
                Printer.v().printTo(sClass, writerOut);
            else if (output_format == Options.output_format_dava)
                DavaPrinter.v().printTo(sClass, writerOut);
            writerOut.flush();
            writerOut.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (streamOut != null)
                try {
                    streamOut.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
        }
    }
}

package Analysis.Transformers;

import soot.*;
import soot.jimple.*;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.internal.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.UnitGraph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ICFGTransformer extends SceneTransformer {

    protected void internalTransform(String phaseName, Map<String, String> map) {

        System.out.println("Analyzing Interprocedural CFG");

        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(true, true);
        Set<Unit> startNodes = icfg.allNonCallStartNodes();

        Iterator iter = startNodes.iterator();
        while (iter.hasNext()){
            Object node = iter.next();
            try {
                String toPrint = getInvokeStmt(node);
                System.out.println(toPrint);
            } catch (StackOverflowError se){
                System.out.println("Stackoverflow Exception: " + node);
            }
        }
        System.out.println(icfg);
    }

    private String getInvokeStmt(Object node){
        if (JIfStmt.class.equals(node.getClass())){
            return getInvokeStmt(((JIfStmt) node).getTarget());
        }
        else if (JIdentityStmt.class.equals(node.getClass())) {
            return getInvokeStmt(((JIdentityStmt) node).rightBox.getValue());
        }
        else if (JAssignStmt.class.equals(node.getClass())) {
            return getInvokeStmt(((JAssignStmt) node).rightBox.getValue());
        }
        else if (JInvokeStmt.class.equals(node.getClass())) {
            return getInvokeStmt(((JInvokeStmt) node).getInvokeExpr().getMethod().getSignature().toString());
        }
        else if (JExitMonitorStmt.class.equals(node.getClass())) {
            return ((JExitMonitorStmt) node).toString();
        }
        else if (JReturnVoidStmt.class.equals(node.getClass())) {
            return ((JReturnVoidStmt) node).toString();
        }
        else if (JReturnStmt.class.equals(node.getClass())) {
            return ((JReturnStmt) node).toString();
        }
        else if (JThrowStmt.class.equals(node.getClass())) {
            return ((JThrowStmt) node).toString();
        }
        else if (JGotoStmt.class.equals(node.getClass())) {
            return getInvokeStmt(((JGotoStmt) node).getTarget());
        }

        else if (JNewExpr.class.equals(node.getClass())){
            return ((JNewExpr) node).getType().toString();
        }
        else if (JNewArrayExpr.class.equals(node.getClass())){
            return ((JNewArrayExpr) node).toString();
        }
        else if(JMulExpr.class.equals(node.getClass())){
            return ((JMulExpr) node).toString();
        }
        else if(JLengthExpr.class.equals(node.getClass())){
            return ((JLengthExpr) node).toString();
        }
        else if(JCastExpr.class.equals(node.getClass())){
            return ((JCastExpr) node).toString();
        }
        else if(JAddExpr.class.equals(node.getClass())){
            return ((JAddExpr) node).toString();
        }
        else if(JStaticInvokeExpr.class.equals(node.getClass())){
            return ((JStaticInvokeExpr) node).getMethod().getSignature();
        }
        else if(JInterfaceInvokeExpr.class.equals(node.getClass())){
            return ((JInterfaceInvokeExpr) node).getMethod().getSignature();
        }
        else if(JVirtualInvokeExpr.class.equals(node.getClass())){
            return ((JVirtualInvokeExpr) node).getMethod().getSignature();
        }
        else if(JSubExpr.class.equals(node.getClass())){
            return ((JSubExpr) node).getSymbol();
        }
        else if(JInstanceOfExpr.class.equals(node.getClass())){
            return node.toString();
        }


        else if (ParameterRef.class.equals(node.getClass())){
            return ((ParameterRef) node).getType().toString();
        }
        else if (JInstanceFieldRef.class.equals(node.getClass())) {
            return ((JInstanceFieldRef) node).getFieldRef().getSignature().toString();
        }
        else if (JCaughtExceptionRef.class.equals(node.getClass())){
            return ((JCaughtExceptionRef) node).toString();
        }
        else if (StaticFieldRef.class.equals(node.getClass())){
            return ((StaticFieldRef) node).toString();
        }
        else if (JArrayRef.class.equals(node.getClass())){
            return ((JArrayRef) node).toString();
        }


        else if (JimpleLocal.class.equals(node.getClass())){
            return ((JimpleLocal) node).toString();
        }
        else if(node.getClass() == StringConstant.class){
            return ((StringConstant) node).value;
        }
        else if(node.getClass() == NullConstant.class){
            return ((NullConstant) node).toString();
        }
        else if(node.getClass() == String.class){
            return ((String) node);
        }

        else{
            if (node.getClass() == AbstractCastExpr.class){
                return"Missed to log EXPRESSION: " + ((NewExpr) node).getClass().toString();
            }
            else if (node.getClass() == Ref.class){
                return"Missed to log REFERENCE: " + ((NewExpr) node).getClass().toString();
            }
            else {
                return "Missed to log STATEMENT: " + node.getClass().toString();
            }
        }
    }
}

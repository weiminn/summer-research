package Analysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GlobalRef {

    /*
    {
        className1: ArrayList of method names
        className2: ArrayList of method names
        ...
     */
    public static HashMap<String, ArrayList<String>> androidApiMap = new HashMap<>();

    /*
    {
        apk1: InvocationMatrix
        apk2: InvocationMatrix
        ...
     */
    public static HashMap<String, InvocationMatrix> invocationMatrices = new HashMap<>();

    public static String ts = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

    public static String androidPlatforms; //file path to android platforms directory
    public static String inputDir; //directory to be processed
    public static ArrayList<String> apks = new ArrayList<>( ); //list of APK names in the directory to be processed
    public static String currentApk; //current APK name being processed

    public static HashMap<String, NodeInfo> currentNodes = new HashMap(); //the method body information of all the methods in the current APK being processed

    public static String outputDir;

    public static int sparkErrors = 0; // keep count of Call Graph Pointer Analysis Exceptions for recording purposes
}

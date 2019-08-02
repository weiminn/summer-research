package Analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GlobalRef {
    public static HashMap<String, ArrayList<String>> androidApiMap = new HashMap<>();
    public static HashMap<String, InvocationMatrix> invocationMatrices = new HashMap<>();

    public static String ts = new Date().toString();

    public static String inputDir;
    public static ArrayList<String> apks = new ArrayList<>( );
    public static String currentApk;

    public static HashMap<String, NodeInfo> currentNodes = new HashMap();

    public static String outputDir;
}

package Analysis;

import java.util.ArrayList;
import java.util.Date;

public class GlobalRef {
    public static String ts = new Date().toString();

    public static String inputDir;
    public static ArrayList<String> apks = new ArrayList<>( );
    public static String currentApk;

    public static StringBuilder currentNodes = new StringBuilder();

    public static String outputDir;
}

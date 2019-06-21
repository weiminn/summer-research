package Analysis.Transformers;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MyBodyTransformer extends BodyTransformer {

    private String ts;
    public MyBodyTransformer(String ts){
        this.ts = ts;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(body);
        try {

            FileWriter writer;

            SootMethod method = body.getMethod();

            String mName = method.getName();
            String cName = method.getDeclaringClass().getName();
            String returnType = method.getReturnType().toString();
            String parameters = method.getParameterTypes().toString();
            String title = cName + " | " + mName + " " + parameters + " : " + returnType;

            String toLog =
                    "=====================================\n" +
                    title +
                    "\n-------------------------------------\n";

            if(mName.equals("checkPermission")){
                //insert instrument jimple code for logging
            }

            String pLevel = "none";
            Boolean contentProvider = false;

            for(Unit u: body.getUnits()){
                Stmt stmt = (Stmt) u;
                toLog += stmt + "\n";


                String stmtStr = stmt.toString();

                if (stmtStr.contains(".permission.")){
                    int pos = stmtStr.indexOf(".permission.") + ".permission.".length();
                    String pName = "";

                    for(int i = pos; i < stmtStr.length(); i++){
                        if(stmtStr.charAt(i) == '"' || stmtStr.charAt(i) == '\\'){
                            break;
                        } else {
                            pName += stmtStr.charAt(i);
                        }
                    }

                    String _pLevel = Permissions.checkPermissionLevel(pName);
                    if(Permissions.PermissionLevels.get(_pLevel) > Permissions.PermissionLevels.get(pLevel)){
                        pLevel = _pLevel;
                    }

                    System.out.println(pName + " : " + pLevel);
                }

                if(stmtStr.contains("content://")){
                    int pos = stmtStr.indexOf("content://") + "content://".length();
                    String cpName = "";

                    for(int i = pos; i < stmtStr.length(); i++){
                        if(stmtStr.charAt(i) == '"' || stmtStr.charAt(i) == '\\'){
                            break;
                        } else {
                            cpName += stmtStr.charAt(i);
                        }
                    }

                    contentProvider = true;
                    System.out.println(cpName);
                }
            }

            writer = new FileWriter("./analysisOutput/logs/" + ts + "-" + pLevel + ".txt", true);
            writer.write(toLog + "\n");
            writer.close();

            if(contentProvider){
                writer = new FileWriter("./analysisOutput/logs/" + ts + "-ContentProviders.txt", true);
                writer.write(toLog + "\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Permissions {

        public static HashMap<String, Integer> PermissionLevels = new HashMap<String, Integer>(){
            {
                put("none", 0);
                put("normal", 1);
                put("dangerous", 2);
                put("signature", 3);
            }
        };

        private static String[] normal = {
            "ACCESS_LOCATION_EXTRA_COMMANDS",
            "ACCESS_NETWORK_STATE",
            "ACCESS_NOTIFICATION_POLICY",
            "ACCESS_WIFI_STATE",
            "BLUETOOTH",
            "BLUETOOTH_ADMIN",
            "BROADCAST_STICKY",
            "CHANGE_NETWORK_STATE",
            "CHANGE_WIFI_MULTICAST_STATE",
            "CHANGE_WIFI_STATE",
            "DISABLE_KEYGUARD",
            "EXPAND_STATUS_BAR",
            "FOREGROUND_SERVICE",
            "GET_PACKAGE_SIZE",
            "INSTALL_SHORTCUT",
            "INTERNET",
            "KILL_BACKGROUND_PROCESSES",
            "MANAGE_OWN_CALLS",
            "MODIFY_AUDIO_SETTINGS",
            "NFC",
            "READ_SYNC_SETTINGS",
            "READ_SYNC_STATS",
            "RECEIVE_BOOT_COMPLETED",
            "REORDER_TASKS",
            "REQUEST_COMPANION_RUN_IN_BACKGROUND",
            "REQUEST_COMPANION_USE_DATA_IN_BACKGROUND",
            "REQUEST_DELETE_PACKAGES",
            "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
            "SET_ALARM",
            "SET_WALLPAPER",
            "SET_WALLPAPER_HINTS",
            "TRANSMIT_IR",
            "USE_FINGERPRINT",
            "VIBRATE",
            "WAKE_LOCK",
            "WRITE_SYNC_SETTINGS"
        };

        private static String[] signature = {
            "BIND_ACCESSIBILITY_SERVICE",
            "BIND_AUTOFILL_SERVICE",
            "BIND_CARRIER_SERVICES",
            "BIND_CHOOSER_TARGET_SERVICE",
            "BIND_CONDITION_PROVIDER_SERVICE",
            "BIND_DEVICE_ADMIN",
            "BIND_DREAM_SERVICE",
            "BIND_INCALL_SERVICE",
            "BIND_INPUT_METHOD",
            "BIND_MIDI_DEVICE_SERVICE",
            "BIND_NFC_SERVICE",
            "BIND_NOTIFICATION_LISTENER_SERVICE",
            "BIND_PRINT_SERVICE",
            "BIND_SCREENING_SERVICE",
            "BIND_TELECOM_CONNECTION_SERVICE",
            "BIND_TEXT_SERVICE",
            "BIND_TV_INPUT",
            "BIND_VISUAL_VOICEMAIL_SERVICE",
            "BIND_VOICE_INTERACTION",
            "BIND_VPN_SERVICE",
            "BIND_VR_LISTENER_SERVICE",
            "BIND_WALLPAPER",
            "CLEAR_APP_CACHE",
            "MANAGE_DOCUMENTS",
            "READ_VOICEMAIL",
            "REQUEST_INSTALL_PACKAGES",
            "SYSTEM_ALERT_WINDOW",
            "WRITE_SETTINGS",
            "WRITE_VOICEMAIL"
        };

        private static String[] dangerous = {
            "READ_CALENDAR",
            "WRITE_CALENDAR",
            "READ_CALL_LOG",
            "WRITE_CALL_LOG",
            "PROCESS_OUTGOING_CALLS",
            "CAMERA",
            "READ_CONTACTS",
            "WRITE_CONTACTS",
            "GET_ACCOUNTS",
            "ACCESS_FINE_LOCATION",
            "ACCESS_COARSE_LOCATION",
            "RECORD_AUDIO",
            "READ_PHONE_STATE",
            "READ_PHONE_NUMBERS",
            "CALL_PHONE",
            "ANSWER_PHONE_CALLS",
            "ADD_VOICEMAIL",
            "USE_SIP",
            "BODY_SENSORS",
            "SEND_SMS",
            "RECEIVE_SMS",
            "READ_SMS",
            "RECEIVE_WAP_PUSH",
            "RECEIVE_MMS",
            "READ_EXTERNAL_STORAGE",
            "WRITE_EXTERNAL_STORAGE"
        };

        public static String checkPermissionLevel(String pName){

            for (String normal:Permissions.normal) {
                if(pName.equals(normal)) return "normal";
            }

            for (String dangerous:Permissions.dangerous) {
                if(pName.equals(dangerous)) return "dangerous";
            }

            for (String signature:Permissions.signature) {
                if(pName.equals(signature)) return "signature";
            }

            return "none";
        }
    }
}

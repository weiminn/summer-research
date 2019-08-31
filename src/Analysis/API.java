package Analysis;

public class API {
    private String className;
    private String api;
    private String permissionText;

    public API(String cName, String api, String pText) {
        this.className = cName;
        this.api = api;
        this.permissionText = pText;
    }

    public String getApi() {
        return api;
    }

    public String getClassName() {
        return className;
    }
}

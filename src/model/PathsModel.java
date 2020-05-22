package model;

public class PathsModel {

    private static String tomcatPath;
    private static String projectPath;

    public static String getTomcatPath() {
        return tomcatPath;
    }

    public static void setTomcatPath(String tomcatpath) {
        tomcatPath = tomcatpath;
    }

    public static String getProjectPath() {
        return projectPath;
    }

    public static void setProjectPath(String projectpath) {
        projectPath = projectpath;
    }
}

package io.github.gafarrell;

public class Debug {
    private static boolean debugActive = false;

    public static void toggleDebug(){
        debugActive = !debugActive;

        if (debugActive)
            System.out.println("Debug mode activated.");
        else
            System.out.println("Debug mode deactivated.");
    }

    public static void writeLine(String s){
        if (debugActive)
            System.out.println(s);

    }

    public static void writeLine(Object o){
        if (debugActive)
            System.out.println(o);
    }

    public static void writeArray(Object[] arr){
        if (!debugActive) return;
        for (Object o : arr)
            System.out.println(o);
    }

    public static void writeArray(float[] arr){
        if (!debugActive) return;
        for (float o : arr)
            System.out.println(o);
    }

    public static void writeArray(int[] arr){
        if (!debugActive) return;
        for (int o : arr)
            System.out.println(o);
    }
}

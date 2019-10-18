package Utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Bool {
    @Contract(value = "var.length==0 → false")
    public static boolean or(@NotNull boolean... var){
        boolean res=var.length==0?false:var[0];
        int i = 1;
        while (i < var.length) {
            res|=var[i];
            i++;
        }
        return res;
    }

    @Contract(value = "var.length==0 → false")
    public static boolean and(@NotNull boolean... var){
        boolean res=var.length==0?false:var[0];
        int i = 1;
        while (i < var.length) {
            res&=var[i];
            i++;
        }
        return res;
    }

    @Contract(value = "var.length==0 → false")
    public static boolean xor(@NotNull boolean... var){
        return !sor(var);
    }

    @Contract(value = "var.length==0 → false")
    public static boolean sor(@NotNull boolean... var){
        if(var.length==0)
            return false;
        int i = 1;
        while (i < var.length) {
            if(var[0]!=var[i])
                return false;
            i++;
        }
        return true;
    }
}

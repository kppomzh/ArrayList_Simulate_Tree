package Utils;

import Tree_Span.BranchTreeRoot;

import java.util.Arrays;
import java.util.List;

public class LanguageTreePrinter {
    public static String printf(BranchTreeRoot node,int level){
        StringBuilder sb=new StringBuilder();
//        boolean[] newLevel=new boolean[level.length+1];
//        System.arraycopy(level,0,newLevel,0,level.length);
        sb.append(appends(node.getBranchName(),level));

        List<String> attrs=node.GetAttributes();
        for(String attr:attrs){
            sb.append(appends(attr,level+1));
        }

        for(BranchTreeRoot childNode:node.getChilds()){
            sb.append(printf(node,level+1));
        }

        return sb.toString();
    }

    private static String appends(String s,int level){
        StringBuilder sb=new StringBuilder();

        for (int i = 0; i < level-1; i++) {
            sb.append("| ");
        }
        if(level>0){
            sb.append("|-");
        }
        sb.append(s);

        return sb.toString();
    }

    private static String getPrintPart(int number){
        switch(number){
            case 1:
                return "|-";
            case 2:
                return "| ";
            default:
                return "  ";
        }
    }
}

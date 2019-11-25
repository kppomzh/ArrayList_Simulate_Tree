package Utils;

import Tree_Span.BranchTreeRoot;

public class LanguageTreePrinter {
    public static String printf(BranchTreeRoot node,int level){
        if(node==null){
            return "";
        }

        StringBuilder sb=new StringBuilder();
        sb.append(appends(node.getBranchName(),level));
        sb.append('\n');

//        for(BranchTreeRoot childNode:node.getWordsQueue()){
        for(BranchTreeRoot childNode:node.getChilds()){
            sb.append(printf(childNode,level+1));
            sb.append('\n');
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

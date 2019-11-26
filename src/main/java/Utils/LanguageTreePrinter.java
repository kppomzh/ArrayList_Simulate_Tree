package Utils;

import Tree_Span.BranchTreeRoot;

public class LanguageTreePrinter {
    public static String printf(BranchTreeRoot node,int level){
        if(node==null){
            return "";
        }

        StringBuilder sb=new StringBuilder();
        if(node.getClass().getSimpleName().equals("Word")){
            sb.append(appends(node.toString(),level));
            sb.append('\n');
        }else {
            sb.append(appends(node.getBranchName(),level));
            sb.append('\n');

            for (BranchTreeRoot childNode : node.getWordsQueue()) {
                sb.append(printf(childNode, level + 1));
            }
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

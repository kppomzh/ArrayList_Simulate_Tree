package Exceptions.ASTError.Impl;

import Exceptions.ASTError.ASTBaseException;
import Tree_Span.BranchTreeRoot;

public class makeBranchTreeException extends ASTBaseException {
    public makeBranchTreeException(BranchTreeRoot root, String positionMember, BranchTreeRoot node) {
        errMessage.append("当前节点是 ");
        errMessage.append(root.getBranchName());
        errMessage.append('\n');

        errMessage.append("此处理应添加的节点是 ");
        errMessage.append(positionMember);
        errMessage.append('\n');

        errMessage.append("目前Parser分析器计划添加的节点是 ");
        errMessage.append(node.getBranchName());
    }
}

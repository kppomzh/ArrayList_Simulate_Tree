package Exceptions.GrammerMakerError.Impl;

import Exceptions.GrammerMakerError.GrammerBaseException;

import javax.tools.Diagnostic;

public class CompileException extends GrammerBaseException {
    public CompileException(){
        super();
        super.append("AST类编译失败，如下文件/类报错:\n");
    }

    public void addExceptionClass(Diagnostic d){
        super.append("类型名：");
        super.append(d.getCode());
        super.append("\n");

        super.append("报错位置：");
        super.append(d.getStartPosition());
        super.append(",");
        super.append(d.getEndPosition());
        super.append("\n");

        super.append("报错信息：");
        super.append(d.getMessage(null));
        super.append("\n");
        super.append("\n");
    }
}

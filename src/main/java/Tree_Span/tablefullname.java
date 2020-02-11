package Tree_Span;

import Exceptions.ASTError.ASTBaseException;
import Tree_Span.BranchTreeRoot;
import bean.Word;
import java.lang.Override;
import java.lang.String;

public class tablefullname extends BranchTreeRoot {
  public Word tablespacename;

  public Word NormalTAG;

  public tablefullname(String... a) {
    branchName="tablefullname";
    tablespacename=null;
    NormalTAG=null;
  }

  @Override
  public void addChild(BranchTreeRoot child) throws ASTBaseException {
    super.addChild(child);
//    switch(child.getBranchName()) {
//      case "NormalTAG":
//      tablespacename=(Word)child;
//      break;
//      case "NormalTAG":
//      NormalTAG=(Word)child;
//      break;
//    }
  }

  @Override
  public Word GetAttribute(String attr) throws ASTBaseException {
    Word res=null;
    int index=super.wordsQueueSequence.indexOf(attr);
    if(index!=-1)
      res=(Word)super.wordsQueue.get(index);
    return res;
  }
}

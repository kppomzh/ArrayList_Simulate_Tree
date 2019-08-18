package junit;

import Exceptions.LexError.LexBaseException;
import Lex.Lex;
import bean.Word;
import org.junit.Test;

import java.util.List;

public class LexTest {
    public static String[] sqls={
//            "select \"MainID\",id pid,name pname from first where id=3 or name='kppom';",
            "select tra,rbg,dsf,public.fisrt.dsa \n" +
                    "from fisrt,(select *from second) second \n" +
                    "where fisrt.name is null;"
            ,"create Table first (id int,name StrINg,time double);"
            ,"--(5)alter\n" +
            "alter table FIRST\n" +
            "add column mda string;\n" +
            "update first\n" +
            "set mda='oui'\n" +
            "where id<10;"
            ,"select * from first\n" +
            "union\n" +
            "select id pid,name pname,time from first\n" +
            "where id=3 --测试union语法解析\n" +
            "or name='kppom'; "
            ,"select count(*) from /*测试括号注释*/ testtable"
    };

    //nowindex定位问题未解决
    @Test
    public void testLexFunction(){

        try {
            for(String s:sqls) {
                Lex lex=new Lex();
                List<Word> list = lex.getWords(s);

                for (Word w : list) {
                    System.out.print(w.getName());
                    System.out.print(' ');
                    System.out.print(w.getSubstance());
                    System.out.print(' ');
                    System.out.print(w.getStayline());
                    System.out.print(',');
                    System.out.println(w.getStaylist());
                }
                System.out.println("###########################################################################");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        } catch (LexBaseException e) {
            e.printStackTrace();
        }
    }
}

package Lex;

public class LexRule {
    private int[] charMap;

    public LexRule(int[] charMapfromReader){
        charMap=charMapfromReader;
//        charMap=new int[129];
//        for (int i = 0; i < charMap.length; i++) {
//            charMap[i]=-1;
//        }
//
//        //charMap[]=;
//        charMap[59]=-2;//; 结束分析
//
//        charMap[32]=0;
//        charMap[12]=0;
//        charMap[13]=0;
//        charMap[10]=0;
//
//        charMap[33]=4;
//        charMap[37]=4;
//        charMap[38]=4;
//        charMap[42]=4;
//        charMap[43]=4;//+
//        charMap[44]=4;
//        charMap[45]=4;//-
//        charMap[46]=6;//多义符号 .
//        charMap[47]=4;
//        charMap[58]=4;
//        charMap[60]=4;
//        charMap[61]=4;
//        charMap[62]=4;
//        charMap[63]=4;
//        charMap[92]=4;
//        charMap[124]=4;
//
//        charMap[48]=2;
//        charMap[49]=2;
//        charMap[50]=2;
//        charMap[51]=2;
//        charMap[52]=2;
//        charMap[53]=2;
//        charMap[54]=2;
//        charMap[55]=2;
//        charMap[56]=2;
//        charMap[57]=2;
//
//        charMap[65]=1;
//        charMap[66]=1;
//        charMap[67]=1;
//        charMap[68]=1;
//        charMap[69]=1;
//        charMap[70]=1;
//        charMap[71]=1;
//        charMap[72]=1;
//        charMap[73]=1;
//        charMap[74]=1;
//        charMap[75]=1;
//        charMap[76]=1;
//        charMap[77]=1;
//        charMap[78]=1;
//        charMap[79]=1;
//        charMap[80]=1;
//        charMap[81]=1;
//        charMap[82]=1;
//        charMap[83]=1;
//        charMap[84]=1;
//        charMap[85]=1;
//        charMap[86]=1;
//        charMap[87]=1;
//        charMap[88]=1;
//        charMap[89]=1;
//        charMap[90]=1;
//
//        charMap[95]=1;
//
//        charMap[97]=1;
//        charMap[98]=1;
//        charMap[99]=1;
//        charMap[100]=1;
//        charMap[101]=1;
//        charMap[102]=1;
//        charMap[103]=1;
//        charMap[104]=1;
//        charMap[105]=1;
//        charMap[106]=1;
//        charMap[107]=1;
//        charMap[108]=1;
//        charMap[109]=1;
//        charMap[110]=1;
//        charMap[111]=1;
//        charMap[112]=1;
//        charMap[113]=1;
//        charMap[114]=1;
//        charMap[115]=1;
//        charMap[116]=1;
//        charMap[117]=1;
//        charMap[118]=1;
//        charMap[119]=1;
//        charMap[120]=1;
//        charMap[121]=1;
//        charMap[122]=1;
//
//        //中括号与小括号
//        charMap[91]=5;
//        charMap[93]=5;
//        charMap[40]=5;
//        charMap[41]=5;
//
//        charMap[34]=9;
//        charMap[39]=3;
//        charMap[92]=8;
    }

    public int getCharStatus(char c){
        if(c>=0&&c<=127)
            return charMap[c];
        else
            return 1;
    }
}

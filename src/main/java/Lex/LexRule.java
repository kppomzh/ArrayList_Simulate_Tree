package Lex;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LexRule implements Serializable {
    private int[] charMap;
    private List<String> EOFStrings;

    public LexRule(int[] charMapfromReader){
        charMap=charMapfromReader;

        EOFStrings=new LinkedList();
        for(int loop=0;loop<charMap.length;loop++){
            if(charMap[loop]==-2){
                EOFStrings.add(String.valueOf(charMap[loop]));
            }
        }
    }

    public int getCharStatus(char c){
        if(c>=0&&c<=127)
            return charMap[c];
        else
            return 1;
    }

    public List<String> getEOFStrings(){
        return EOFStrings;
    }
}

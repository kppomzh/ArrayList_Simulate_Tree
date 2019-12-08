package Utils;

import bean.KVEntryImpl;

import java.io.*;
import java.nio.channels.FileLockInterruptionException;
import java.util.*;

public class GrammerFileReader {
    private String baseDir;

    public GrammerFileReader(String baseDir){
        if(new File(baseDir).isDirectory()){
            this.baseDir=new File(baseDir).getPath()+'/';
        }
    }

    public Collection<String> getLinesinFile(String fileName) throws IOException {
        File file=new File(baseDir+fileName);
        if(file.isFile()){
            if (!file.canRead()) {
                throw new FileLockInterruptionException();
            }
        }
        else{
            throw new FileNotFoundException(baseDir+fileName);
        }

        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        Set<String> lines=new HashSet<>();
        while(br.ready()){
            //按行读取
            String line=br.readLine().strip();
            if(line.equals("")||line.charAt(0)=='#'){
                continue;
            }
            lines.add(line);
        }

        return lines;
    }

    public static Map<String,String> makeBaseGrammer(Collection<String> lines){
        Map<String,String> res=new HashMap<>();
        for(String mark:lines){
            int splitINdex=mark.lastIndexOf(':');
            res.put(mark.substring(0,splitINdex).strip(),mark.substring(splitINdex+1).strip());
        }
        return res;
    }

    public static Map<String,String> makeConbinationGrammer(Collection<String> lines){
        Map<String,String> prop=new HashMap<>();
        for(String rule:lines){
            int splitINdex=rule.indexOf(':');
            if(splitINdex>0)
                prop.put(rule.substring(0,splitINdex).strip(),rule.substring(splitINdex+1).strip());
            else{
                StringBuilder sb=new StringBuilder(rule);
                for(int i=1;i<sb.length();i=i+2){
                    sb.insert(i,' ');
                }
                prop.put(rule,sb.toString());
            }
        }
        return prop;
    }
}

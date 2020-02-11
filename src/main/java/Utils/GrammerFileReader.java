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

    /**
     * 用于词法定义文件的解析，词法定义可以不包含组成关键字的符号的分隔符(:)
     * 做为词法来说，每个单词只能由唯一一种表示方式，所以在这里用Map来处理比较方便。
     * @param lines
     * @return
     */
    public static Map<String,String> makeBaseGrammer(Collection<String> lines){
        Map<String,String> res=new HashMap<>();
        for(String mark:lines){
            int splitINdex=mark.lastIndexOf(':');
            if(splitINdex>0)
                res.put(mark.substring(0,splitINdex).strip(),mark.substring(splitINdex+1).strip());
            else{
                StringBuilder sb=new StringBuilder(mark);
                for(int i=1;i<sb.length();i=i+2){
                    sb.insert(i,' ');
                }
                res.put(mark,sb.toString());
            }
        }
        return res;
    }

    /**
     * 用于语法定义文件的解析
     * @param lines
     * @return
     */
    public static List<KVEntryImpl<String,String>> makeConbinationGrammer(Collection<String> lines){
        List<KVEntryImpl<String,String>> prop=new LinkedList<>();
        for(String rule:lines){
            int splitINdex=rule.indexOf(':');
            prop.add(new KVEntryImpl<>(rule.substring(0,splitINdex).strip(),rule.substring(splitINdex+1).strip()));
        }
        return prop;
    }
}

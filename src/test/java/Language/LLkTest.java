package Language;

import Exceptions.GrammerMakerError.GrammerBaseException;
import Exceptions.GrammerMakerError.Impl.FollowDebugException;
import Exceptions.GrammerMakerError.Impl.GrammerUndefined;
import Exceptions.LexError.LexBaseException;
import Exceptions.ParserError.ParserBaseException;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class LLkTest {
    @Test
    public void LLk() throws IOException, InstantiationException, InvocationTargetException, LexBaseException, GrammerBaseException, ParserBaseException, IllegalAccessException, GrammerUndefined, FollowDebugException, ClassNotFoundException {
        ReaderDemo readerDemo=new ReaderDemo();
        readerDemo.Analysis(List.of("LLk"));
        readerDemo.runSQLandCheck("select a.b.c from d.e");
    }
}

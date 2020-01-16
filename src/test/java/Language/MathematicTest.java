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

public class MathematicTest {
    @Test
    public void Mathematic() throws IOException, InstantiationException, InvocationTargetException, LexBaseException, GrammerBaseException, ParserBaseException, IllegalAccessException, GrammerUndefined, FollowDebugException, ClassNotFoundException {
        ReaderDemo readerDemo=new ReaderDemo();
        readerDemo.Analysis(List.of("Caculater"));
        readerDemo.runSQLandCheck("1+abs(2-3*4)");
    }
}

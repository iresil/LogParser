package logParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import logParser.dataModel.RequestEntity;
import logParser.util.LogParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpringBootTest
public class LogParserTests {
    @Test
    void parseEntryNullInput_ThrowsException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);

        Throwable thrown = catchThrowable(() -> method.invoke(parser, new Object[]{ null }));

        assertThat(thrown).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void parseEntryInvalidInput_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "test";

        assertThat(method.invoke(parser, input).equals(new RequestEntity()));
    }

    @Test
    void parseEntryMissingHttpVerb_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "pipe1.nyc.pipeline.com - - [01/Aug/1995:00:12:37 -0400] \"GET /history/apollo/apollo-13/apollo-13-patch-small.gif\" 200 12859";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(new RequestEntity()));
    }

    @Test
    void parseEntryInvalidHost_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "derec - - [01/Aug/1995:11:53:44 -0400] \"GET /ksc.html HTTP/1.0\" 200 7280";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(new RequestEntity()));
    }

    @Test
    void parseEntryResponseCodeNotNumeric_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "pipe1.nyc.pipeline.com - - [01/Aug/1995:00:12:37 -0400] \"GET /history/apollo/apollo-13/apollo-13-patch-small.gif\" - 12859";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(new RequestEntity()));
    }

    @Test
    void parseEntryMissingHost_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = " - - [01/Aug/1995:11:53:44 -0400] \"GET /ksc.html HTTP/1.0\" 200 7280";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(new RequestEntity()));
    }

    @Test
    void parseEntry404_ReturnsNewRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "199.1.60.95 - - [13/Aug/1995:21:48:56 -0400] \"GET /://spacelink.msfc.nasa.gov HTTP/1.0\" 404 -";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(new RequestEntity()));
    }

    @Test
    void parseEntryValidInput_ReturnsValidRequestModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogParser parser = new LogParser();
        Method method = parser.getClass().getDeclaredMethod("parseEntry", String.class );
        method.setAccessible(true);
        String input = "in24.inetnebr.com - - [01/Aug/1995:00:00:01 -0400] \"GET /shuttle/missions/sts-68/news/sts-68-mcc-05.txt HTTP/1.0\" 200 1839";

        Object result = method.invoke(parser, input);

        assertThat(result.getClass().equals(RequestEntity.class));
        assertThat(((RequestEntity)result).getHost().equals("in24.inetnebr.com"));
        assertThat(((RequestEntity)result).getHttpVerb().equals("GET"));
        assertThat(((RequestEntity)result).getResource().equals("/shuttle/missions/sts-68/news/sts-68-mcc-05.txt"));
        assertThat(((RequestEntity)result).getResponseCode().equals("200"));
    }
}

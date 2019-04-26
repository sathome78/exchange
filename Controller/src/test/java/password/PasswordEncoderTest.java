package password;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.impl.EncoderImpl;
import service.interfaces.Encoder;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PasswordEncoderTest {

    @Parameterized.Parameters(name = " {index}. {0} ")
    public static Collection<Object[]> data() {
        return new ArrayList<Object[]>() {
            {
                add(new Object[]{"titomilla@gmail.com", "titomilla98521@#", "3EAM8l91s@s&kans(JlkAJ8bbt^1@sad@daCSvcx", "1rr1UfytnhNuN04GK4g7XA=="});
                add(new Object[]{"jtoewe@gmail.com", "jtoewe78523@#", "sjadkslAJKDASJDLjL&*(k;lJS()*I()8:LASKD()80;l", "udzziVRegpO9dNRayT4nUg=="});
            }
        };
    }

    @Parameterized.Parameter()
    public String email;

    @Parameterized.Parameter(1)
    public String password;

    @Parameterized.Parameter(2)
    public String hash;

    @Parameterized.Parameter(3)
    public String result;

    Encoder encoder = new EncoderImpl();

    @Test
    public void test() {
        Assert.assertEquals(encoder.encode(password, hash), result);
    }

}

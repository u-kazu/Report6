package example.jbot.slack;


import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;

import static org.junit.Assert.assertThat;

/**
 * @author ramswaroop
 * @version 20/06/2016
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class SlackBotTest {
    @org.junit.jupiter.api.Test
    String week1() {
        Calendar now = Calendar.getInstance();

        String[] week = new String[7];
        week[0] = "日";
        week[1] = "月";
        week[2] = "火";
        week[3] = "水";
        week[4] = "木";
        week[5] = "金";
        week[6] = "土";

        int this_week = now.get(now.DAY_OF_WEEK);
        String d = week[this_week - 1];
        return d;
    }
}
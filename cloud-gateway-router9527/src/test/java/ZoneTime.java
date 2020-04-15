import java.time.ZonedDateTime;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/15 11:44
 * @Description:
 */
public class ZoneTime {

    public static void main(String[] args) {
        ZonedDateTime now = ZonedDateTime.now();
        // 2020-04-15T11:46:19.781+08:00[Asia/Shanghai]
        System.out.println(now);

    }
}

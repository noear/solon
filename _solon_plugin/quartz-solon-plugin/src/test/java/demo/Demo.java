package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.quartz.EnableQuartz;
import org.noear.solon.extend.quartz.Quartz;

import java.util.Date;

/**
 * @author noear 2022/11/24 created
 */
@EnableQuartz
public class Demo {
    public static void main(String[] args) {
        Solon.start(Demo.class, args);
    }
}

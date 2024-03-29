package webapp.dso;

import freemarker.template.Configuration;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.InitializingBean;

/**
 * @author noear 2021/8/28 created
 */
@Component
public class AppInitializingImpl implements InitializingBean {
    @Override
    public void afterInjection() throws Throwable {
        Solon.app().onEvent(Configuration.class, cfg -> {
            System.out.println("%%%%%%%%%%%%%%%%%%");
            cfg.setSetting("classic_compatible", "true");
        });
    }
}

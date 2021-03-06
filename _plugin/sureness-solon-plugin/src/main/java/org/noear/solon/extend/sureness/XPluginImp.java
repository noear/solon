package org.noear.solon.extend.sureness;

import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.sureness.integration.SurenessConfiguration;
import com.usthe.sureness.subject.SubjectSum;
import com.usthe.sureness.util.JsonWebTokenUtil;
import com.usthe.sureness.util.SurenessContextHolder;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * @author noear
 * @author tomsun28
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanScan(SurenessConfiguration.class);

        // issue jwt rest api
        app.get("/auth/token", ctx -> {
            SubjectSum subjectSum = SurenessContextHolder.getBindSubject();

            if (subjectSum == null) {
                ctx.render(Result.failure("Please auth!"));
            } else {
                String principal = (String) subjectSum.getPrincipal();
                List<String> roles = (List<String>) subjectSum.getRoles();
                // issue jwt
                String jwt = JsonWebTokenUtil.issueJwt(UUID.randomUUID().toString(), principal,
                        "token-server", 3600L, roles);
                ctx.render(Result.succeed(jwt));
            }
        });

        app.after("**", context -> SurenessContextHolder.unbindSubject());
    }
}

package org.kie.workbench.common.screens.home.client.widgets.shortcut.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

/**
 * Exposes HomeModelAuthorizationManager API in JavaScript
 */
@ApplicationScoped
public class HomeModelAuthorizationManager {

    private User user;
    private AuthorizationManager authorizationManager;

    private static HomeModelAuthorizationManager self;

    public HomeModelAuthorizationManager() {
    }

    @Inject
    public HomeModelAuthorizationManager(final User user, final AuthorizationManager authorizationManager) {
        this.user = user;
        this.authorizationManager = authorizationManager;
    }

    public void setup() {
        self = this;
        expose();
    }

    public static boolean nativeAuthorizePerspective(final String perspectiveId) {
        return self.authorizePerspective(perspectiveId);
    }

    public native void expose() /*-{
        $wnd.AppFormer.HomeModelAuthorizationManager = {
            authorize: function (perspectiveId) {
                return @org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.HomeModelAuthorizationManager::nativeAuthorizePerspective(Ljava/lang/String;)(perspectiveId);
            }
        };
    }-*/;

    public boolean authorizePerspective(final String perspectiveId) {
        return this.authorizationManager.authorize(new ResourceRef(perspectiveId, PERSPECTIVE), user);
    }
}

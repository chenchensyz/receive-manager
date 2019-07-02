package cn.com.cyber.config.shiro;

import cn.com.cyber.model.SysPermission;
import cn.com.cyber.model.SysUser;
import cn.com.cyber.service.CompanyInfoService;
import cn.com.cyber.service.SysPermissionService;
import cn.com.cyber.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ShiroDbRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroDbRealm.class);

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        LOGGER.debug("doGetAuthenticationInfo:" + authcToken);
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        SysUser user = sysUserService.getByUserId(token.getUsername());
        if (user == null) {
            return null;
        }
        if (user.getState() == 0) {
            throw new DisabledAccountException();
        }
        boolean owner = false;
        if (user.getSource() == 1
                && companyInfoService.getCountByOwner(user.getUserId(), user.getCompanyId()) > 0) {
            owner = true;
        }
        return new SimpleAuthenticationInfo(
                new ShiroUser(user.getId(), user.getUserId(), user.getNickName(), user.getSource(), user.getCompanyId(), owner, null), user.getPassword(), getName());
    }

    public void cleanUserSession(String username) {
        Session sessionCurr = SecurityUtils.getSubject().getSession();
        LOGGER.info("currSession:{}", sessionCurr.getId());
        //apache shiro获取所有在线用户
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            String loginUsername = String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY));//获得session中已经登录用户的名字
            if (username.equals(loginUsername)) {  //这里的username也就是当前登录的username
                if (!session.getId().equals(sessionCurr.getId())) {
                    LOGGER.info("remove session id:{}", session.getId());
                    session.setTimeout(0);  //这里就把session清除，
                }
            }
        }
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LOGGER.info("doGetAuthorizationInfo");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> roles = new ArrayList<String>();
        roles.add("admin");
        info.addRoles(roles);
        return info;
    }

    /**
     * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
     */
    public static class ShiroUser implements Serializable {
        private static final long serialVersionUID = -1373760761780840081L;
        public Long id;
        public String userId;
        public String nickName;
        public int source;
        public Long companyId;
        public boolean owner;
        public List<SysPermission> permissions;

        public ShiroUser(Long id, String userId, String nickName, int source, Long companyId, boolean owner, List<SysPermission> permissions) {
            this.id = id;
            this.userId = userId;
            this.nickName = nickName;
            this.source = source;
            this.companyId = companyId;
            this.owner = owner;
            this.permissions = permissions;
        }

        public String getUserId() {
            return userId;
        }

        public List<SysPermission> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<SysPermission> permissions) {
            this.permissions = permissions;
        }

        /**
         * 本函数输出将作为默认的<shiro:principal/>输出.
         */
        @Override
        public String toString() {
            return nickName;
        }

        /**
         * 重载hashCode,只计算loginName;
         */
        @Override
        public int hashCode() {
            return Objects.hashCode(userId);
        }

        /**
         * 重载equals,只计算loginName;
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ShiroUser other = (ShiroUser) obj;
            if (userId == null) {
                if (other.userId != null) {
                    return false;
                }
            } else if (!userId.equals(other.userId)) {
                return false;
            }
            return true;
        }
    }
}
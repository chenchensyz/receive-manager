package cn.com.cyber.config.shiro;

import cn.com.cyber.model.Developer;
import cn.com.cyber.model.PermModel;
import cn.com.cyber.model.User;
import cn.com.cyber.model.UserRole;
import cn.com.cyber.service.DeveloperService;
import cn.com.cyber.service.UserRoleService;
import cn.com.cyber.service.UserService;
import cn.com.cyber.util.CodeUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShiroDbRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroDbRealm.class);

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        LOGGER.debug("doGetAuthenticationInfo:" + authcToken);
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        User user = new User();
        int source = 0;
        if ("0".equals(token.getHost())) { //后台用户
            user = userService.getByUserId(token.getUsername());
            if (user == null) {
                throw new UnknownAccountException();
            }
            if (user.getState() == 0) {
                throw new DisabledAccountException();
            }
        } else if ("1".equals(token.getHost())) {  //开发者
            Developer developer = developerService.getDeveloperByUserName(token.getUsername());
            if (developer == null) {
                throw new UnknownAccountException();
            }
            if (developer.getStatus() == 0) {
                throw new DisabledAccountException();
            }
            user.setId(developer.getId().longValue());
            user.setUserId(developer.getUserName());
            user.setNickName(developer.getName());
            user.setPassword(developer.getPassword());
            if (developer.getRoleId() == null) {
                developer.setRoleId(CodeUtil.ROLE_COMPDEVELOPER);
                setUserRole(developer);   //邦定角色
            }
            user.setRoleId(developer.getRoleId());
            source = 1;
        }
        return new SimpleAuthenticationInfo(
                new ShiroUser(user.getId(), user.getUserId(), user.getNickName(), user.getRoleId(), source, null), user.getPassword(), getName());
    }


    private void setUserRole(Developer developer) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(developer.getRoleId());
        userRole.setUserId(developer.getId().longValue());
        userRoleService.insert(userRole);
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
        public Integer roleId;
        public int source;  //用户来源 0：后台用户 1：开发者用户
        public List<PermModel> permissions;

        public ShiroUser(Long id, String userId, String nickName, Integer roleId, int source, List<PermModel> permissions) {
            this.id = id;
            this.userId = userId;
            this.nickName = nickName;
            this.roleId = roleId;
            this.source = source;
            this.permissions = permissions;
        }

        public String getUserId() {
            return userId;
        }

        public List<PermModel> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<PermModel> permissions) {
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
function userInfo() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

userInfo.prototype = {
    init: function () {
        this.initData();
        this.confirmPassword();
        this.addOrEdit();
        this.reset();
    },

    initData: function () {
        var userInfoId = $('#userInfoId').val();
        var that = this;
        if (userInfoId > 0) {
            $.get(getRootPath() + '/user/getUserInfoData?userId=' + userInfoId).then(function (res) {
                if (res.code == 0) {
                    var user = res.data.user;
                    $('.layui-hide').removeClass('layui-hide');
                    $('.password,.confirm-password').parents('.layui-form-item').remove();
                    $('.userId').val(user.userId).addClass('input-disabled');
                    $('.nickName').val(user.nickName);
                    $('.telephone').val(user.telephone);
                    $('.email').val(user.email);
                    $('.state').val(user.state == 0 ? '禁用' : '可用').addClass('input-disabled');
                    $('.createTime').val(user.createTimeStr).addClass('input-disabled');
                    $('.updateTime').val(user.updateTimeStr).addClass('input-disabled');
                    $('.input-disabled').attr('disabled', true).removeAttr('name');
                    if (user.source == 0) {
                        var existRole = '';
                        if (user.userRoles != null) {
                            $.each(user.userRoles, function (index, item) {
                                existRole += item.roleId + ',';
                            });
                        }
                        that.checkedRoles(existRole, res.data.roles)
                    } else {
                        $('.role-div').addClass('layui-hide');
                    }
                }
            });
        } else {
            $('.header-title b').html('添加用户');
            $('.password').removeClass('layui-hide');
            $('.confirm-password').removeClass('layui-hide');
            that.getRoles();
        }
    },

    confirmPassword: function (e) {
        var $this = $('.confirm-password');
        var $parent = $this.parent().parent();
        $('.confirm-password').blur(function () {
            var password = $('.password').val();
            var confirmPassword = $('.confirm-password').val();
            $parent.find('span').remove();
            if (password != confirmPassword) {
                $parent.append('<span style="color: red">两次输入密码不一致</span>');
            }
        });
    },

    addOrEdit: function () {
        this.layForm.on('submit(addOrEdit)', function (data) {
            var roles = $('.roles[type="checkbox"]:checked');
            var role = '';
            $.each(roles, function () {
                role += $(this).val() + ',';
            });
            data.field.roleIds = role;
            $.post(getRootPath() + '/user/addOrEditUser', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = history.back(-1);
                } else {
                    //弹出错误提示
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    },

    getRoles: function () {
        $.get(getRootPath() + "/role/queryRoleListData", function (res) {
            if (res.code == 0) {
                //显示角色数据
                $("#roleDiv").empty();
                $.each(res.data, function (index, item) {
                    var roleInput = $("<input class='roles' type='checkbox' name='roleId' value=" + item.id + " title=" + item.roleName + " lay-skin='primary'/>");
                    $("#roleDiv").append(roleInput);
                })
                //重新渲染下form表单 否则复选框无效
                layui.form.render('checkbox');
            } else {
                //弹出错误提示
                layer.alert("获取角色数据有误，请您稍后再试", function () {
                    layer.closeAll();
                });
            }
        });
    },

    checkedRoles: function (existRole, userRoles) {
        //显示角色数据
        $("#roleDiv").empty();
        $.each(userRoles, function (index, item) {
            var roleInput = $("<input class='roles' type='checkbox' name='roleId' value=" + item.id + " title=" + item.roleName + " lay-skin='primary'/>");
            if (existRole != '' && existRole.indexOf(item.id) >= 0) {
                roleInput = $("<input class='roles' type='checkbox' name='roleId' value=" + item.id + " title=" + item.roleName + " lay-skin='primary' checked='checked'/>");
            }
            $("#roleDiv").append(roleInput);
        });
        //重新渲染下form表单中的复选框 否则复选框选中效果无效
        // layui.form.render();
        layui.form.render('checkbox');
    },

    //重置
    reset: function () {
        $('.reset-btn').off('click').on('click', function () {
            $('input').not('.input-disabled').val("");
        });
    }
};
new userInfo();
function login() {
    var that = this;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

login.prototype = {
    init: function () {
        this.initData();
        this.loginForm();
        this.toRegiest();
    },

    initData: function () {
        $.ajax({
            url: getRootPath() + "/login/config",
            type: 'get',
            success: function (res) {
                var title = '统一资源服务管理平台';
                if (res.code == 0) {
                    title = res.data;
                }
                $('.login_name p').text(title);
                localStorage.setItem('platform_title', title);
            },
            error: function (err) {
                layer.alert(JSON.stringify(err), function () {
                    layer.closeAll();
                });
            }
        });
    },

    submitValidate: function () {
        this.layForm.verify({
            userId: function (value) {
                if (value === '') {
                    return '请输入用户名';
                }
            },
            password: function (value) {
                if (value === '') {
                    return '请输入密码';
                }
            }
        });
    },

    loginForm: function () {
        this.submitValidate();
        this.layForm.on('submit(login-btn)', function (data) {
            $.post(getRootPath() + '/login/login', data.field).then(function (res) {
                if (res.code == 0) {
                    localStorage.setItem("userId", data.field.userId)
                    localStorage.setItem("source", data.field.source)
                    top.location = getRootPath() + '/index';
                } else {
                    layer.alert(res.message);
                }
            });
            return false;
        });
    },

    toRegiest: function () {
        $('.regiest a').off('click').on('click', function () {
            location.href = getRootPath() + "/regiest/toRegiest";
        });
    }

};
new login();
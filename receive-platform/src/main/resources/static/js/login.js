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
        // $('#creater').val('你好');
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
                    sessionStorage.setItem("userId", data.field.userId)
                    sessionStorage.setItem("source", data.field.source)
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
function register() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

register.prototype = {
    init: function () {
        this.initData();
        this.registerForm();
        this.next();
        this.toLogin();
    },

    initData: function () {
        var that = this;
        that.layForm.verify({
            cofirmPwd: function (value, item) { //value：表单的值、item：表单的DOM对象
                if (value === '') {
                    return '请再次输入密码';
                }
                var password = $('.password').val();
                if (password != value) {
                    return '两次输入密码不一致';
                }
            }
        });
    },

    registerForm: function () {
        this.layForm.on('submit(registerUser)', function (data) {
            $.post(getRootPath() + '/register/registerUser', data.field).then(function (res) {
                debugger
                if (res.code == 0) {
                    top.location = getRootPath() + '/login/toLogin';
                }else {
                    layer.alert(res.message);
                }
            });
            return false;
        });
    },

    next: function () {
        $('.next-btn').off('click').on('click',function () {
            $('.company-div').attr('style','display:none');
            $('.user-div').attr('style','display:inline');
        });
        $('.before-btn').off('click').on('click',function () {
            $('.user-div').attr('style','display:none');
            $('.company-div').attr('style','display:inline');
        });
    },

    toLogin: function () {
        $('.login-btn').off('click').on('click',function () {
            location.href = getRootPath() + "/login/toLogin";
        });
    },

};
new register();
function companyUser() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

companyUser.prototype = {
    init: function () {
        this.initData();
        this.addOrEdit();
    },

    initData: function () {
        var companyUserId = $('#companyUserId').val();
        var that = this;
        if (companyUserId > 0) {
            $.get(getRootPath() + '/companyInfo/getCompanyUserData?userId=' + companyUserId).then(function (res) {
                if (res.code == 0) {
                    var user = res.data;
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
                }
            });
        } else {
            $('.header-title b').html('添加用户');
        }
    },

    addOrEdit: function () {
        this.layForm.on('submit(addOrEdit)', function (data) {
            $.post(getRootPath() + '/companyInfo/addOrEditCompanyUser', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = history.go(-1);
                } else {
                    //弹出错误提示
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    }

};
new companyUser();
function regiest() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

regiest.prototype = {
    init: function () {
        this.initData();
        this.regiestForm();
        this.next();
    },

    initData: function () {
        // $('#creater').val('你好');
    },

    regiestForm: function () {
        this.layForm.on('submit(regiestUser)', function (data) {
            $.post(getRootPath() + '/regiest/regiestUser', data.field).then(function (res) {
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
        this.submitValidate();
        $('.next-btn').off('click').on('click',function () {
            $('.company-div').attr('style','display:none');
            $('.user-div').attr('style','display:inline');
        });
        $('.before-btn').off('click').on('click',function () {
            $('.user-div').attr('style','display:none');
            $('.company-div').attr('style','display:inline');
        });
    },

    submitValidate: function () {
        this.layForm.verify({
            companyName: function (value) {
                if (value === '') {
                    return '请输入公司名称';
                }
            }
        });
    }

};
new regiest();
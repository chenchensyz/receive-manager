function companyInfo() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

companyInfo.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
        this.addOrEdit();
        this.reset();
    },

    initData: function () {
        var compId = $('#compId').val();
        if (compId != 0) {
            $.get(getRootPath() + '/companyInfo/queryCompanyInfoData?compId=' + compId).then(function (res) {
                if (res.code == 0) {
                    var data = res.data.companyInfo;
                    $('.layui-hide').removeClass('layui-hide');
                    $('.companyName').val(data.companyName);
                    $('.owner').val(data.nickName).addClass('input-disabled');
                    $('.telephone').val(data.telephone);
                    $('.email').val(data.email);
                    $('.remark').val(data.remark);
                    $('.state').val(data.state == 0 ? '禁用' : '可用').addClass('input-disabled');
                    $('.createTime').val(data.createTimeStr).addClass('input-disabled');
                    $('.updateTime').val(data.updateTimeStr).addClass('input-disabled');
                    $('.input-disabled').attr('disabled', true).removeAttr('name');
                    if (!res.data.owner && res.data.source == 1) {
                        $('.layui-input').attr('disabled', true).removeAttr('name');
                    }
                }
            });
        } else {
            $('.header-title b').html('添加权限');
        }
    },

    toAdd: function (e) {
        $('.add-btn').on('click', function () {
            var permId = $(this).parent().parent().attr("data-tt-id");
            var parentId = $(this).parent().parent().attr("data-tt-parent-id");
            location.href = getRootPath() + "/permission/getPermission?permId=" + permId + "&parentId=" + parentId;
        });
    },

    addOrEdit: function () {
        this.layForm.on('submit(addOrEdit)', function (data) {
            $.post(getRootPath() + '/companyInfo/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/companyInfo/getCompanyList?ran=' + new Date().getTime();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    },

    //重置
    reset: function (e) {
        $('.reset-btn').off('click').on('click', function () {
            $('input').not('.input-disabled').val("");
        });
    }
};
new companyInfo();
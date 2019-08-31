function appInfo() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

appInfo.prototype = {
    init: function () {
        this.initData();
        this.updateForm();
        this.reset();
    },

    initData: function () {
        var appInfoId = $('#appId').val();
        if (appInfoId > 0) {
            $.get(getRootPath() + '/appInfo/getAppInfoData?appId=' + appInfoId).then(function (res) {
                if (res.code == 0) {
                    var appInfo = res.data.appInfo;
                    $('.layui-hide').removeClass('layui-hide');
                    $('.appName').val(appInfo.appName);
                    $('.appKey').val(appInfo.appKey).addClass('input-disabled');
                    $('.urlPrefix').val(appInfo.urlPrefix);
                    $('.introduce').val(appInfo.introduce);
                    $('.companyName').val(appInfo.companyInfo.companyName).addClass('input-disabled');
                    $('.createUser').val(appInfo.createUser).addClass('input-disabled');
                    $('.createTime').val(appInfo.createTimeStr).addClass('input-disabled');
                    $('.lastUpdateUser').val(appInfo.lastUpdateUser).addClass('input-disabled');
                    $('.lastUpdateTime').val(appInfo.lastUpdateTimeStr).addClass('input-disabled');
                    $('.input-disabled').attr('readonly', true).removeAttr('name');
                    if (res.data.source == 1 && appInfo.state == 0) {
                        $('.layui-input').addClass('input-disabled').removeAttr('name');
                        $('.update-operation').remove();
                    } else {
                        if (appInfo.state == 1) {
                            $('.saveOrRefuse').attr('data-type', '-1').text('拒绝');
                            $('.addOrEdit').attr('data-type', '').text('保存');
                        }
                        if (appInfo.state == 0) {
                            $('.saveOrRefuse').attr('data-type', '-1').text('拒绝');
                            $('.addOrEdit').attr('data-type', '1').text('审核通过');
                        }
                    }
                    $('.state').val(appInfo.state);
                }
            });
        } else {
            $('.header-title b').html('添加应用');
        }
    },

    updateForm: function () {
        this.layForm.on('submit(addOrEdit)', function (data) {
            var type = $(data.elem).attr('data-type');
            if (type != '') {
                data.field.state = type;
            } else {
                data.field.state = $('.state').val();  //保存时传应用当前状态
            }
            $.post(getRootPath() + '/appInfo/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/appInfo/getAppInfoList?state=' + res.data;
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
new appInfo();
function appService() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

appService.prototype = {
    init: function () {
        this.initData();
        this.updateForm();
    },

    initData: function () {
        var that = this;
        var appServiceId = $('#appServiceId').val();
        if (appServiceId > 0) {
            $.get(getRootPath() + '/appService/getAppServiceData?appServiceId=' + appServiceId).then(function (res) {
                if (res.code == 0) {
                    that.layForm .val("updateForm", {
                        "serviceType": res.data.serviceType
                    })
                    $('.layui-hide').removeClass('layui-hide');
                    $('.serviceName').val(res.data.serviceName);
                    $('.serviceKey').val(res.data.serviceKey).addClass('input-disabled');
                    $('.urlSuffix').val(res.data.urlSuffix);
                    $('.createUser').val(res.data.createUser).addClass('input-disabled');
                    $('.createTime').val(res.data.createTimeStr).addClass('input-disabled');
                    $('.lastUpdateUser').val(res.data.updateUser).addClass('input-disabled');
                    $('.lastUpdateTime').val(res.data.updateTimeStr).addClass('input-disabled');
                    $('.input-disabled').attr('disabled', true).removeAttr('name');
                    that.getContentType(res.data.method, res.data.contentType);
                }
            });
        } else {
            $('.header-title b').html('添加接口');
            that.getContentType('', '');
        }
    },

    updateForm: function () {
        var appId = $('#appId').val();
        this.layForm.on('submit(create)', function (data) {
            $.post(getRootPath() + '/appService/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/appService/list?appId=' + appId;
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    },

    getContentType: function (method, contentType) {
        var option = '<option value="" >请选择...</option>';
        $.get(getRootPath() + '/appService/getContentType', function (res) {
            if (res.code == 0) {
                // for (var ele of res.data.method) {
                //     if (ele != '') {
                //         if (method == ele.code) {
                //             option += `<option value='${ele.code}' selected="selected">${ele.code}</option>`;
                //         } else {
                //             option += `<option value='${ele.code}'>${ele.code}</option>`;
                //         }
                //     }
                // }
                $.each( res.data.method, function ( i, ele ) {
                    if (method == ele) {
                        option += "<option value='" + ele + "' selected=\"selected\">" + ele + "</option>";
                    } else {
                        option += "<option value='" + ele + "'>" + ele + "</option>";
                    }
                });
                $(".method").append(option);

                option = '<option value="" >请选择...</option>';
                // for (var ele of res.data.contentType) {
                //     if (ele != '') {
                //         if (contentType == ele.code) {
                //             option += `<option value='${ele.code}' selected="selected">${ele.code}</option>`;
                //         } else {
                //             option += `<option value='${ele.code}'>${ele.code}</option>`;
                //         }
                //     }
                // }
                $.each( res.data.contentType, function ( i, ele ) {
                    if (contentType == ele) {
                        option += "<option value='" + ele + "' selected=\"selected\">" + ele + "</option>";
                    } else {
                        option += "<option value='" + ele + "'>" + ele + "</option>";
                    }
                });
                $(".contentType").append(option);
                layui.form.render('select');
            }
        });
    }

};
new appService();
function perimission() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

perimission.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
        this.addOrEdit();
        this.reset();
    },

    initData: function () {
        var permId = $('#permId').val();
        if (permId > 0) {
            $.get(getRootPath() + '/permission/getPermissionData?permId=' + permId).then(function (res) {
                if (res.code == 0) {
                    $('.layui-hide').removeClass('layui-hide');
                    if (res.data.parentId > 0) {
                        $('.parentId').val(res.data.parentPerm).addClass('input-disabled');
                    } else {
                        $('.parentId').parents('.layui-form-item').addClass('layui-hide');
                    }
                    $('.name').val(res.data.name);
                    $('.code').val(res.data.code);
                    $('.url').val(res.data.url);
                    $('.state').val(res.data.state == 0 ? '禁用' : '可用').addClass('input-disabled');
                    $('.createTime').val(res.data.createTimeStr).addClass('input-disabled');
                    $('.updateTime').val(res.data.updateTimeStr).addClass('input-disabled');
                    $('.input-disabled').attr('disabled', true).removeAttr('name');
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
            $.post(getRootPath() + '/permission/addOrEditPermission', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/permission/getPermList'
                }else {
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
new perimission();
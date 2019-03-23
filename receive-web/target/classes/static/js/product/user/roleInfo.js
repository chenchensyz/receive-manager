function roleInfo() {
    var that = this;
    layui.use(['form'], function () {
        that.layForm = layui.form;
        that.init();
        that.layForm.render();
    });
}

roleInfo.prototype = {
    init: function () {
        this.initData();
        this.addOrEdit();
        this.checkAll();
        this.checkChild();
        this.checkGroup();
    },

    initData: function () {
        var roleId = $('#roleId').val();
        var that = this;
        if (roleId > 0) {
            $.get(getRootPath() + '/role/getRoleInfoData?roleId=' + roleId).then(function (res) {
                if (res.code == 0) {
                    var role = res.data.role;
                    $('.roleCode').val(role.roleCode);
                    $('.roleName').val(role.roleName);
                    $('.introduction').val(role.introduction);
                    var existPerm = role.permissions;
                    that.checkedPerm(existPerm, res.data.permissions)
                }
            });
        } else {
            $('.header-title b').html('添加角色');
            that.getPermissions();
        }
    },

    addOrEdit: function () {
        this.layForm.on('submit(addOrEdit)', function (data) {
            var perms = $('.perms[type="checkbox"]:checked');
            var permession = '';
            $.each(perms, function () {
                permession += $(this).val() + ',';
            });
            $('.permissions').val(permession);
            data.field.permissions = $('.permissions').val();
            $.post(getRootPath() + '/role/addOrEditRole', data.field).then(function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/role/getRoleList';
                }
            });
            return false;
        });
    },

    getPermissions: function () {
        $.get(getRootPath() + "/permission/queryPermListData", function (res) {
            if (res.code == 0) {
                //显示权限数据
                $("#permDiv").empty();
                $.each(res.data, function (index, item) {
                    var parentId = item.parentId;
                    // var permInput = `<input class="perms" type='checkbox' name='permId'
                    //                  data-id="${item.id}"
                    //                  data-parentId="${parentId}" value="${item.code }"
                    //                  title="${item.name}" lay-skin='primary'/>`;
                    var permInput = "<input class=\"perms\" type='checkbox' name='permId'" +
                        "data-id=\"" + item.id + "\"  data-parentId=\"" + parentId + "\" value=\"" + item.code + "\"" +
                        "title=\"" + item.name + "\" lay-skin='primary'/>";
                    if (parentId == 0) {
                        // var childDiv = `<div class="childDiv" data-id="${item.id}"
                        //                  style="margin-left: 109px;margin-top: -31px;"></div>`;
                        // $("#permDiv").append(`<div class="perm${item.id}"></div>`);
                        var childDiv = "<div class=\"childDiv\" data-id=\"" + item.id + "\" " +
                                    "style=\"margin-left: 109px;margin-top: -31px;\"></div>";
                        $("#permDiv").append("<div class=\"perm" + item.id + "\"></div>");
                        $('.perm' + item.id).append(permInput).append(childDiv);
                        $('.perms[data-id=' + item.id + ']').attr('lay-filter', 'check-parent');
                    } else {
                        $('.childDiv[data-id=' + parentId + ']').append(permInput);
                        $('.perms[data-id=' + item.id + ']').attr('lay-filter', 'check-child');
                    }
                });
                //重新渲染下form表单 否则复选框无效
                layui.form.render('checkbox');
            } else {
                //弹出错误提示
                layer.alert("获取权限数据有误，请您稍后再试", function () {
                    layer.closeAll();
                });
            }
        });
    },

    checkAll: function () {
        layui.form.on('checkbox(check-all)', function (data) {
            if (data.elem.checked == true) {
                $(".perms").prop("checked", true);
                layui.form.render('checkbox');
            } else {
                $(".perms").prop("checked", false);
                layui.form.render('checkbox');
            }
        });
    },

    //点击父级菜单，选中子级菜单
    checkChild: function () {
        layui.form.on('checkbox(check-parent)', function (data) {
            var id = $(this).attr('data-id');
            if (data.elem.checked == true) {
                $('.perms[data-parentId=' + id + ']').prop("checked", true);
                layui.form.render('checkbox');
            } else {
                $('.perms[data-parentId=' + id + ']').prop("checked", false);
                $('.check-all').prop("checked", false);
                layui.form.render('checkbox');
            }
        });
    },

    //点击级菜单，选中子父级菜单
    checkGroup: function () {
        layui.form.on('checkbox(check-child)', function (data) {
            var parentId = $(this).attr('data-parentId');
            var count = $('.childDiv[data-id=' + parentId + '] input[type="checkbox"]:checked').length;
            if (data.elem.checked == true) {
                $('.perms[data-id=' + parentId + ']').prop("checked", true);
                layui.form.render('checkbox');
            } else {
                if (count == 0) {
                    $('.perms[data-id=' + parentId + ']').prop("checked", false);
                }
                $('.check-all').prop("checked", false);
                layui.form.render('checkbox');
            }
        });
    },

    //回显用户已有权限
    checkedPerm: function (existPerm, permissions) {
        //显示权限数据
        $("#permDiv").empty();
        $.each(permissions, function (index, item) {
            var parentId = item.parentId;
            // var permInput = `<input class="perms" type='checkbox' name='permId'
            //                          data-id="${item.id}"
            //                          data-parentId="${parentId}" value="${item.code }"
            //                          title="${item.name}" lay-skin='primary'/>`;
            var permInput = "<input class=\"perms\" type='checkbox' name='permId'" +
                "data-id=\"" + item.id + "\" " +
                "data-parentId=\"" + parentId + "\" value=\"" + item.code + "\"" +
                "title=\"" + item.name + "\" lay-skin='primary'/>";
            if (parentId == 0) {
                // var childDiv = `<div class="childDiv" data-id="${item.id}"
                //                          style="margin-left: 109px;margin-top: -31px;"></div>`;
                // $("#permDiv").append(`<div class="perm${item.id}"></div>`);
                var childDiv ="<div class=\"childDiv\" data-id=\"" + item.id + "\"" +
                        "style=\"margin-left: 109px;margin-top: -31px;\"></div>";
                $("#permDiv").append("<div class=\"perm" + item.id + "\"></div>");
                $('.perm' + item.id).append(permInput).append(childDiv);
                $('.perms[data-id=' + item.id + ']').attr('lay-filter', 'check-parent');
            } else {
                $('.childDiv[data-id=' + parentId + ']').append(permInput);
                $('.perms[data-id=' + item.id + ']').attr('lay-filter', 'check-child');
            }
            if (existPerm != '' && existPerm.indexOf(item.code) >= 0) {
                $('.perms[data-id=' + item.id + ']').prop("checked", true);
            }
        });
        var count = $('.perms[type="checkbox"]:checked').length;
        if (permissions.length == count) {
            $('.check-all').prop("checked", true);
        }
        //重新渲染下form表单中的复选框 否则复选框选中效果无效
        // layui.form.render();
        layui.form.render('checkbox');
    }
};
new roleInfo();
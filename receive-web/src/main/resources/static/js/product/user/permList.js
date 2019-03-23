/**
 * 用户管理
 */
function permList() {
    var that = this;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

permList.prototype = {
    init: function () {
        this.initData();
        this.editPerm();
        this.toAdd();
        this.getChildPerm();
        this.addChildPerm();
        this.reset();
        this.delPerm();
    },

    initData: function () {
        $.ajax({
            "type": 'post',
            "url": getRootPath() + '/permission/queryPermListData',
            "dataType": "json",
            "success": function (data) {
                $.each(data.data, function (idx, obj) {
                    var parentId = obj.parentId;
                    if (obj.parentId == 0) {
                        // var tr = `<tr class="treeTr" data-tt-id="${obj.id}" data-tt-parent-id="${obj.parentId}">
                        //         <td> ${obj.name}</td><td> ${obj.code}</td><td>${obj.url}</td>
                        //         <td> <a class="edit-btn layui-btn layui-btn-xs" style="margin-left: 20px;">编辑</a>
                        //         <a class="del-btn layui-btn layui-btn-danger layui-btn-xs">删除</a>
                        //         <a class="addChild-btn layui-btn layui-btn-normal layui-btn-xs" >添加子节点</a>
                        //         </td></tr>`;
                        var tr = "<tr class=\"treeTr\" data-tt-id=\"" + obj.id + "\" data-tt-parent-id=\"" + obj.parentId + "\">" +
                            "<td> " + obj.name + "</td><td> " + obj.code + "</td><td>" + obj.url + "</td>" +
                            "<td> <a class=\"edit-btn layui-btn layui-btn-xs\" style=\"margin-left: 20px;\">编辑</a>" +
                            "<a class=\"del-btn layui-btn layui-btn-danger layui-btn-xs\">删除</a>" +
                            "<a class=\"addChild-btn layui-btn layui-btn-normal layui-btn-xs\" >添加子节点</a></td></tr>";
                        $("#treeTable").append(tr);
                    } else {
                        // var tr = `<tr class="treeTr" data-tt-id="${obj.id}" data-tt-parent-id="${obj.parentId}">
                        //         <td> ${obj.name}</td><td> ${obj.code}</td><td>${obj.url}</td>
                        //         <td><a class="edit-btn layui-btn layui-btn-xs" style="margin-left: 20px;">编辑</a>
                        //         <a class="del-btn layui-btn layui-btn-danger layui-btn-xs">删除</a></td></tr>`;
                        var tr = "<tr class=\"treeTr\" data-tt-id=\"" + obj.id + "\" data-tt-parent-id=\"" + obj.parentId + "\">" +
                            "<td> " + obj.name + "</td><td> " + obj.code + "</td><td>" + obj.url + "</td>" +
                            "<td><a class=\"edit-btn layui-btn layui-btn-xs\" style=\"margin-left: 20px;\">编辑</a>" +
                            "<a class=\"del-btn layui-btn layui-btn-danger layui-btn-xs\">删除</a></td></tr>";
                        $('.treeTr[data-tt-id=' + parentId + ']').after(tr);
                    }
                });
                $('table .treeTr[data-tt-parent-id="0"]').attr('background-color', '#f9f9f9');
                $("#treeTable").treetable({
                    expandable: true,
                    initialState: "expanded",
                    //expandable : true
                    clickableNodeNames: true,//点击节点名称也打开子节点.
                    indent: 30//每个分支缩进的像素数。
                });
            }
        });
    },

    editPerm: function () {
        $('#treeTable').on('click', '.edit-btn ', function () {
            var permId = $(this).parent().parent().attr("data-tt-id");
            var parentId = $(this).parent().parent().attr("data-tt-parent-id");
            location.href = getRootPath() + "/permission/getPermission?permId=" + permId + "&parentId=" + parentId;
        });
    },

    toAdd: function (e) {
        $('.add-btn').on('click', function () {
            location.href = getRootPath() + "/permission/getPermission?permId=0&parentId=0";
        });
    },

    //打开子节点弹窗
    getChildPerm: function (e) {
        $('#treeTable').on('click', '.addChild-btn', function () {
            var permId = $(this).parent().parent().attr("data-tt-id");
            $.ajax({
                "type": 'get',
                "url": getRootPath() + '/permission/getAddCilidPerm?permId=' + permId,
                "dataType": "json",
                "success": function (res) {
                    if (res.code == 0) {
                        $('.parentId').val(res.data.id);
                        $('.parentName').val(res.data.name).addClass('input-disabled').attr('disabled', true);
                        layer.open({
                            type: 1,
                            title: "添加子节点",
                            fixed: false,
                            resize: false,
                            shadeClose: true,
                            area: ['600px'],
                            content: $('#permChild'),
                            end: function () {
                                $('#permChild').css("display", "none");
                                $('.layui-hide').removeClass('layui-hide');
                                $('input').not('.layui-hide').val("");
                            }
                        });
                    } else {

                    }
                }
            });
        });
    },

    //重置
    reset: function (e) {
        $('.reset-btn').off('click').on('click', function () {
            $('input').not('.parentId').val("");
        });
    },

    //删除
    delPerm: function (e) {
        $('#treeTable').on('click', '.del-btn ', function () {
            var permId = $(this).parent().parent().attr("data-tt-id");
            var parentId = $(this).parent().parent().attr("data-tt-parent-id");
            layer.confirm('您确定要删除吗？', {
                btn: ['确认', '返回'] //按钮
            }, function(){
                $.post(getRootPath() + '/permission/delPermission',{'permId':permId,'parentId':parentId}).then(function (res) {
                    if (res.code == 0) {
                        location.reload();
                    }else {
                        layer.alert(res.message, function () {
                            layer.closeAll();
                        });
                    }
                });
            });
        });
    },

    addChildPerm: function () {
        this.layForm.on('submit(permSubmit)', function (data) {
            data.field.id = 0;
            $.post(getRootPath() + '/permission/addOrEditPermission', data.field).then(function (res) {
                if (res.code == 0) {
                    location.reload();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    }
};
new permList();
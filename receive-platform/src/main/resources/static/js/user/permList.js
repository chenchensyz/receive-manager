/**
 * 用户管理
 */
function permList() {
    var that = this;
    layui.use(['table', 'form', 'treetable'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layTreetable = layui.treetable;
        that.init();
    });
}

permList.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
        this.addChildPerm();
        this.reset();
    },

    initData: function () {
        var that = this;
        // 渲染表格
        layer.load(2);
        that.layTreetable.render({
            treeColIndex: 1,
            treeSpid: 0,
            treeIdName: 'id',
            treePidName: 'parentId',
            elem: '#munu-table',
            url: getRootPath() + '/permission/queryPermListData',
            page: false,
            cols: [[
                {type: 'numbers'},
                {field: 'name', minWidth: 200, title: '权限名称'},
                {field: 'code', title: '权限标识'},
                {field: 'url', title: '菜单url'},
                {templet: '#auth-state', width: 174, title: '操作'}
            ]],
            done: function () {
                layer.closeAll('loading');
            }
        });

        $('#btn-expand').click(function () {
            that.layTreetable.expandAll('#munu-table');
        });

        $('#btn-fold').click(function () {
            that.layTreetable.foldAll('#munu-table');
        });

        //监听工具条
        that.layTable.on('tool(munu-table)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;

            if (layEvent === 'del') {
                that.delPerm(data.id, data.parentId);
            } else if (layEvent === 'edit') {
                location.href = getRootPath() + "/permission/getPermission?permId=" + data.id + "&parentId=" + data.parentId;
            }  else if (layEvent === 'child') {
                that.getChildPerm(data.id);
            }
        });
    },

    toAdd: function (e) {
        $('.add-btn').on('click', function () {
            location.href = getRootPath() + "/permission/getPermission?permId=0&parentId=0";
        });
    },

    //打开子节点弹窗
    getChildPerm: function (id) {
            $.ajax({
                "type": 'get',
                "url": getRootPath() + '/permission/getAddCilidPerm?permId=' + id,
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
                            maxmin: true, //开启最大化最小化按钮
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
    },

    //重置
    reset: function (e) {
        $('.reset-btn').off('click').on('click', function () {
            $('input').not('.parentId').val("");
        });
    },

    //删除
    delPerm: function (id, parentId) {
        layer.confirm('您确定要删除吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(getRootPath() + '/permission/delPermission', {
                'permId': id,
                'parentId': parentId
            }).then(function (res) {
                if (res.code == 0) {
                    location.reload();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
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
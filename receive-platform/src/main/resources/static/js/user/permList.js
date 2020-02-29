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
        this.addPermission();
        this.reset();
    },

    initData: function () {
        var that = this;
        // 渲染表格
        layer.load(2);
        that.permTable= that.layTreetable.render({
            treeColIndex: 1,
            treeSpid: 0,
            treeIdName: 'id',
            treePidName: 'parentId',
            elem: '#munu-table',
            url: getRootPath() + '/permission/queryPermListData',
            page: false,
            cols: [[
                {type: 'numbers'},
                {field: 'name', title: '权限名称'},
                {field: 'code', title: '权限标识'},
                {field: 'url', title: '菜单url'},
                {field: 'right', templet: '#auth-state', title: '操作', width: 202.8}
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
                // location.href = getRootPath() + "/permission/getPermission?permId=" + data.id + "&parentId=" + data.parentId;
                $('.id').val(data.id);
                $('.parentId').val(data.parentId);
                $('.name').val(data.name);
                $('.code').val(data.code);
                $('.url').val(data.url);
                $('.parent-div').hide();
                that.openPermDialog('编辑权限');
            } else if (layEvent === 'child') {
                $('.parentId').val(data.id);
                $('.parentName').val(data.name).addClass('input-disabled').attr('disabled', true);
                $('.parent-div').show();
                that.openPermDialog('添加子节点');
            }
        });
    },


    toAdd: function (e) {
        var that = this;
        $('.add-btn').on('click', function () {
            // location.href = getRootPath() + "/permission/getPermission?permId=0&parentId=0";
            $('.parent-div').hide();
            $('.parentId').val(0);
            that.openPermDialog('新增权限');
        });
    },

    openPermDialog: function (title) {
        layer.open({
            type: 1,
            title: title,
            fixed: false,
            resize: false,
            shadeClose: true,
            area: ['600px'],
            maxmin: true, //开启最大化最小化按钮
            content: $('#permChild'),
            end: function () {
                $('#permChild').css("display", "none");
                $('input').not('.layui-hide').val("");
            }
        });
    },

    //重置
    reset: function (e) {
        $('.reset-btn').off('click').on('click', function () {
            $('input').not('.parentId').val("");
            $('input').not('.parentName').val("");
        });
    },

    //删除
    delPerm: function (id, parentId) {
        var that = this;
        layer.confirm('您确定要删除吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(getRootPath() + '/permission/delPermission', {
                'permId': id,
                'parentId': parentId
            }).then(function (res) {
                if (res.code == 0) {
                    that.initData();
                    layer.closeAll();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
        });
    },

    addPermission: function () {
        var that = this;
        this.layForm.on('submit(permSubmit)', function (data) {
            $.post(getRootPath() + '/permission/addOrEditPermission', data.field).then(function (res) {
                if (res.code == 0) {
                    that.initData();
                    layer.closeAll();
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
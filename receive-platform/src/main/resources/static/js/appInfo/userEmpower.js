/**
 * 应用管理
 */
function userEmpower() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'tree', 'form', 'util'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layTree = layui.tree;
        that.layUtil = layui.util;
        that.init();
    });
}

userEmpower.prototype = {
    init: function () {
        this.userTree();
        this.getResourceList();
        this.saveAppService();
    },

    userTree: function () {
        var that = this;
        // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
        var setting = {
            view: {
                selectedMulti: true, //设置是否能够同时选中多个节点
                showIcon: true, //设置是否显示节点图标
                showLine: true, //设置是否显示节点与节点之间的连线
                showTitle: true, //设置是否显示节点的title提示信息
            },
            callback: {
                onClick: that.getCheckedService
            },
            data: {
                key: {
                    name: "title"
                }
            }
        };
        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
        $.ajax({
            url: getRootPath() + '/userEmpower/userTree',
            type: 'get',
            success: function (res) {
                that.appEmpower = $.fn.zTree.init($("#userSelect"), setting, res.data);
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
        $("#search_btn").click(that.filterAppInfo);
    },



    getCheckedService: function (event, treeId, treeNode) {
        var that = this;
        if (treeNode.basicData == "u") {
            $('.userName').val(treeNode.id);
            $.get(getRootPath() + "/userEmpower/getUserChecked", {'userName': treeNode.id}, function (res) {
                if (res.code == 0) {
                    var treeObj = $.fn.zTree.getZTreeObj("interfaceSelect");
                    treeObj.checkAllNodes(false);
                    treeObj.expandAll(false);  //收缩节点
                    if (res.data) {
                        for (var i = 0; i < res.data.length; i++) {
                            var node = treeObj.getNodeByParam("parentId", res.data[i]);
                            treeObj.cancelSelectedNode();//先取消所有的选中状态
                            treeObj.checkNode(node, true, true); //将指定ID的checkbox勾选
                            treeObj.selectNode(node, true);//将指定ID的节点选中
                            treeObj.expandNode(node, true, false);//将指定ID节点展开
                        }
                    }
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
        }

    },

    getSecond(setting) {
        var that = this;
        $.ajax({
            url: getRootPath() + '/appInfo/appServiceTree/2',
            type: 'GET',
            success: function (res) {
                that.zTreeObj = $.fn.zTree.init($("#interfaceSelect"), setting, res.data);
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    getThrid(setting) {
        var that = this;
        $.ajax({
            url: getRootPath() + '/appInfo/appServiceTree/3',
            type: 'GET',
            success: function (res) {
                that.zTreeObj = $.fn.zTree.init($("#interfaceApiSelect"), setting, res.data);
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    getResourceList() {
        var that = this;
        var setting = {
            view: {
                selectedMulti: true, //设置是否能够同时选中多个节点
                showIcon: true, //设置是否显示节点图标
                showLine: true, //设置是否显示节点与节点之间的连线
                showTitle: true, //设置是否显示节点的title提示信息
            },
            check: {
                enable: true, //设置是否显示checkbox复选框
                chkStyle: "checkbox",
                chkboxType: {"Y": "ps", "N": "ps"},
                nocheckInherit: true
            },
            data: {
                key: {
                    name: "title"
                }
            }
        };
        that.getSecond(setting);
        that.getThrid(setting);
    },

    saveAppService: function () {
        var that = this;
        $(".add-btn").click(function () {
            var userName = $('.userName').val();
            var treeObj = $.fn.zTree.getZTreeObj("interfaceSelect"); //ztree的id
            var params = treeObj.getCheckedNodes();
            if (!userName) {
                layer.alert('请选择用户后重试', function () {
                    layer.closeAll();
                });
                return false;
            }
            var message = '您确定要执行此操作吗？';
            if (params.length == 0) {
                message = '未选择接口，点击确认清空接口权限'
            }
            layer.confirm(message, {
                btn: ['确认', '取消'] //按钮
            }, function () {
                var data = {'userName': userName, "params": params};
                $.ajax({
                    url: getRootPath() + "/userEmpower/saveUserService",
                    type: 'post',
                    "data": JSON.stringify(data),
                    contentType: 'application/json',
                    success: function (res) {
                        if (res.code == 0) {
                            layer.msg(res.message);
                        } else {
                            layer.alert(res.message, function () {
                                layer.closeAll();
                            });
                        }
                    },
                    error: function (err) {
                        layer.alert(JSON.stringify(err), function () {
                            layer.closeAll();
                        });
                    }
                });
            }, function () {
                layer.closeAll();
            });
            return false;
        })
    }
};
new userEmpower();
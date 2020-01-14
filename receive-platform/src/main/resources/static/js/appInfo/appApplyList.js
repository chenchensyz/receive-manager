/**
 * 应用管理
 */
function appApplyList() {
    var that = this;
    var pageCurr;
    var interfaceTree;
    layui.use(['table', 'tree', 'form', 'util', 'dtree'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layTree = layui.tree;
        that.layUtil = layui.util;
        that.layDtree = layui.dtree;
        that.init();
    });
}

appApplyList.prototype = {
    init: function () {
        this.initData();
        // this.getAppServiceList();
        this.saveAppService();
        this.ztree();
    },

    initData: function () {
        var that = this;
        var DTree = that.layDtree.render({
            elem: "#appSelect",
            url: getRootPath() + '/appInfo/queryAppList', // 该JSON格式被配置过了
            dataStyle: "layuiStyle",  //使用layui风格的数据格式
            response: {statusName: "code", statusCode: 0, rootName: "data", treeId: "id"} // 这里指定了返回的数据格式，组件会根据这些值来替换返回JSON中的指定格式，从而读取信息
            , initLevel: 1
            , done: function (data, obj) {
                $("#search_btn").unbind("click");
                $("#search_btn").click(function () {
                    var value = $("#searchInput").val();
                    if (value) {
                        var flag = DTree.searchNode(value); // 内置方法查找节点
                        if (!flag) {
                            layer.msg("该名称节点不存在！", {icon: 5});
                        }
                    } else {
                        DTree.menubarMethod().refreshTree(); // 内置方法刷新树
                    }
                });
            }
        });
        // 点击节点名称获取选中节点值
        that.layDtree.on("node('appSelect')", function (obj) {
            $('.appId').val(obj.param.nodeId);
            $('.appKey').val(obj.param.parentId);
            that.getCheckedService(obj.param.nodeId);
        });
    },

    getCheckedService: function (appId) {
        var that = this;
        that.interfaceTree.menubarMethod().unCheckAll();  //清空节点
        $.get(getRootPath() + "/appEmpower/getCheckedService", {'appId': appId}, function (res) {
            if (res.code == 0) {
                that.interfaceTree.menubarMethod().closeAllNode(); //收缩节点
                if (res.data) {
                    that.layDtree.chooseDataInit("interfaceSelect", res.data);
                    that.interfaceTree.initNoAllCheck();  //半选
                    // that.interfaceTree.setDisabledNodes(res.data);  //disable
                }
            } else {
                layer.alert(res.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    getAppServiceList: function () {
        var that = this;
        that.interfaceTree = that.layDtree.render({
            elem: "#interfaceSelect",
            url: getRootPath() + '/appInfo/appServiceTree',
            initLevel: 1,  // 指定初始展开节点级别
            type: "all",
            checkbarData: "change",
            checkbar: true,
            checkbarType: "no-all",
            dataStyle: "layuiStyle",  //使用layui风格的数据格式
            response: {statusName: "code", statusCode: 0, rootName: "data", treeId: "id"} // 这里指定了返回的数据格式，组件会根据这些值来替换返回JSON中的指定格式，从而读取信息
        });
        // 点击节点名称获取选中节点值
        that.layDtree.on("node('interfaceSelect')", function (obj) {
            // layer.msg(JSON.stringify(obj.param));
        });
    },

    saveAppService: function () {
        var that = this;
        $(".add-btn").click(function () {
            var appId = $('.appId').val();
            var appKey = $('.appKey').val();
            // var params = that.layDtree.getCheckbarNodesParam("interfaceSelect");
            var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
            var nodes = treeObj.getSelectedNodes();
            alert(nodes);
            return false;

            if (!appId) {
                layer.alert('请选择应用后重试', function () {
                    layer.closeAll();
                });
                return false;
            }
            if (params.length == 0) {
                layer.alert('请选择接口后重试', function () {
                    layer.closeAll();
                });
                return false;
            }

            layer.confirm('是否确定提交接口申请？', {
                btn: ['确认', '返回'] //按钮
            }, function () {
                var data = {'appId': appId, 'appKey': appKey, "params": params};
                $.ajax({
                    url: getRootPath() + "/appValid/apply",
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
                        layer.alert(err.message, function () {
                            layer.closeAll();
                        });
                    }
                });
            });
        })
    },

    ztree: function () {
        var that = this;
        var zTreeObj;
        // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
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
        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
        var zNodes = [
            {
                name: "test1", open: true, children: [
                    {name: "test1_1"}, {name: "test1_2"}]
            },
            {
                name: "test2", open: true, children: [
                    {name: "test2_1"}, {name: "test2_2"}]
            }
        ];
        $.ajax({
            url: getRootPath() + '/appInfo/appServiceTree',
            type: 'get',
            success: function (res) {
               that.zTreeObj= $.fn.zTree.init($("#treeDemo"), setting, res.data);
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    }
};
new appApplyList();
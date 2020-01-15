/**
 * 应用管理
 */
function appEmpower() {
    var that = this;
    var pageCurr;
    var interfaceTree;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

appEmpower.prototype = {
    init: function () {
        this.initData();
        this.getAppServiceList();
        this.saveAppService();
    },

    initData: function () {
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
            url: getRootPath() + '/appInfo/queryAppList',
            type: 'get',
            success: function (res) {
                that.appEmpower = $.fn.zTree.init($("#appEmpower"), setting, res.data);
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
        $("#search_btn").click(that.filterAppInfo);
    },

    filterAppInfo: function () {
        var that = this;
        var treeObj = $.fn.zTree.getZTreeObj("appEmpower");
        var allNode = treeObj.transformToArray(treeObj.getNodes());
        treeObj.hideNodes(allNode);

        function filterFunc(node) {
            var keywords = $("#searchInput").val();
            if (node.title.indexOf(keywords) != -1) return true;
            return false;
        };
        var keywords = $("#searchInput").val();
        if (keywords == "") {
            treeObj.showNodes(allNode);
            // zTreeObj.expandAll(false);
        } else {
            that.nodeList = treeObj.getNodesByFilter(filterFunc);
            for (var n in that.nodeList) {
                if (that.nodeList.hasOwnProperty(n)) {
                    treeObj.expandNode(that.nodeList[n], true, false, false);
                    var pNode = that.nodeList[n].getParentNode();
                    if (pNode != null) {
                        that.nodeList.push(pNode);
                    }
                }
            }
            treeObj.showNodes(that.nodeList);
        }
    },

    getCheckedService: function (event, treeId, treeNode) {
        var that = this;
        $('.appId').val(treeNode.id);
        $('.appKey').val(treeNode.parentId);
        $.get(getRootPath() + "/appEmpower/getCheckedService", {'appId': treeNode.id}, function (res) {
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
    },

    getAppServiceList: function () {
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
        $.ajax({
            url: getRootPath() + '/appInfo/appServiceTree',
            type: 'get',
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

    saveAppService: function () {
        var that = this;
        $(".add-btn").click(function () {
            var appId = $('.appId').val();
            var appKey = $('.appKey').val();
            // var params = that.layDtree.getCheckbarNodesParam("interfaceSelect");
            var treeObj = $.fn.zTree.getZTreeObj("interfaceSelect"); //ztree的id
            var params = treeObj.getCheckedNodes();

            if (!appId) {
                layer.alert('请选择应用后重试', function () {
                    layer.closeAll();
                });
                return false;
            }
            var message = '您确定要执行此操作吗？';
            if (params.length == 0) {
                message = '未选择接口，点击确认清空接口权限'
            }
            layer.confirm(message, {
                btn: ['确认', '返回'] //按钮
            }, function () {
                var data = {'appId': appId, 'appKey': appKey, "params": params};
                $.ajax({
                    url: getRootPath() + "/appEmpower/saveAppService",
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
    }
};
new appEmpower();
/**
 * 应用管理
 */
function appEmpower() {
    var that = this;
    var pageCurr;
    var interfaceTree;
    layui.use(['table', 'form', 'dtree'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layDtree = layui.dtree;
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
        that.layDtree.render({
            elem: "#appEmpower",
            url: getRootPath() + '/appInfo/queryAppList', // 该JSON格式被配置过了
            dataStyle: "layuiStyle",  //使用layui风格的数据格式
            // dataFormat: "list",  //配置data的风格为list
            response: {statusName: "code", statusCode: 0, rootName: "data", treeId: "id"} // 这里指定了返回的数据格式，组件会根据这些值来替换返回JSON中的指定格式，从而读取信息
        });
        // 点击节点名称获取选中节点值
        that.layDtree.on("node('appEmpower')", function (obj) {
            $('.appId').val(obj.param.nodeId);
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
            type:"all",
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
            var params = that.layDtree.getCheckbarNodesParam("interfaceSelect");
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
            var data = {'appId': appId, "params": params};
            $.ajax({
                url: getRootPath() + "/appEmpower/saveAppService",
                type: 'post',
                data: JSON.stringify(data),
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
                    layer.alert(err, function () {
                        layer.closeAll();
                    });
                }
            });
        })
    }
};
new appEmpower();
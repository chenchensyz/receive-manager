/**
 * 应用管理
 */
function userEmpower() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'tree', 'form', 'util', 'dtree'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layTree = layui.tree;
        that.layUtil = layui.util;
        that.layDtree = layui.dtree;
        that.init();
    });
}

userEmpower.prototype = {
    init: function () {
        this.userTree();
        this.getAppServiceList();
        this.saveAppService();
    },

    userTree: function () {
        var that = this;
        that.layDtree.render({
            elem: "#userSelect",
            url: getRootPath() + '/userEmpower/userTree', // 该JSON格式被配置过了
            initLevel: 1,  // 指定初始展开节点级别
            cache: false, // 当取消节点缓存时，则每次加载子节点都会往服务器发送请求
            dataStyle: "layuiStyle",  //使用layui风格的数据格式
            // dataFormat: "list",  //配置data的风格为list
            response: {statusName: "code", statusCode: 0, rootName: "data"} // 这里指定了返回的数据格式，组件会根据这些值来替换返回JSON中的指定格式，从而读取信息
        });
        // 点击节点名称获取选中节点值
        that.layDtree.on("node('userSelect')", function (obj) {
            var basicData = obj.param.basicData.replace("\"", "").replace("\"", "");
            if (basicData == "u") {
                $('.userName').val(obj.param.nodeId);
                that.getUserChecked(obj.param.nodeId);
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

    getUserChecked: function (userName) {
        var that = this;
        that.interfaceTree.menubarMethod().unCheckAll();  //清空节点
        $.get(getRootPath() + "/userEmpower/getUserChecked", {'userName': userName}, function (res) {
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

    saveAppService: function () {
        var that = this;
        $(".add-btn").click(function () {
            var userName = $('.userName').val();
            var params = that.layDtree.getCheckbarNodesParam("interfaceSelect");
            if (!userName) {
                layer.alert('请选择用户后重试', function () {
                    layer.closeAll();
                });
                return false;
            }
            if (params.length==0) {
                layer.alert('请选择接口后重试', function () {
                    layer.closeAll();
                });
                return false;
            }
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
                    layer.alert(err.message, function () {
                        layer.closeAll();
                    });
                }
            });
        })
    }
};
new userEmpower();
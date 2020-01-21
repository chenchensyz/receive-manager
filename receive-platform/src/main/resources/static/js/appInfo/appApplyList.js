/**
 * 应用管理
 */
function appApplyList() {
    var that = this;
    var pageCurr;
    var interfaceTree;
    layui.use(['table', 'tree', 'form', 'util', 'element', 'flow'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layTree = layui.tree;
        that.layUtil = layui.util;
        that.element = layui.element;
        that.flow = layui.flow;
        that.init();
    });
}

appApplyList.prototype = {
    init: function () {
        this.initTab();
        this.initData();
        this.developerValid();
        this.getAppServiceList();
        this.saveAppService();
        this.loginSubmit();
    },


    initTab: function () {
        var that = this;
        that.element.on('tab(pushAreaFilter)', function (data) {
            $('.pushArea').val(data.index);
        });
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
                that.appSelect = $.fn.zTree.init($("#appSelect"), setting, res.data);
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
        var treeObj = $.fn.zTree.getZTreeObj("appSelect");
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
        var pushArea = $('.pushArea').val();
        $.get(getRootPath() + "/appEmpower/getCheckedService", {
            'appId': treeNode.id,
            'pushArea': pushArea
        }, function (res) {
            if (res.code == 0) {
                var treeObj;
                if (pushArea == 0) {
                    treeObj = $.fn.zTree.getZTreeObj("interfaceSelect");
                } else {
                    treeObj = $.fn.zTree.getZTreeObj("interfaceApiSelect");
                }
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
            type: 'POST',
            data: {'area': 0},
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
            var pushArea = $('.pushArea').val();

            // var params = that.layDtree.getCheckbarNodesParam("interfaceSelect");
            var treeObj; //ztree的id
            if (pushArea == 0) {
                treeObj = $.fn.zTree.getZTreeObj("interfaceSelect");
            } else {
                treeObj = $.fn.zTree.getZTreeObj("interfaceApiSelect");
            }
            var params = treeObj.getCheckedNodes();

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
                var data = {'appId': appId, 'appKey': appKey, 'pushArea': pushArea, "params": params};
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

    //开发者绑定关系记录
    developerValid: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            id: 'developerValidTable',
            elem: '#developerValidList'
            , size: 'sm' //小尺寸的表格
            , url: getRootPath() + '/developer/valid/list'
            , page: true,
            request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }, response: {
                statusName: 'code' //数据状态的字段名称，默认：code
                , statusCode: 0 //成功的状态码，默认：0
                , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            }
            , cols: [[
                {type: 'numbers'}
                , {field: 'userName', title: '开发者账号 ', align: 'center'}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = ' <a class="layui-btn layui-btn-xs layui-btn-danger"  lay-event="del">删除</a>';
                        return span;
                    }, title: '操作', align: 'center'
                }
            ]]
            , done: function (res, curr, count) {
                that.pageCurr = curr;
            }
        });

        //监听工具条
        that.layTable.on('tool(developerValidTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {//登陆
                that.delValid(data);
            }
        });
    },

    delValid: function (data) {
        var that = this;
        var data = {'id': data.id};
        layer.confirm('您确定要删除吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.get(getRootPath() + '/developer/valid/delete', data, function (res) {
                if (res.code == 0) {
                    layer.msg(res.message);
                    that.developerValid();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
        }, function () {
            layer.closeAll();
        });
    },

    loginSubmit: function () {
        var that = this;
        $('.addLogin').off('click').on('click', function () {
            layer.open({
                type: 1,
                title: "账号绑定",
                fixed: false,
                resize: false,
                shadeClose: true,
                area: ['500px'],
                maxmin: true, //开启最大化最小化按钮
                content: $('#loginDialog'),
                end: function () {
                    $('#loginDialog').css("display", "none");
                    $('.companyKey').val('')
                }
            });
        });
        $('.loginSubmit').off('click').on('click', function () {
            var data = {'companyKey': $('.companyKey').val(), 'userName': $('.userName').val()};
            that.loginValid(data);
        });

        $('.login-developer').off('click').on('click', function () {
            that.getAppApiServiceList();
        });
        $('.developer-change').off('click').on('click', function () {
            $('.interfaceApiDiv').hide();
            $('.developerValidDiv').show();
            that.developerValid();
        });
    },

    loginValid: function (data) {
        var that = this;
        $.ajax({
            url: getRootPath() + '/developer/valid/login',
            type: 'post',
            contentType: 'application/json',
            "data": JSON.stringify(data),
            success: function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    that.getAppApiServiceList();
                } else {
                    layer.alert(res.message);
                }
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    getAppApiServiceList: function () {
        var that = this;
        var zTreeObj;
        // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
        var setting = {
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
            type: 'POST',
            data: {'area': 1},
            success: function (res) {
                if (res.code == 0) {
                    $('.localName').text(res.companyParam);
                    that.zTreeObj = $.fn.zTree.init($("#interfaceApiSelect"), setting, res.data);
                    $('.interfaceApiDiv').show();
                    $('.developerValidDiv').hide();
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
    }
};
new appApplyList();
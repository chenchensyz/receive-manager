/**
 * 应用管理
 */
function arrangeInfoList() {
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

arrangeInfoList.prototype = {
    init: function () {
        this.initData();
        this.getAppServiceList();
        this.toAdd();
        this.submitArrangeInfo();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#arrangeInfoList'
            , url: getRootPath() + '/arrangeInfo/queryArrangeInfoListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , page: true
            , request: {
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
                , {field: 'interfaceName', title: '编排接口名称'}
                , {field: 'state', title: '应用状态', align: 'center', templet: '#stateJob'}
                , {field: 'createTimeStr', title: '创建时间'}
                , {field: 'right', title: '操作', width: 194, align: 'center', toolbar: '#optBar'}
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
                $("[data-field='state']").children().each(function () {
                    if ($(this).text() == '1') {
                        $(this).text("可用")
                    } else if ($(this).text() == '0') {
                        $(this).text("不可用")
                    }
                });
            }
        });

        //监听工具条
        that.layTable.on('tool(arrangeInfoTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {//编辑
                that.toEdit(data.id);
            } else if (obj.event === 'del') {
                that.deleteArrangeInfo(data)
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });

        $(".state-btn").each(function () {
            if ($(this).attr('data-type') == urlState) {
                $(this).attr('style', 'background-color:white');
            }
        });

    },

    toAdd: function (e) {
        var that = this;
        $('.add-btn').on('click', function () {
            $('.arrangeInfoId').val(null);
            $('.param-name').val('');
            that.layTree.setChecked('demoId1', []); //批量勾选
            that.getInterfaceDialog();
        });
    },

    toEdit: function (arrangeInfoId) {
        var that = this;
        $('.arrangeInfoId').val(arrangeInfoId);
        $.ajax({
            "type": 'get',
            "url": getRootPath() + '/arrangeInfo/getArrangeInfoData?arrangeInfoId=' + arrangeInfoId,
            "dataType": "json",
            "success": function (res) {
                if (res.code == 0) {
                    $('.param-name').val(res.data.interfaceName);
                    that.layTree.setChecked('demoId1', res.services); //批量勾选
                    that.getInterfaceDialog();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            }
        });
    },

    getAppServiceList: function () {
        var that = this;
        $.ajax({
            "type": 'get',
            "url": getRootPath() + '/arrangeInfo/getAppServiceList',
            "dataType": "json",
            "success": function (res) {
                if (res.code == 0) {
                    that.layTree.render({
                        elem: '#interfaceSelect'
                        , data: res.data
                        , showCheckbox: true  //是否显示复选框
                        , id: 'demoId1'
                        , isJump: true //是否允许点击节点时弹出新窗口跳转
                        , click: function (obj) {
                            var data = obj.data;  //获取当前点击的节点数据
                            // layer.msg('状态：' + obj.state + '<br>节点数据：' + JSON.stringify(data));
                        }
                    });
                    console.log(res.data);
                } else {

                }
            }
        });
    },

    //打开弹窗
    getInterfaceDialog: function () {
        layer.open({
            type: 1,
            title: "编排接口",
            fixed: false,
            resize: false,
            shadeClose: true,
            area: ['600px', '400px'],
            maxmin: true, //开启最大化最小化按钮
            content: $('#interfaceDialog'),
            end: function () {
                // $('#interfaceDialog').css("display", "none");
            }
        });
    },

    //
    submitArrangeInfo: function () {
        var that = this;
        this.layForm.on('submit(addOrEdit)', function (data) {
            var checkedData = that.layTree.getChecked('demoId1'); //获取选中节点的数据
            console.log(JSON.stringify(checkedData));
            var data = {'id': data.field.id, 'interfaceName': data.field.interfaceName, 'arrangeModel': checkedData};
            $.ajax({
                "type": 'post',
                "url": getRootPath() + '/arrangeInfo/addOrEdit',
                "contentType": 'application/json',
                "data": JSON.stringify(data),
                "dataType": "json",
                "success": function (res) {
                    if (res.code == 0) {
                        that.load(data);
                        layer.closeAll();
                        layer.msg(res.message);
                    } else {
                        layer.alert(res.message, function () {
                            layer.closeAll();
                        });
                    }
                }
            });
            return false;
        });
    },

    deleteArrangeInfo: function (obj) {
        var that = this;
        layer.confirm('您确定要删除吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.get(getRootPath() + "/arrangeInfo/deleteArrangeInfo", {'arrangeInfoId': obj.id}, function (res) {
                if (res.code == 0) {
                    //回调弹框
                    layer.msg(res.message);
                    that.load(obj);//自定义
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                        that.load(obj);//自定义
                    });
                }
            });
        }, function () {
            layer.closeAll();
        });
    },

    load: function (obj) {
        var that = this;
        //重新加载table
        that.tableIns.reload({
            where: obj.field
            , page: {
                curr: that.pageCurr //从当前页码开始
            }
        });
        that.getAppServiceList();
    }
};
new arrangeInfoList();
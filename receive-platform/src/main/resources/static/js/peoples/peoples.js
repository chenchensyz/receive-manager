/**
 * 应用管理
 */
function peoples() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

peoples.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
        this.saveAppInfoForm();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#appInfoList'
            , url: getRootPath() + '/appInfo/queryAppInfoListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , page: true,
            where: {'state': 1},
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
                , {field: 'appName', title: '应用名称'}
                , {field: 'appKey', title: '应用密钥', width: 317}
                , {
                    field: 'state', align: 'center', templet: function (d) {
                        if (d.state == 0) {
                            return '<span>待审核</span>';
                        } else if (d.state == 1) {
                            return '<span>已通过</span>';
                        } else if (d.state == 2) {
                            return '<span>未通过</span>';
                        }
                    }, title: '应用状态', width: 90
                }
                , {field: 'createTimeStr', title: '创建时间', width: 172}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = ' <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>';
                        // if (d.state == 2) {
                        //     span += '<a class="layui-btn layui-btn-warm layui-btn-xs opt-btn" lay-event="view">理由</a>';
                        // }
                        if (d.state == 1) {
                            span += '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="down">下架</a>';
                        } else if (d.state == 2) {
                            span += '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="up">上架</a>';
                        }
                        return span;
                    }, title: '操作', width: 184
                }
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
            }
        });

        //监听工具条
        that.layTable.on('tool(appInfoTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {//编辑
                $('.form-edit').show();
                that.layForm.val("appInfoInfoFrom", {
                    "id": data.id
                    , "appName": data.appName
                    , 'appKey': data.appKey
                    , 'description': data.description
                    , 'createTimeStr': data.createTimeStr
                })
                layer.open({
                    type: 1,
                    title: "编辑应用",
                    fixed: false,
                    resize: false,
                    shadeClose: true,
                    maxmin: true, //开启最大化最小化按钮
                    area: ['600px'], //宽高
                    content: $("#appInfoDialog")
                });
            } else if (obj.event === 'down') {
                that.optAppInfo(obj, 2);
            } else if (obj.event === 'up') {
                that.optAppInfo(obj, 0);
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });
    },

    optAppInfo: function (obj, state) {
        var data = obj.data;
        var that = this;
        layer.confirm('您确定要执行此操作吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.ajax({
                "type": 'post',
                "url": getRootPath() + '/appInfo/saveAppInfo',
                "data": {'id': data.id, 'state': state},
                "dataType": "json",
                "success": function (res) {
                    if (res.code == 0) {
                        if (state == 0) {
                            layer.alert('申请已提交，请等待管理员审核', function () {
                                layer.closeAll();
                            });
                        } else {
                            layer.msg(res.message);
                        }
                        that.load(obj);
                    } else {
                        layer.alert(res.message, function () {
                            layer.closeAll();
                        });
                    }
                }, "error": function (err) {
                    layer.alert(JSON.stringify(err), function () {
                        layer.closeAll();
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
    },

    toAdd: function () {
        var that = this;
        $('.add-btn').off('click').on('click', function () {
            $('#appInfoInfoFrom')[0].reset();
            $('.param-id').val('');
            $('.form-edit').hide();
            layer.open({
                type: 1,
                title: "新增应用",
                fixed: false,
                resize: false,
                shadeClose: true,
                maxmin: true, //开启最大化最小化按钮
                area: ['600px'], //宽高
                content: $("#appInfoDialog")
            });
        });
    },

    saveAppInfoForm: function () {
        var that = this;
        this.layForm.on('submit(addOrEdit)', function (data) {
            var message = data.field.state;
            $.ajax({
                "type": 'post',
                "url": getRootPath() + '/appInfo/saveAppInfo',
                "data": data.field,
                "dataType": "json",
                "success": function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        layer.msg(res.message);
                        that.load(data.field);
                    } else {
                        layer.alert(res.message, function () {
                            layer.closeAll();
                        });
                    }
                }, "error": function (err) {
                    layer.alert(JSON.stringify(err), function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    },
};
new peoples();
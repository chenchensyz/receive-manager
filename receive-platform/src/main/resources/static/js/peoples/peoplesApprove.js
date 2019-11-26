/**
 * 应用管理
 */
function peoplesApprove() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

peoplesApprove.prototype = {
    init: function () {
        this.initData();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#appInfoList'
            , url: getRootPath() + '/appInfo/queryAppInfoListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , page: true,
            where: {'state': 0},
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
                        var span = ' <a class="layui-btn layui-btn-xs" lay-event="approve">审核通过</a>';
                        span += '<a class="layui-btn layui-btn-danger layui-btn-xs opt-btn" lay-event="refuse">拒绝</a>';
                        return span;
                    }, title: '操作'
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
            if (obj.event === 'approve') {//编辑
                that.option(data.id, 1);
            } else if (obj.event === 'refuse') {
                that.option(data.id, 2);
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
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

    option: function (id, state) {
        var that = this;
        layer.confirm('您确定要执行此操作吗？', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            $.ajax({
                "type": 'post',
                "url": getRootPath() + '/appInfo/saveAppInfo',
                "data": {"id": id, "state": state},
                "dataType": "json",
                "success": function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        layer.msg(res.message);
                        that.init();
                    } else {
                        layer.msg(res.message);
                    }
                },"error":function (err) {
                    layer.msg(JSON.stringify(err));
                }
            });
        }, function () {
            layer.closeAll();
        });

    }
};
new peoplesApprove();
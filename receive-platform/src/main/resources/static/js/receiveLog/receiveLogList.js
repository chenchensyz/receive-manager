/**
 * 应用管理
 */
function receiveLogList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.laydate = layui.laydate;
        that.init();
    });
}

receiveLogList.prototype = {
    init: function () {
        this.laydate.render({elem: '#beginTime', type: 'datetime'});
        this.laydate.render({elem: '#endTime', type: 'datetime'});
        this.initData();
        this.toAdd();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#receiveLogList'
            , url: getRootPath() + '/receiveLog/queryReceiveLogListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
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
                , {field: 'serviceName', title: '服务名称'}
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '调用应用'
                }
                , {field: 'requestTimeStr', title: '请求时间'}
                , {field: 'responseTimeStr', title: '响应时间'}
                , {field: 'responseCode', title: '响应状态', width: 165, align: 'center'}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = '<a class="layui-btn layui-btn-xs" lay-event="view">查看详情</a>'
                        return span;
                    }, title: '操作', width: 124
                }
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
                $("[data-field='responseCode']").children().each(function () {
                    if ($(this).text() == '1') {
                        $(this).text('响应成功');
                    } else if ($(this).text() == '0') {
                        $(this).text('响应失败');
                    }
                });
            }
        });

        //监听工具条
        that.layTable.on('tool(receiveLogTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'view') {//编辑
                // layer.alert(data.remark, {
                //     closeBtn: 0
                // });
                $('.param-method').val(data.url);
                $('.param-params').val(data.params);
                $('.param-remark').val(data.remark);
                layer.open({
                    type: 1,
                    title: "详情",
                    fixed: false,
                    resize: false,
                    shadeClose: true,
                    maxmin: true, //开启最大化最小化按钮
                    area: ['600px'], //宽高
                    content: $("#logDialog")
                });
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });
    },


    optCompany: function (obj) {
        var that = this;
        var option = obj.state == 1 ? '禁用' : '启用';
        var state = obj.state == 1 ? 0 : 1;
        layer.confirm('您确定要' + option + ' ' + obj.companyName + ' 吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(getRootPath() + "/companyInfo/delCompany", {"companyId": obj.id, "state": state}, function (data) {
                if (data.code == 0) {
                    //回调弹框
                    layer.msg(option + "成功！");
                    that.load(obj);//自定义
                } else {
                    layer.alert(data, function () {
                        layer.closeAll();
                        //加载load方法
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
    },

    toAdd: function () {
        $('.add-btn').off('click').on('click', function () {
            // $('.add-btn').attr('href', getRootPath() + '/user/getUserInfo?userId=0');
            location.href = getRootPath() + '/appInfo/getAppInfo?appId=0';
        });
    }
};
new receiveLogList();
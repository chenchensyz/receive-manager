/**
 * 我的申请
 */
function personalApply() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form', 'upload'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.upload = layui.upload;
        that.init();
    });
}

personalApply.prototype = {
    init: function () {
        this.initData();
        this.checkAppService();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appValid/approveListData'
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
                , {field: 'appName', title: '应用名称', width: 270}
                , {field: 'applyTimeStr', title: '申请时间', align: 'center',width: 169}
                , {field: 'approveTimeStr', title: '审批时间', align: 'center',width: 169}
                , {field: 'userName', title: '申请人', align: 'center'}
                , {
                    field: 'state', align: 'center', templet: function (d) {
                        if (d.state == 0) {
                            return ' <span>待审核</span>';
                        } else if (d.state == 1) {
                            return ' <span>已通过</span>';
                        } else if (d.state == 2) {
                            return ' <span>未通过</span>';
                        }
                    }, title: '状态'
                }
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = ' <a class="layui-btn layui-btn-xs" lay-event="view">查看</a>';
                        if (d.state == 2) {
                            span += ' <a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="refuse">理由</a>';
                        }
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
        that.layTable.on('tool(appServiceTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'view') {//编辑
                that.alertCheck(obj.data);
            } else if (obj.event === 'refuse') {
                layer.alert(data.remark, {
                    closeBtn: 0
                });
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

    alertCheck: function (data) {
        var that = this;
        that.layTable.render({
            id: 'serviceApproveTable',
            elem: '#serviceApproveList'
            , url: getRootPath() + '/appValid/approveService?recordId=' + data.id
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {state: 0}
            , response: {
                statusName: 'code' //数据状态的字段名称，默认：code
                , statusCode: 0 //成功的状态码，默认：0
                , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            }
            , cols: [[
                {type: 'numbers'}
                , {field: 'serviceName', title: '接口名称'}
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '所属应用'
                }
                // , {field: 'urlSuffix', title: '请求地址'}
                // , {field: 'method', title: '请求方式', align: 'center', width: 89}
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
            }
        });

        $('.dio-id').val(data.id);
        $('.dio-appId').val(data.appId);

        layer.open({
            type: 1,
            title: "审核接口",
            fixed: false,
            resize: false,
            shadeClose: true,
            area: ['600px', '400px'],
            maxmin: true, //开启最大化最小化按钮
            content: $('#serviceDialog'),
            end: function () {
                $('#serviceDialog').css("display", "none");
            }
        });
    },

    checkAppService: function () {
        var that = this;
        $('.dio-operate').off('click').on('click', function () {
            that.operateAppService($('.dio-id').val(), $('.dio-appId').val(), $(this).attr('data-type'));
        });
    },

    operateAppService: function (id, appId, state) {
        var that = this;
        var data = {'id': id, 'appId': appId, 'state': state}
        if (state == 2) {
            layer.prompt({title: '请输入拒绝理由', formType: 2}, function (text, index) {
                layer.close(index);
                data.remark = text;
                that.checkPost(data);
            });
        } else {
            layer.confirm('您确定要执行此操作吗？', {
                btn: ['确认', '取消'] //按钮
            }, function () {
                that.checkPost(data);
            }, function () {
                layer.closeAll();
            });
        }
    },

    checkPost: function (data) {
        $.ajax({
            url: getRootPath() + "/appValid/check",
            type: 'POST',
            data: data,
            success: function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/appValid/approveList';
                    layer.msg(res.message);
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            },
            error: function (XMLHttpRequest) {
                layer.alert(XMLHttpRequest.toString(), function () {
                    layer.closeAll();
                });
            }
        });
    }
};
new personalApply();
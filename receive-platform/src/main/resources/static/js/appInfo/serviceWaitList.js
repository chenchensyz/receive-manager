/**
 * 待审核接口
 */
function appServiceList() {
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

appServiceList.prototype = {
    init: function () {
        this.initData();
        this.checkAppService();
    },

    initData: function () {
        var that = this;
        var source = sessionStorage.getItem("source")
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appService/queryAppServiceListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {state: 0}
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
                , {field: 'serviceName', title: '接口名称', width: 270}
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '所属应用', width: 270
                }
                , {field: 'method', title: '请求方式', align: 'center', width: 89}
                , {field: 'createTimeStr', title: '创建时间'}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = '';
                        if (source == 0) {
                            span += ' <a class="layui-btn layui-btn-xs" lay-event="check">审核</a>' +
                                '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="refuse">拒绝</a>';
                        } else if (source == 1) {
                            span += ' <a class="layui-btn layui-btn-xs" lay-event="view">查看</a>';
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
            if (obj.event === 'check') {//编辑
                that.alertCheck(obj.data);
            } else if (obj.event === 'view') {
                var state = $('.opt-btn').attr('data-type');
                that.delAppService(obj, obj.data.id, state);
            } else if (obj.event === 'refuse') {
                that.refuseAppService(obj.data.id, 2);
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
        $('.param-id').val(data.id);
        $('.param-appName').val(data.appName == null ? '无' : data.appName);
        $('.param-serviceName').val(data.serviceName);
        $('.param-urlSuffix').val(data.urlSuffix);
        $('.param-method').val(data.method);
        $('.param-contentType').val(data.contentType);
        var that = this;
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
            that.refuseAppService($('.param-id').val(), $(this).attr('data-type'));
        });
    },

    refuseAppService: function (id, state) {
        var that = this;
        var data = {'id': id, 'state': state}
        if (state == 2) {
            layer.prompt({title: '请输入拒绝理由', formType: 2}, function (text, index) {
                layer.close(index);
                data.refuseMsg = text;
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
            url: getRootPath() + "/appService/addOrEdit",
            type: 'POST',
            data: data,
            success: function (res) {
                if (res.code == 0) {
                    location.href = getRootPath() + '/appService/waitList';
                    layer.msg(res.message);
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            },
            error: function (XMLHttpRequest) {
                layer.alert(XMLHttpRequest.status, function () {
                    layer.closeAll();
                });
            }
        });
    }
};
new appServiceList();
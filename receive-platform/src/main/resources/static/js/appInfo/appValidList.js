/**
 * 待审核接口申请
 */
function appValidList() {
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

appValidList.prototype = {
    init: function () {
        this.initData();
    },

    initData: function () {
        var that = this;
        var source = localStorage.getItem("source")
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appValid/appValidListData'
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
                , {field: 'approveAppName', title: '授权应用'}
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '调用应用'
                }
                , {field: 'appKey', title: '应用密钥(appKey)'}
                , {field: 'serviceName', title: '调用接口'}
                , {field: 'serviceKey', title: '接口密钥(serviceKey)'}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = ' <a class="layui-btn layui-btn-xs" lay-event="view">查看</a>';
                        return span;
                    }, title: '操作', width: 80
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
                that.alertView(obj.data);
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });

    },

    alertView: function (data) {
        $('.param-id').val(data.id);
        $('.param-urlSuffix').val(data.urlSuffix);
        $('.param-method').val(data.method);
        $('.param-contentType').val(data.contentType);
        var that = this;
        layer.open({
            type: 1,
            title: "详情",
            fixed: false,
            resize: false,
            shadeClose: true,
            area: ['600px', '300px'],
            maxmin: true, //开启最大化最小化按钮
            content: $('#serviceDialog'),
            end: function () {
                $('#serviceDialog').css("display", "none");
            }
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
    }
};
new appValidList();
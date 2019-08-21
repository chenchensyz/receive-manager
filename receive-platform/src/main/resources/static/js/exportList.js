/**
 * 应用管理
 */
function exportList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

exportList.prototype = {
    init: function () {
        this.initData();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#exportList'
            , url: getRootPath() + '/redirect/export'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , page: true,
            request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }, response: {
                statusName: 'code' //数据状态的字段名称，默认：code
                , statusCode: 0 //成功的状态码，默认：0
                // , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            }
            , cols: [[
                  {field: 'key', title: 'id'}
                , {field: 'interReceiveTime', title: '互联网收到', width: 162}
                , {field: 'interForwardTime', title: '互联网发送移动网', width: 162}
                , {field: 'mobileReceiveTime', title: '移动网接收互联网', width: 162}
                , {field: 'mobileRequsetTime', title: '移动网发送内网', width: 162}
                , {field: 'mobileResponseTime', title: '内网返回', width: 162}
                , {field: 'mobileFrwardTime', title: '移动网返回互联网', width: 162}
                , {field: 'interResTime', title: '互联网接收移动网返回', width: 173}
                , {field: 'error', title: '异常', width: 162}
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
            }
        });
    },

    delAppInfo: function (obj) {
        var data = obj.data;
        var that = this;
        var $tr = obj.tr;
        var state = $tr.find('.app-opt').attr('data-type');
        var option = $tr.find('.app-opt').text();
        layer.confirm('您确定要' + option + ' ' + data.appName + ' 吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.get(getRootPath() + "/appInfo/deleteAppInfo", {'appId': data.id, 'state': state}, function (res) {
                if (res.code == 0) {
                    //回调弹框
                    layer.msg(option + "成功！");
                    that.load(obj);//自定义
                } else {
                    layer.alert(res.message, function () {
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
    }
};
new exportList();
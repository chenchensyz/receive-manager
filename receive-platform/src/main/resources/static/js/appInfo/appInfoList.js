/**
 * 应用管理
 */
function appInfoList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

appInfoList.prototype = {
    init: function () {
        this.queryCountAppInfo();
        this.initData();
        this.toAdd();
        this.getListState();
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
                , {field: 'appKey', title: '应用密钥'}
                , {
                    field: 'state', align: 'center', templet: function (d) {
                        if (d.state == 1) {
                            return '<span>可用</span>';
                        } else if (d.state == 2) {
                            return '<span>下架</span>';
                        } else if (d.state == 3) {
                            return '<span>注销</span>';
                        }
                    }, title: '应用状态', width: 90
                }
                , {field: 'createTimeStr', title: '创建时间', width: 162}
                // , {field: 'right', title: '操作', width: 170, align: 'center', toolbar: '#optBar'}
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
                location.href = getRootPath() + "/appInfo/getAppInfo?appId=" + data.id;
            } else if (obj.event === 'interface') {
                location.href = getRootPath() + "/appService/getAppServiceList?appId=" + data.id;
            } else if (obj.event === 'opt') {
                that.delAppInfo(obj);
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
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
                    that.queryCountAppInfo();
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

    queryCountAppInfo: function () {
        $.get(getRootPath() + '/appInfo/queryCountAppInfo?ran=' + new Date().getTime(), function (res) {
            if (res.code == 0) {
                //回调弹框
                $.each(res.data, function (index, item) {
                    $('.state-span').eq(index).text(item);
                });
            } else {
                layer.alert(res.message, function () {
                    layer.closeAll();
                });
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
    },

    toAdd: function () {
        $('.add-btn').off('click').on('click', function () {
            // $('.add-btn').attr('href', getRootPath() + '/user/getUserInfo?userId=0');
            location.href = getRootPath() + '/appInfo/getAppInfo?appId=0';
        });
    },

    getListState: function () {
        var that = this;
        $('.state-btn').off('click').on('click', function () {
            var state = $(this).attr('data-type');
            that.tableIns.reload({
                where: {'state': state, 'pageNum': 1, 'pageSize': 10}
                , page: {
                    curr: that.pageCurr //从当前页码开始
                }
            });
            $('#url-state').val(state);
            $('.state-btn').attr('style', 'background-color:#e6e6e6');
            $(this).attr('style', 'background-color:white');
            return false;
        });
    }
};
new appInfoList();
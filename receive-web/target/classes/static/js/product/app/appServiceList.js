/**
 * 应用管理
 */
function appServiceList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

appServiceList.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
        this.toBack();
    },

    initData: function () {
        var that = this;
        var appId = $('#appId').val();
        that.tableIns = that.layTable.render({
            elem: '#appServiceList'
            , url: getRootPath() + '/appService/queryAppServiceListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {appId: appId}
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
                , {field: 'name', title: '接口名称', width: 265}
                , {field: 'urlSuffix', title: '接口后缀', width: 342}
                , {field: 'method', title: '请求方式', align: 'center', width: 120}
                , {field: 'createTimeStr', title: '创建时间', width: 187}
                , {field: 'right', title: '操作', align: 'center', toolbar: '#optBar'}
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
                $("[data-field='state']").children().each(function () {
                    if ($(this).text() == '1') {
                        $(this).text("可用")
                    } else if ($(this).text() == '0') {
                        $(this).text("禁用")
                    }
                })
            }
        });

        //监听工具条
        that.layTable.on('tool(appServiceTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {//编辑
                var appId = $('#appId').val();
                location.href = getRootPath() + '/appService/getAppService?appId=' + appId + '&appServiceId=' + data.id;
            } else if (obj.event === 'opt') {
                that.delAppService(obj);
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

    toBack: function () {
        $('.back-btn').off('click').on('click', function () {
            location.href = getRootPath() + '/appInfo/getAppInfoList';
        });
    },

    toAdd: function () {
        $('.add-btn').off('click').on('click', function () {
            // $('.add-btn').attr('href', getRootPath() + '/user/getUserInfo?userId=0');
            var appId = $('#appId').val();
            location.href = getRootPath() + '/appService/getAppService?appId=' + appId;
        });
    },

    delAppService: function (obj) {
        var data = obj.data;
        var that = this;
        layer.confirm('您确定要删除 ' + data.name + ' 吗？', {
            btn: ['确认', '返回'] //按钮n
        }, function () {
            $.get(getRootPath() + "/appService/deleteAppService", {
                'appServiceId': data.id,
                'state': 0
            }, function (res) {
                if (res.code == 0) {
                    //回调弹框
                    layer.msg("删除成功！");
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
    }
};
new appServiceList();
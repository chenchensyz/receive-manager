/**
 * 应用管理
 */
function appServiceList() {
    var that = this;
    var pageCurr;
    var tableIns;
    var appTree;
    layui.use(['table', 'form', 'upload', 'dtree'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.upload = layui.upload;
        that.layDtree = layui.dtree;
        that.init();
    });
}

appServiceList.prototype = {
    init: function () {
        this.initData();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appService/searchData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {'state': 1}
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
                {type: 'checkbox'}
                , {field: 'serviceName', title: '接口名称', align: 'center'}
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '所属应用', align: 'center'
                }
                , {
                    field: 'state', templet: function (d) {
                        if (d.state == 1) {
                            return '<span>已通过</span>'
                        }
                    }, title: '接口状态', width: 92, align: 'center'
                }
                , {field: 'createUser', title: '创建者', align: 'center'}
                , {field: 'createTimeStr', title: '创建时间', align: 'center'}
                // , {field: 'right', title: '操作', align: 'center', toolbar: '#optBar'}
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
            if (obj.event === 'edit') {//编辑
                location.href = getRootPath() + '/appService/getAppService?appId=' + obj.data.appId + '&appServiceId=' + data.id;
            } else if (obj.event === 'opt') {
                var state = $('.opt-btn').attr('data-type');
                that.delAppService(obj, obj.data.id, state);
            } else if (obj.event === 'del') {
                that.delAppService(obj, obj.data.id, -1);
            }
        });

        that.layTable.on('checkbox(appServiceTable)', function (obj) {
            var checkStatus = that.layTable.checkStatus('appServiceTable'); //appServiceTable 即为 id 对应的值
            if (checkStatus.data.length > 0) {
                if ($('.state').val() == 2) {
                    $('.refuse-btn').text('批量恢复');
                }
                $('.refuse-btn').show();
            } else {
                $('.refuse-btn').hide();
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
    }

};
new appServiceList();
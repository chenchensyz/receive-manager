/**
 * 开发者管理
 */
function developerList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

developerList.prototype = {
    init: function () {
        this.initData();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#developerList'
            , url: getRootPath() + '/developer/queryDeveloperListData'
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
                , {field: 'userName', title: '用户名', unresize: true}
                , {field: 'name', title: '昵称'}
                , {field: 'status', title: '状态', width: 95, align: 'center', templet: '#stateJob'}
                , {field: 'createTimeStr', title: '创建时间', minWidth: 80}
                // , {fixed: 'right', title: '操作', width: 194, align: 'center', toolbar: '#optBar'}
            ]]
            , done: function (res, curr, count) {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                that.pageCurr = curr;
                $("[data-field='status']").children().each(function () {
                    if ($(this).text() == '1') {
                        $(this).text("可用")
                    } else if ($(this).text() == '0') {
                        $(this).text("禁用")
                    }
                })
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
};
new developerList();
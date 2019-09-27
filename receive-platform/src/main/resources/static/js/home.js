/**
 * 应用管理
 */
function home() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

home.prototype = {
    init: function () {
        this.initSse()
        this.inletSelect();
        this.initData();
        this.queryEcharts();
        this.initErrRemind();
    },

    initSse: function () {
        if (!!window.EventSource) {
            console.log('支持')
        }
    },

    inletSelect: function () {
        var that = this;
        $.get(getRootPath() + '/ranking/inlet').then(function (res) {
            if (res.code == 0) {
                var inletDivList = [];
                var inletNum = res.data.countAppInfo;
                var inletTitle = '应用数量(个)';
                var bg = 'layui-bg-blue';
                inletDivList.push(that.addInlet(inletNum, inletTitle, bg));

                inletNum = res.data.countService;
                inletTitle = '接口数量(个)';
                bg = 'layui-bg-cyan';
                inletDivList.push(that.addInlet(inletNum, inletTitle, bg));

                inletNum = res.data.receiveLogCount;
                inletTitle = '访问量(次)';
                bg = 'layui-bg-orange';
                inletDivList.push(that.addInlet(inletNum, inletTitle, bg));
                $('.inlet-body').html(inletDivList);
            }

        });
    },

    addInlet: function (inletNum, inletTitle, bg) {
        // var inletDiv = `<div class="layui-col-xs4">
        //                     <div class="panel layui-bg-number">
        //                         <div class="panel-body">
        //                             <div class="panel-title">
        //                                 <span class="label pull-right ${bg}">实时</span>
        //                                 <h5>${inletTitle}</h5>
        //                             </div>
        //                             <div class="panel-content">
        //                                 <h1 class="no-margins">${inletNum}</h1>
        //                                 <small>当前分类总记录数</small>
        //                             </div>
        //                         </div>
        //                     </div>
        //                 </div>`

        var inletDiv="<div class=\"layui-col-xs4\">" +
            "<div class=\"panel layui-bg-number\">" +
            "<div class=\"panel-body\">" +
            "<div class=\"panel-title\"><span class=\"label pull-right ".concat(bg, "\">实时</span><h5>").concat(inletTitle, "</h5></div>" +
                "<div class=\"panel-content\"><h1 class=\"no-margins\">").concat(inletNum, "</h1>" +
                "<small>当前分类总记录数</small></div>" +
                "</div></div></div>");
        return inletDiv;
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#appRanking'
            , url: getRootPath() + '/ranking/app/top'
            , method: 'get' //默认：get请求
            , cellMinWidth: 80
            , response: {
                statusName: 'code' //数据状态的字段名称，默认：code
                , statusCode: 0 //成功的状态码，默认：0
                , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            },
            cols: [[
                {type: 'numbers', title: '排名'}
                , {field: 'appName', title: '应用名称'}
                , {field: 'receiveNum', title: '访问量(次)'}
            ]]
        });
    },

    queryEcharts: function () {
        // 基于准备好的dom，初始化echarts图表
        var date = new Date();
        date.setDate(date.getDate() - 6)
        var startTime = timeFormat(date);
        var endTime = timeFormat(new Date());
        var dateList = queryDateBetween(startTime, endTime);

        var sucCount = [];
        var errCount = [];
        $.ajax({
            url: getRootPath() + "/ranking/receiveLog",//提交的url,
            type: 'GET',
            data: {'startTime': startTime, 'endTime': endTime},
            success: function (res) {
                if (res.code == 0) {
                    sucCount = res.data.sucCount;
                    errCount = res.data.errCount;
                    var echartsRecords = echarts.init($('#main')[0]);
                    var option = {
                        tooltip: {
                            trigger: 'axis'
                        },
                        legend: {
                            data: ['请求成功', '请求失败']
                        },
                        toolbox: {
                            show: true,
                            feature: {
                                mark: {show: true},
                                dataView: {show: true, readOnly: false},
                                magicType: {show: true, type: ['line', 'bar']},
                                restore: {show: true},
                                saveAsImage: {show: true}
                            }
                        },
                        calculable: true,
                        xAxis: [
                            {
                                type: 'category',
                                boundaryGap: false,
                                data: dateList
                            }
                        ],
                        yAxis: [
                            {
                                type: 'value',
                                axisLabel: {
                                    formatter: '{value}'
                                }
                            }
                        ],
                        series: [
                            {
                                name: '请求成功',
                                type: 'line',
                                data: sucCount
                            },
                            {
                                name: '请求失败',
                                type: 'line',
                                data: errCount
                            }
                        ]
                    };

                    // 为echarts对象加载数据
                    echartsRecords.setOption(option);

                    // echarts 窗口缩放自适应
                    window.onresize = function () {
                        echartsRecords.resize();
                    }
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

    initErrRemind: function () {
        var that = this;
        that.layTable.render({
            elem: '#errRemind'
            , url: getRootPath() + '/appService/queryAppServiceListData'
            , method: 'get' //默认：get请求
            , cellMinWidth: 80
            , where: {controlState: 0}
            , response: {
                statusName: 'code' //数据状态的字段名称，默认：code
                , statusCode: 0 //成功的状态码，默认：0
                , countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            },
            cols: [[
                {type: 'numbers', title: '序号'}
                , {field: 'serviceName', title: '服务名称'}
            ]]
        });
    }

};
new home();
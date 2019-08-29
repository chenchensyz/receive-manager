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
        this.inletSelect();
        this.initData();
        this.queryEcharts();
    },

    inletSelect: function () {
        var that = this;
        $.get(getRootPath() + '/ranking/inlet').then(function (res) {
            if (res.code == 0) {
                var inletDivList = [];
                var inletNum = res.data.countAppInfo;
                var inletTitle = '应用数量(个)';
                var icon = 'layui-icon-app';
                inletDivList.push(that.addInlet(inletNum, inletTitle, icon));

                inletNum = res.data.countService;
                inletTitle = '接口数量(个)';
                icon = 'layui-icon-form';
                inletDivList.push(that.addInlet(inletNum, inletTitle, icon));

                inletNum = res.data.receiveLogCount;
                inletTitle = '访问量(次)';
                icon = 'layui-icon-template';
                inletDivList.push(that.addInlet(inletNum, inletTitle, icon));
                $('.inlet-body').html(inletDivList);
            }

        });
    },

    addInlet: function (inletNum, inletTitle, icon, url) {
        let inletDiv = `<div class="inlet-div">
                        <div class="inlet-div-icon">
                            <i class="layui-icon ${icon}"></i>
                            <a href="${url}" class="inlet-num">${inletNum}</a>
                        </div>
                        <div class="inlet-div-title">
                            <a href="" class="inlet-title">${inletTitle}</a>
                        </div>
                    </div>`
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
        let dateList = queryDateBetween(startTime, endTime);

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
                    var myChart = echarts.init($('#main')[0]);
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
                    myChart.setOption(option);
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

    toAdd: function () {
        $('.add-btn').off('click').on('click', function () {
            // $('.add-btn').attr('href', getRootPath() + '/user/getUserInfo?userId=0');
            location.href = getRootPath() + '/appInfo/getAppInfo?appId=0';
        });
    }

};
new home();
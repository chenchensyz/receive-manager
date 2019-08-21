/**
 * 应用管理
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
        this.queryAppList();
        this.changeServiceFile();
        this.alertServiceFile();
        this.refuseMoreAppService();
        this.appSec();
        this.toAdd();
    },

    queryAppList: function () {
        var that = this;
        $.ajax({
            url: getRootPath() + "/appInfo/queryAppList",
            type: 'get',
            success: function (res) {
                if (res.code == 0) {
                    for (i = 0; i < res.data.length; i++) {
                        $('.app-list').append(`<button type="button" class="layui-btn layui-btn-primary layui-btn-fluid layui-btn-sm app-sec" data-type="${res.data[i].id}">${res.data[i].appName}</button>`);
                    }
                    if (res.data.length > 0) {
                        var i=$('#appId').val();
                        $('#appId').val(res.data[0].id);
                        $('#appName').val(res.data[0].appName);
                        that.initData(res.data[0].id);
                        $('.app-list').find('.app-sec').each(function (index, element) {
                            if (index == 0) {
                                $(this).attr('style', 'background-color: #c3c3c3');
                            }
                        });
                    }
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    initData: function (appId) {
        var that = this;
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appService/queryAppServiceListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {appId: appId, state: 1}
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
                , {field: 'serviceName', title: '接口名称', width: 270}
                , {field: 'serviceKey', title: '接口密钥', width: 305}
                , {field: 'method', title: '请求方式', align: 'center', width: 89}
                , {field: 'right', title: '操作', align: 'center', toolbar: '#optBar', width: 150}
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
                var appId = $('#appId').val();
                location.href = getRootPath() + '/appService/getAppService?appId=' + appId + '&appServiceId=' + data.id;
            } else if (obj.event === 'opt') {
                var state = $('.opt-btn').attr('data-type');
                console.log(state)
                that.delAppService(obj, obj.data.id, state);
            } else if (obj.event === 'del') {
                that.delAppService(obj, obj.data.id, -1);
            }
        });

        that.layTable.on('checkbox(appServiceTable)', function (obj) {
            var checkStatus = that.layTable.checkStatus('appServiceTable'); //appServiceTable 即为 id 对应的值
            if (checkStatus.data.length > 0) {
                if ($('.state').val() == 0) {
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
    },

    //切换应用
    appSec: function () {
        var that = this;
        $('.app-list').on('click', '.app-sec', function () {
            $(".rest-btn").click();
            $('.app-sec').attr('style', 'background-color: #fefefe');
            var appId = $(this).attr('data-type');
            $(this).attr('style', 'background-color: #c3c3c3');
            $('#appId').val(appId);
            $('#appName').val($(this).text());
            that.initData(appId)
        });
    },

    refuseMoreAppService: function () {
        var that = this;
        $('.refuse-btn').off('click').on('click', function () {
            var checkStatus = that.layTable.checkStatus('appServiceTable'); //appServiceTable 即为 id 对应的值
            var ids = '';
            for (var i = 0; i < checkStatus.data.length; i++) {
                ids += checkStatus.data[i].id + ',';
            }
            var state = $('.state').val() == 0 ? 1 : 0;
            that.delAppService(that, ids, state);
            return false;
        });
    },

    delAppService: function (obj, ids, state) {
        var that = this;
        layer.confirm('您确定要执行此操作吗？', {
            btn: ['确认', '返回'] //按钮n
        }, function () {
            debugger
            $.ajax({
                url: getRootPath() + "/appService/changeAppService",//提交的url,
                type: 'POST',
                data: {'appServiceIds': ids, 'state': state},
                success: function (res) {
                    if (res.code == 0) {
                        layer.msg(res.message);
                        that.load(obj); //加载load方法
                        $('.refuse-btn').hide();
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
        }, function () {
            layer.closeAll();
        });
    },

    changeServiceFile: function () {
        var that = this;
        // ①设定change事件
        $(".addServiceFile").change(function () {
            //    ②如果value不为空，调用文件加载方法
            if ($(this).val() != "") {
                that.addServiceFile(this);
            }
        })
    },
    alertServiceFile: function () {
        var that = this;
        $('.alertServiceFile').off('click').on('click', function () {
            layer.confirm('请下载示例文件填写上传，如已下载请点击上传', {
                btn: ['下载示例文件', '上传', '取消'] //按钮
            }, function () {
                location.href = getRootPath() + '/file/批量上传接口.xlsx';
                layer.closeAll();
            }, function () {
                $(".addServiceFile").click();
            }, function () {
                layer.closeAll();
            });
        });
    },
    addServiceFile: function (ele) {
        var that = this;
        //④创建一个formData对象
        var formData = new FormData;
        //⑥获取files
        var files = $(ele)[0].files[0];
        if (files.name.indexOf(".xlsx") == -1) {
            layer.alert('对不起，文件格式不正确');
            return
        }
        files.name = new Date().getTime() + ".xlsx";
        //⑦将name 和 files 添加到formData中，键值对形式
        formData.append("file", files);
        formData.append("appId", $('#appId').val());
        $.ajax({
            url: getRootPath() + '/appService/uploadServiceFile',//提交的url,
            type: 'POST',
            data: formData,
            processData: false,// ⑧告诉jQuery不要去处理发送的数据
            contentType: false, // ⑨告诉jQuery不要去设置Content-Type请求头
            success: function (res) {
                if (res.code == 0) {
                    layer.msg(res.message);
                    $('.addServiceFile').val('');
                    that.load(ele); //加载load方法
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            },
            error: function (err) {
                layer.alert(err.message, function () {
                    layer.closeAll();
                });
            }
        });
    },

    toAdd: function () {
        var that = this;
        $('.add-btn').off('click').on('click', function () {
            // // $('.add-btn').attr('href', getRootPath() + '/user/getUserInfo?userId=0');
            var appId = $('#appId').val();
            location.href = getRootPath() + '/appService/getAppService?appId=' + appId;
            // $('.param-reset-btn').click();
            // $('.param-appName').val($('#appName').val());
            // that.getContentType(1, 1);
            // layer.open({
            //     type: 1,
            //     title: "编排接口",
            //     fixed: false,
            //     resize: false,
            //     shadeClose: true,
            //     area: ['600px', '400px'],
            //     maxmin: true, //开启最大化最小化按钮
            //     content: $('#serviceDialog'),
            //     end: function () {
            //         // $('#interfaceDialog').css("display", "none");
            //     }
            // });
        });
    }
};
new appServiceList();
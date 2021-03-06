/**
 * 我的服务
 */
function appServiceList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form', 'upload'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.layUpload = layui.upload;
        that.init();
    });
}

appServiceList.prototype = {
    init: function () {
        this.initData();
        this.refuseMoreAppService();
        this.getContentType();
        this.updateForm();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            id: 'appServiceTable',
            elem: '#appServiceList'
            , url: getRootPath() + '/appService/queryAppServiceListData'
            , method: 'post' //默认：get请求
            , cellMinWidth: 80
            , where: {'state': $('.state').val()}
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
                , {
                    field: 'appName', templet: function (d) {
                        if (d.appName) {
                            return '<span>' + d.appName + '</span>'
                        } else {
                            return '<span>无</span>';
                        }
                    }, title: '所属应用'
                }
                , {
                    field: 'state', templet: function (d) {
                        if (d.state == 1) {
                            return '<span>已通过</span>'
                        } else if (d.state == 0) {
                            return '<span>待审核</span>'
                        } else if (d.state == 2) {
                            return '<span>未通过</span>'
                        }
                    }, title: '接口状态', width: 92, align: 'center'
                }
                , {
                    field: 'filePath', templet: function (d) {
                        var span = '<a class="layui-btn layui-btn-primary layui-btn-xs"  lay-event="up"> 上传</a>'
                        if (d.filePath) {
                            span += '<a class="layui-btn layui-btn-normal layui-btn-xs"  lay-event="down">下载</a>';
                        }
                        return span;
                    }, title: '附件', align: 'center'
                }
                , {field: 'createTimeStr', title: '创建时间', width: 183, align: 'center'}
                , {
                    field: 'right', align: 'center', templet: function (d) {
                        var span = ' <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>';
                        if (d.state == 1) {
                            span += '<a class="layui-btn layui-btn-warm layui-btn-xs opt-btn" lay-event="off">下架</a>';
                        }
                        if (d.state == 2) {
                            span += '<a class="layui-btn layui-btn-warm layui-btn-xs opt-btn" lay-event="view">理由</a>';
                        }
                        if (d.state != 1) {
                            span += '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>';
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
            $('.close-btn').off('click').on('click', function () {
                layer.closeAll();
            });
            var data = obj.data;
            if (obj.event === 'edit') {//编辑
                $('.param-method').val(data.method);
                $('.param-appKey').val(!data.appKey ? data.serviceKey : data.appKey);
                $('.param-serviceKey').val(data.serviceKey);
                $('.param-contentType').val(data.contentType);
                $('.param-appName').val(!data.appName ? '无' : data.appName);
                $('.param-introduce').val(data.introduce);
                $('.param-id').val(data.id);
                if (data.state == 2) {
                    $('.submit-apply').show();
                } else {
                    $('.submit-apply').hide();
                }
                that.layForm.val("serviceInfoFrom", {
                    "serviceName": data.serviceName
                    , 'urlSuffix': data.urlSuffix
                    , 'serviceRule': data.serviceRule
                    , 'sourceType': data.sourceType
                    , 'isOpen': data.isOpen
                    , 'pushArea': data.pushArea
                })
                layer.open({
                    type: 1
                    ,title: "接口编辑"
                    ,area: ['100%', '100%']
                    ,content: $("#serviceDialog")
                    ,success: function(layero, index){
                        layer.tips('点击此处关闭', '.layui-layer-setwin', {tips: 4});
                    }
                });
            } else if (obj.event === 'view') {
                layer.alert(data.refuseMsg, {
                    closeBtn: 0
                });
            } else if (obj.event === 'off') {
                that.delAppService(obj, obj.data.id, 2);
            } else if (obj.event === 'del') {
                that.delAppService(obj, obj.data.id, -1);
            } else if (obj.event === 'up') {
                that.upFile.reload({
                    data: {'serviceId': obj.data.id}
                    , done: function (res) {
                        that.load(obj)
                    }
                });
                $("#upFile").trigger("click");
            } else if (obj.event === 'down') {
                window.open(getRootPath() + '/file/service' + data.filePath);
            }
        });

        that.layTable.on('checkbox(appServiceTable)', function (obj) {
            var source = localStorage.getItem("source")
            if (source == 0) {
                var checkStatus = that.layTable.checkStatus('appServiceTable'); //appServiceTable 即为 id 对应的值
                if (checkStatus.data.length > 0) {
                    if ($('.state').val() == 0) {
                        $('.refuse-btn').text('批量恢复');
                    }
                    $('.refuse-btn').show();
                    $('.refuse-div').show();
                } else {
                    $('.refuse-div').hide();
                }
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });

        that.upFile = that.layUpload.render({ //允许上传的文件后缀
            elem: '#upFile'
            , url: getRootPath() + '/appService/uploadFile'
            , accept: 'file' //普通文件
            , done: function (res) {
                layer.msg(res.message);
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

    refuseMoreAppService: function () {
        var that = this;
        $('.refuse-btn').off('click').on('click', function () {
            var checkStatus = that.layTable.checkStatus('appServiceTable'); //appServiceTable 即为 id 对应的值
            var ids = '';
            for (var i = 0; i < checkStatus.data.length; i++) {
                ids += checkStatus.data[i].id + ',';
            }
            var state = $('.state').val() == 2 ? 1 : 2;
            that.delAppService(that, ids, state);
            return false;
        });
    },

    delAppService: function (obj, ids, state) {
        var that = this;
        layer.confirm('您确定要执行此操作吗？', {
            btn: ['确认', '返回'] //按钮n
        }, function () {
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

    updateForm: function () {
        var that = this;
        this.layForm.on('submit(addOrEdit)', function (data) {
            var state = $(this).attr('data-type');
            if (state == 0) {
                data.field.state = 0;  //提交待审核
            }
            $.post(getRootPath() + '/appService/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    layer.msg(res.message);
                    that.initData();
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            });
            return false;
        });
    },

    getContentType: function () {
        var option = '<option value="" >请选择...</option>';
        $.get(getRootPath() + '/appService/serviceCodeData', function (res) {
            if (res.code == 0) {
                $.each(res.method, function (i, ele) {
                    option += "<option value='" + ele.code + "'>" + ele.name + "</option>";
                });
                $(".param-method").append(option);

                option = '<option value="" >请选择...</option>';
                $.each(res.content_type, function (i, ele) {
                    option += "<option value='" + ele.code + "'>" + ele.name + "</option>";
                });
                $(".param-contentType").append(option);

                option = '<option value="" >请选择...</option>'; //编码规则
                $.each(res.encoded, function (i, ele) {
                    option += "<option value='" + ele.code + "'>" + ele.name + "</option>";
                });
                $(".serviceRule").append(option);

                option = '<option value="" >请选择...</option>'; //资源类型
                $.each(res.service_type, function (i, ele) {
                    option += "<option value='" + ele.code + "'>" + ele.name + "</option>";
                });
                $(".sourceType").append(option);
                layui.form.render('select');
            }
        });
    }
};
new appServiceList();
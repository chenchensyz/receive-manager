function serviceRegister() {
    var that = this;
    var upFile;
    layui.use(['form', 'step', 'upload'], function () {
        that.layForm = layui.form;
        that.layStep = layui.step;
        that.layUpload = layui.upload;
        that.init();
    });
}

serviceRegister.prototype = {
    init: function () {
        this.initData();
        this.getAppList();
        this.getContentType('', '');
        this.alertServiceFile();
    },

    initData: function () {
        var that = this;
        that.layStep.render({
            elem: '#stepForm',
            filter: 'stepForm',
            width: '100%', //设置容器宽度
            stepWidth: '750px',
            height: '660px',
            stepItems: [{
                title: '基本信息'
            }, {
                title: '接口信息'
            }, {
                title: '完成'
            }]
        });

        that.layForm.on('submit(formStep)', function (data) {
            that.layStep.next('#stepForm');
            return false;
        });

        that.layForm.on('submit(formStep2)', function (data) {
            data.field.appId = $('.appId').val();
            data.field.serviceName = $('.serviceName').val();
            data.field.introduce = $('.introduce').val();
            data.field.filePath = $('.serviceFileText').text();
            $.post(getRootPath() + '/appService/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    that.layStep.next('#stepForm');
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
                return res.code;
            });
            return false;
        });

        $('.pre').click(function () {
            that.layStep.pre('#stepForm');
        });

        $('.next').click(function () {
            that.layStep.next('#stepForm');
            $('.layui-form')[0].reset();
            $('.layui-form')[1].reset();
            $('.serviceFileText').text('');
        });

        that.serviceFile = that.layUpload.render({ //允许上传的文件后缀
            elem: '#serviceFile'
            , url: getRootPath() + '/appService/uploadFile'
            , accept: 'file' //普通文件
            , done: function (res) {
                if (res.code == 0) {
                    $('.serviceFileText').text(res.data);
                    that.serviceFile.reload({
                        data: {'pathSuffix': res.data}
                    });
                } else {
                    layer.alert(res.message, function () {
                        layer.closeAll();
                    });
                }
            }
            , error: function () {
                //演示失败状态，并实现重传
                var demoText = $('#demoText');
                demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
            }
        });
    },

    getContentType: function (method, contentType) {
        var option = '<option value="" >请选择...</option>';
        $.get(getRootPath() + '/appService/getContentType', function (res) {
            if (res.code == 0) {
                $.each(res.data.method, function (i, ele) {
                    if (method == ele) {
                        option += "<option value='" + ele + "' selected=\"selected\">" + ele + "</option>";
                    } else {
                        option += "<option value='" + ele + "'>" + ele + "</option>";
                    }
                });
                $(".method").append(option);

                option = '<option value="" >请选择...</option>';
                $.each(res.data.contentType, function (i, ele) {
                    if (contentType == ele) {
                        option += "<option value='" + ele + "' selected=\"selected\">" + ele + "</option>";
                    } else {
                        option += "<option value='" + ele + "'>" + ele + "</option>";
                    }
                });
                $(".contentType").append(option);
                layui.form.render('select');
            }
        });
    },


    getAppList: function () {
        var that = this;
        var option = '<option value="" >应用选择（选填）</option>';
        $.get(getRootPath() + '/appInfo/queryAppList', function (res) {
            if (res.code == 0) {
                $.each(res.data, function (i, ele) {
                    option += "<option value='" + ele.id + "'>" + ele.title + "</option>";
                });
                $(".appId").append(option);
                $(".more-appId").append(option);
                layui.form.render('select');
            }
        });

        that.layForm.on('select(appId)', function (data) {
            $('.param-appId').val(data.value);
            $('.param-appName').text(data.elem[data.elem.selectedIndex].text);
        });
    },

    alertServiceFile: function () {
        var that = this;
        $('.alertServiceFile').off('click').on('click', function () {
            layer.open({
                type: 1,
                title: "批量增加",
                fixed: false,
                resize: false,
                shadeClose: true,
                maxmin: true, //开启最大化最小化按钮
                area: ['600px', '350px'], //宽高
                content: $("#register-more")
            });
        });

        that.layForm.on('select(more-appId)', function (data) {
            $('.more-appId').val(data.value);
            that.upFile.reload({
                data: {'appId': $('.more-appId').val() == null ? '' : $('.more-appId').val()}

            });
        });

        that.upFile = that.layUpload.render({ //允许上传的文件后缀
            elem: '#upFile'
            , url: getRootPath() + '/appService/uploadMoreService'
            , accept: 'file' //普通文件
            , exts: 'xlsx' //只允许上传excel
            , done: function (res) {
                layer.msg(res.message);
            }
        });

        $('.uploadMore').off('click').on('click', function () {
            location.href = getRootPath() + '/file/批量上传接口.xlsx';
            return false;
        });
    }
};
new serviceRegister();
function serviceRegister() {
    var that = this;
    var upFile;
    layui.extend({
        step: '{/}' + getRootPath() + '/js/lay-module/step-lay/step'   // {/}的意思即代表采用自有路径，即不跟随 base 路径
    }).use(['form', 'step', 'upload'], function () {
        that.layForm = layui.form;
        that.layStep = layui.step;
        that.layUpload = layui.upload;
        that.init();
        that.layForm.render();
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
            height: '500px',
            stepItems: [{
                title: '填写服务注册信息'
            }, {
                title: '确认服务注册信息'
            }, {
                title: '完成'
            }]
        });

        that.layForm.on('submit(formStep)', function (data) {
            $('.param-appName').text($('.param-appName').text());
            $('.param-serviceName').text(data.field.serviceName);
            $('.param-urlSuffix').text(data.field.urlSuffix);
            $('.param-method').text(data.field.method);
            $('.param-contentType').text(data.field.contentType);
            that.layStep.next('#stepForm');
            return false;
        });

        that.layForm.on('submit(formStep2)', function (data) {
            data.field.appId = $('.param-appId').val();
            data.field.serviceName = $('.param-serviceName').text();
            data.field.urlSuffix = $('.param-urlSuffix').text();
            data.field.method = $('.param-method').text();
            data.field.contentType = $('.param-contentType').text();
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
            , url: getRootPath() + '/appService/uploadServiceFile'
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
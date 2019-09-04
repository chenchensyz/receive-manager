function serviceRegister() {
    var that = this;
    layui.extend({
        step: '{/}' + getRootPath() + '/js/lay-module/step-lay/step'   // {/}的意思即代表采用自有路径，即不跟随 base 路径
    }).use(['form', 'step'], function () {
        that.layForm = layui.form;
        that.layStep = layui.step;
        that.init();
        that.layForm.render();
    });
}

serviceRegister.prototype = {
    init: function () {
        this.initData();
        this.getContentType('', '');
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
            $('.param-serviceName').text(data.field.serviceName);
            $('.param-urlSuffix').text(data.field.urlSuffix);
            $('.param-method').text(data.field.method);
            $('.param-contentType').text(data.field.contentType);
            that.layStep.next('#stepForm');
            return false;
        });

        that.layForm.on('submit(formStep2)', function (data) {
            data.field.serviceName = $('.param-serviceName').text();
            data.field.urlSuffix = $('.param-urlSuffix').text();
            data.field.method = $('.param-method').text();
            data.field.contentType = $('.param-contentType').text();
            $.post(getRootPath() + '/appService/addOrEdit', data.field).then(function (res) {
                if (res.code == 0) {
                    that.layStep.next('#stepForm');
                }else {
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
    }

};
new serviceRegister();
/**
 * 用户管理
 */
function userList() {
    var that = this;
    var pageCurr;
    var tableIns;
    layui.use(['table', 'form'], function () {
        that.layTable = layui.table;
        that.layForm = layui.form;
        that.init();
    });
}

userList.prototype = {
    init: function () {
        this.initData();
        this.toAdd();
    },

    initData: function () {
        var that = this;
        that.tableIns = that.layTable.render({
            elem: '#userList'
            , url: getRootPath() + '/user/queryUserListData'
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
                , {field: 'userId', title: '用户名', unresize: true}
                , {field: 'nickName', title: '昵称'}
                , {field: 'telephone', title: '联系电话', minWidth: 80}
                , {field: 'createTimeStr', title: '创建时间', minWidth: 80}
                , {field: 'state', title: '状态', width: 95, align: 'center', templet: '#stateJob'}
                , {fixed: 'right', title: '操作', width: 194, align: 'center', toolbar: '#optBar'}
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
        that.layTable.on('tool(userTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {//编辑
                location.href = getRootPath() + "/user/getUserInfo?userId=" + data.id;
            } else if (obj.event === 'release') {
                that.resetPass(data);
            } else if (obj.event === 'opt') {
                that.delUser(data, data.userId, data.nickName);
            }
        });

        that.layForm.on('submit(searchSubmit)', function (data) {
            //重新加载table
            that.load(data);
            return false;
        });
    },

    resetPass: function (obj) {
        var that = this;
        layer.confirm('您确定要重置 '+ obj.nickName+' 的密码为888888吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(getRootPath() + "/user/reSetPassword", {"userId": obj.id}, function (res) {
                if (res.code == 0) {
                    //回调弹框
                    layer.msg( "重置成功！");
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
    },

    delUser: function (obj, userId, nickName) {
        var that = this;
        var option = obj.state == 1 ? '禁用' : '启用';
        var state = obj.state == 1 ? 0 : 1;
        layer.confirm('您确定要' + option + ' ' + nickName + ' 吗？', {
            btn: ['确认', '返回'] //按钮
        }, function () {
            $.post(getRootPath() + "/user/deleteUser", {"userId": userId, "state": state}, function (data) {
                if (data.code == 0) {
                    //回调弹框
                    layer.msg(option + "成功！");
                    that.load(obj);//自定义
                } else {
                    layer.alert(data, function () {
                        layer.closeAll();
                        //加载load方法
                        that.load(obj);//自定义
                    });
                }
            });
        }, function () {
            layer.closeAll();
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

    openUser: function (id, title) {
        if (id == null || id == "") {
            $("#id").val("");
        }
        layer.open({
            type: 1,
            title: title,
            fixed: false,
            resize: false,
            shadeClose: true,
            area: ['400px'],
            content: $('#releasePassword'),
            end: function () {
                $('#releasePassword').css("display", "none");
            }
        });
    },

    toAdd: function () {
        $('.add-btn').off('click').on('click', function () {
            // $('.add-btn').attr('href',getRootPath()+'/user/getUserInfo?userId=0');
            location.href = getRootPath() + '/user/getUserInfo?userId=0';
        });
    }
};
new userList();
/**
 * 应用管理
 */
var $;

function index() {
    var that = this;
    layui.use(['element', 'layer', 'layuimini'], function () {
        $ = layui.jquery;
        var element = layui.element,
            layer = layui.layer;
        layuimini.init();
        that.init();
    });
}

index.prototype = {
    init: function () {
        this.initSse()
    },

    initSse: function () {
        var socket;
        var online;
        if (typeof (WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        } else {
            console.log("您的浏览器支持WebSocket");
            //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
            //等同于socket = new WebSocket("ws://localhost:8083/checkcentersys/websocket/20");
            var userId = localStorage.getItem('userId');
            var url = getRootPath() + "/websocket/" + userId;
            socket = new WebSocket(url.replace("http", "ws"));
            //打开事件
            socket.onopen = function () {
                console.log("Socket 已打开");
                //socket.send("这是来自客户端的消息" + location.href + new Date());
            };
            //获得消息事件
            socket.onmessage = function (msg) {
                var content = '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">' +
                    '检测下列服务异常，请检查服务运行！<br>';
                var data = JSON.parse(msg.data);
                $.each(data, function (index, item) {
                    content += '<p>' + item + '</p>'
                });
                content += '</div>'
                layer.open({
                    type: 1
                    , title: '<span style="color: red">异常提醒</span>'
                    , offset: 'rb' //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
                    , id: 'layerDemo' + 'rb' //防止重复弹出
                    , content: content
                    , btnAlign: 'c' //按钮居中
                    , shade: 0 //不显示遮罩
                    , yes: function () {
                        layer.closeAll();
                    }
                });
            };
            //关闭事件
            socket.onclose = function () {
                console.log("Socket已关闭");
            };
            //发生了错误事件
            socket.onerror = function () {
                console.log("Socket发生了错误");
            }
        }

        $('.login-out').on("click", function () {
            layer.confirm('您确定要退出吗？', {
                btn: ['确认', '返回'] //按钮
            }, function () {
                localStorage.clear();
                location.href = getRootPath() + "/login/quit";
            }, function () {
                layer.closeAll();
            });
        });
    }
};
new index();
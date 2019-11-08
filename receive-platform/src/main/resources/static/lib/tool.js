$(function () {
    $.ajaxSetup({
        error: function(jqXHR, textStatus, errorMsg){  // 出错时默认的处理函数
            // jqXHR 是经过jQuery封装的XMLHttpRequest对象
            // textStatus 可能为： null、"timeout"、"error"、"abort"或"parsererror"
            // errorMsg 可能为： "Not Found"、"Internal Server Error"等
            console.log(jqXHR)
            switch (jqXHR.status){
                case(500):
                    alert("服务器系统内部错误");
                    break;
                case(401):
                    alert("未登录");
                    break;
                case(403):
                    alert("当前用户没有权限");
                    break;
                case(408):
                    alert("请求超时");
                    break;
                default:
                    alert("未知错误");
            }
        }
    });
});
function getRootPath() {
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath = window.document.location.href;
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht = curWwwPath.substring(0, pos);
    //获取带"/"的项目名，如：/uimcardprj
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}

function queryDateBetween(start, end) {
    var date_all = [], i = 0;
    var startTime = getDate(start);
    var endTime = getDate(end);
    while ((endTime.getTime() - startTime.getTime()) >= 0) {
        var year = startTime.getFullYear();
        var month = (startTime.getMonth() + 1).toString().length == 1 ? "0" + (startTime.getMonth() + 1).toString() : (startTime.getMonth() + 1).toString();
        var day = startTime.getDate().toString().length == 1 ? "0" + startTime.getDate() : startTime.getDate();
        date_all[i] = year + "-" + month + "-" + day;
        startTime.setDate(startTime.getDate() + 1);
        i += 1;
    }
    console.log(date_all)
    return date_all;
}


function timeFormat(date) {
    if (!date || typeof (date) === "string") {
        this.error("参数异常，请检查...");
    }
    var y = date.getFullYear(); //年
    var m = date.getMonth() + 1; //月
    if (m < 10) {
        m = '0' + m;
    }
    var d = date.getDate(); //日
    return y + "-" + m + "-" + d;
}


function getDate(datestr) {
    var temp = datestr.split("-");
    var date = new Date(temp[0], temp[1] - 1, temp[2]);
    return date;
}



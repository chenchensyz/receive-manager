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

$(function () {
    $.ajaxSetup({
        contentType: "application/x-www-form-urlencoded;charset=utf-8",
        complete: function (XMLHttpRequest, textStatus) {
            //通过XMLHttpRequest取得响应头，sessionstatus，
            // console.log(XMLHttpRequest.status)
            // if (XMLHttpRequest.status == 401) {
            //     //如果超时就处理 ，指定要跳转的页面(比如登陆页)
            //     window.location.replace(getRootPath()+"/login/toLogin");
            // }
        },
        statusCode: {
            200: function () {
                console.log("成功了")
            },
            401: function () {
                window.location.href = getRootPath() + "/login/toLogin";
            }
        }
    });
});

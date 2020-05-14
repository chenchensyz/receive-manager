/*
 * ===================AJAX异步分页=================
 *
 * Copyright 2012 8 23, zhutx
 *
 * 假设id为pageWidget的div是你放置分页控件的容器，则按如下形式调用：
 * $("#pageWidget").asynPage("/user/findUser_asyn.action","#tbody",buildHtml,totalRowSize,{"pageRowSize":10});
 * 参数说明：
 * ------------Required-----------
 * 参数一：请求URL
 * 参数二：渲染结果集的页面容器
 * 参数三：负责渲染结果集到页面的函数
 * 参数四：总记录数
 * ------------Optional-----------
 * 参数五(json对象)：
 * 属性pageRowSize:每页记录数(不配置，则默认为20)
 * 属性param:请求参数(json格式)
 */
(function ($) {
    var settings;
    var page;
    var paramStr;

    $.fn.asynPage = function (url, contentContainer, buildHtml_fun, totalRowSize, callerSettings) {

        settings = $.extend({
            currPageNum: 1,
            pageRowSize: 20,
            param: null
        }, callerSettings || {});

        settings.contentContainer = $(contentContainer);
        settings.url = url;
        settings.pageWidget = this;
        settings.totalRowSize = totalRowSize;
        settings.buildHtml_fun = buildHtml_fun;

        page = new AsynPage(settings.currPageNum, settings.totalRowSize, settings.pageRowSize);

        paramStr = makeParamStr(settings.param);

        //开始获取数据
        fetchData(page.getCurrPageNum());

        return this;
    };

    /* 将json转换为请求参数字符串 */
    var trunParamConfig2RequestParamStr = function (json) {
        var str = "";
        for (key in json) {
            if (str == "") {
                str += key + "=" + json[key];
            } else {
                str += "&" + key + "=" + json[key];
            }
        }
        return str;
    };

    /* 将配置参数拼接为请求字符串 */
    var makeParamStr = function (param_json) {
        if (param_json == null) {
            return "";
        } else {
            return trunParamConfig2RequestParamStr(param_json);
        }
    };

    /*
     * 负责获取后台数据，获取完毕后会触发构建分页控件
     */
    var fetchData = function (currPageNum) {

        page.setCurrPageNum(currPageNum);
        var firstResult = page.getFirstResult();
        var maxResult = page.getMaxResult();
        var pageRowSize = page.getPageRowSize();

        var data = null;
        if (paramStr) {
            data = paramStr + "&page.currPageNum=" + currPageNum + "&page.pageRowSize=" + pageRowSize + "&page.firstResult=" + firstResult + "&page.maxResult=" + maxResult;
        } else {
            data = "page.currPageNum=" + currPageNum + "&page.pageRowSize=" + pageRowSize + "&page.firstResult=" + firstResult + "&page.maxResult=" + maxResult;
        }

        $.ajax({
            type: "POST",
            url: settings.url,
            data: data,
            success: function (res) {
                settings.contentContainer.empty();
                settings.buildHtml_fun(datas);
                buildPageWidget(page);//触发构建分页控件
            },
            error: function (xmlHttpRequest, textStatus, errorThrown) {
                if (textStatus == "error") {
                    var error = eval('(' + xmlHttpRequest.responseText + ')');
                    alert("Sorry：" + error.errorCode + "，" + error.message + "！");
                }
            }
        });
    };

    var trunTargetPage = function (pageNum) {
        fetchData(pageNum);
    }

    /* 为分页控件绑定事件 */
    var bindEvent = function () {
        var links = settings.pageWidget.find("a");
        $.each(links, function (i, link) {
            var link = $(link);
            var pageNum = link.attr("pageNum");
            link.click(function () {
                trunTargetPage(pageNum);
            });
        });
    }

    /* 构建分页控件的具体算法实现 */
    var buildPageWidget = function (page) {

        //构建分页控件前，先清理现有控件
        settings.pageWidget.empty();

        /* --------------- 下面开始进入真正的分页控件构建过程 -------------- */

        /* --------------- 1.开始：构建描述信息（如“共？页，？条记录”） ----------------- */
        settings.pageWidget.append("<div class='total'>共<span>" + page.getTotalPageNum() + "</span>页 <span>" + page.getTotalRowSize() + "</span>条记录</div>");
        settings.pageWidget.append("<ul>");
        /* --------------- 1.结束：构建描述信息（如“共？页，？条记录”） ----------------- */

        /* --------------- 2.开始：构建“首页”和“上一页”控件 ------------- */
        var currPageNum = Number(page.getCurrPageNum());
        var totalPageNum = Number(page.getTotalPageNum());

        if (currPageNum == 1) {
            //如果你希望当前页是第一页的时候，也允许“首页”和“上一页”控件出现，则可以在这里进行补充
        } else {
            settings.pageWidget.find("ul").append("<li><a id='' pageNum='1' href='javascript:void(0);' title='首页' class='first'>首页</a></li>");
            settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + (currPageNum - 1) + "' href='javascript:void(0);' title='上一页' class='prev'>上一页</a></li>");
        }
        /* --------------- 2.结束：构建“首页”和“上一页”控件 ------------- */

        /* --------------- 3.开始：构建分页数字控件 -------------- */
        if (totalPageNum < 10) {
            for (var i = 1; i <= totalPageNum; i++) {
                if (i == currPageNum) {
                    settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class='current'>" + i + "</a></li>");
                } else {
                    settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class='javascript:trunTargetPage(" + i + ");'>" + i + "</a></li>");
                }
            }
            //如果总页数>=10
        } else {
            //如果当前页小于5，则显示1-9项，且记忆当前项
            if (currPageNum < 5) {
                for (var i = 1; i < 10; i++) {
                    if (i == currPageNum) {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class='current'>" + i + "</a></li>");
                    } else {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class=''>" + i + "</a></li>");
                    }
                }
                //如果当前页>=5，且总页数与当前页的差<4
            } else if (totalPageNum - currPageNum < 4) {
                for (var i = totalPageNum - 8; i <= totalPageNum; i++) {
                    if (i == currPageNum) {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class='current'>" + i + "</a></li>");
                    } else {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class=''>" + i + "</a></li>");
                    }
                }
                //如果当前页>=5，则显示围绕当前页的9项，且记忆当前项
            } else {
                for (var i = currPageNum - 4; i < currPageNum + 5; i++) {
                    if (i == currPageNum) {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(0);' title='' class='current'>" + i + "</a></li>");
                    } else {
                        settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + i + "' href='javascript:void(" + i + ");' title='' class=''>" + i + "</a></li>");
                    }
                }
            }
        }
        /* --------------- 3.结束：构建分页数字控件 -------------- */

        /* --------------- 4.开始：构建“下一页”和“尾页”控件 ------------- */
        if (totalPageNum == currPageNum) {
            //如果你希望当前页是最后一页的时候，也允许“尾页”和“下一页”控件出现，则可以在这里进行补充
        } else {
            settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + (currPageNum + 1) + "' href='javascript:void(0);' title='下一页' class='next'>下一页</a></li>");
            settings.pageWidget.find("ul").append("<li><a id='' pageNum='" + totalPageNum + "' href='javascript:void(0);' title='尾页' class='last'>尾页</a></li>");
        }
        /* --------------- 4.结束：构建“下一页”和“尾页”控件 ------------- */

        //还要为控件绑定点击事件
        bindEvent();
    }

})(jQuery);

/*
 * Page类
 */
function AsynPage(currPageNum, totalRowSize, pageRowSize) {
    this.currPageNum = currPageNum;
    this.totalRowSize = totalRowSize;
    this.pageRowSize = pageRowSize;
}

AsynPage.prototype.getCurrPageNum = function () {
    return this.currPageNum;
};
AsynPage.prototype.setCurrPageNum = function (currPageNum) {
    this.currPageNum = currPageNum;
};
AsynPage.prototype.getTotalPageNum = function () {
    return (this.totalRowSize % this.pageRowSize == 0) ? (this.totalRowSize / this.pageRowSize) : (Math.floor(this.totalRowSize / this.pageRowSize) + 1);
};
AsynPage.prototype.getTotalRowSize = function () {
    return this.totalRowSize;
};
AsynPage.prototype.setTotalRowSize = function (totalRowSize) {
    this.totalRowSize = totalRowSize;
};
AsynPage.prototype.getPageRowSize = function () {
    return this.pageRowSize;
};
AsynPage.prototype.setPageRowSize = function (pageRowSize) {
    this.pageRowSize = pageRowSize;
};
AsynPage.prototype.getFirstResult = function () {
    if (this.getCurrPageNum() <= 0) return 0;
    return this.getPageRowSize() * (this.getCurrPageNum() - 1);
};
AsynPage.prototype.getMaxResult = function () {
    return this.getFirstResult() + this.getPageRowSize();
};
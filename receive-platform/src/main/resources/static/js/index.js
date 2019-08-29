var mainLayout;
$(function () {
    // 禁止右键菜单
    // $("body").bind("contextmenu", function () {
    //     return false;
    // });

    // tabs
    contentContainerTabs = $("#content").tabs(tabsSettings);
    var ui_tabs_nav = contentContainerTabs.find(".ui-tabs-nav");
    ui_tabs_nav.disableSelection();
    $("#content span.ui-icon-close").live("click", function () {
        var index = $("li", contentContainerTabs).index($(this).parent());
        contentContainerTabs.tabs("remove", index);
    });

    // init mainLayout
    mainLayout = $('body').layout(layoutSettings);
    resizecontent();

    // init menu
    initMenu();

    //默认打开首页
    $("#homeIframe").attr("src", getRootPath() + "/index/home");

    getPermMenu();
    clickPermMenu();
    modifyPassword();
    editUserDetails();
});

//获取权限菜单
function getPermMenu() {
    var layer = layui.layer;
    $.get(getRootPath() + '/index/getPermMenu?ran=' + new Date().getTime()).then(function (res) {
        if (res.code == 0) {
            $.each(res.data, function (index, item) {
                var parentId = item.parentId;
                if (parentId == 0) {
                    // var title = ` <div class="title">
                    //            <div class="title-content" data-id="${item.id}">${item.name}</div> </div>`;
                    var title = "<div class=\"title\">" +
                        "<div class=\"title-content\" data-id=\"" + item.id + "\">" + item.name + "</div> </div>";
                    $(".menu-li").append(title).append("<ul class=\"title-ul\" data-id=\"" + item.id + "\"></ul>");
                } else {
                    // var li = `<li>
                    //  <a href="javascript:;" class="menu-req" id="${item.code}" data-code="${item.code}"  data-name="${item.name}" data-url="${item.url}">${item.name}</a>
                    //  </li>`;
                    var li = "<li>" +
                        "<a href=\"javascript:;\" class=\"menu-req\" id=\"" + item.code + "\" data-code=\"" + item.code + "\"  data-name=\"" + item.name + "\" data-url=\"" + item.url + "\">" + item.name + "</a></li>";
                    $('.title-ul[data-id=' + parentId + ']').append(li);
                }
            });
        } else {
            layer.alert(res.message, {
                skin: 'layui-layer-lan' //样式类名
                , closeBtn: 0
            }, function () {
                top.location.href = getRootPath() + "/login/toLogin";
            });
            return false;
        }
    });
}

//点击菜单
function clickPermMenu() {
    $('.menu-li').on('click', '.menu-req', function () {
        var id = $(this).attr("data-code");
        var title = $(this).attr("data-name");
        var url = $(this).attr("data-url");
        openPageInContent(id, title, getRootPath() + url);
    });
}


/* tab 设置 */
var tabsSettings = {
    tabTemplate: "<li><a href='#{href}'>#{label}</a><span title='关闭' class='ui-icon ui-icon-close'>x</span></li>",
    add: function (event, ui) {
        $(ui.panel).append(contentContainerTabs.curr_add_content ? contentContainerTabs.curr_add_content : "error");
        $(this).tabs('select', '#' + ui.panel.id);
        resizecontent();
        return;
    },
    remove: function (event, ui) {
        resizecontent();
    },
    show: function (event, ui) {
        try {
            $(window.frames["#" + ui.panel.id])[0].mainLayout()
        } catch (e) {
        }
    }
};
/* layout 设置 */
var layoutSettings = {
    defaults: {
        resizable: false,
        slidable: false,
        spacing_open: 3,
        spacing_closed: 0,
        togglerLength_open: 0
    },
    north: {
        size: 65
    },
    west: {
        size: 200
    },
    center: {
        onresize: "resizecontent()",
        size: 'auto'
    }
};

/* 自适应content */
function resizecontent() {
    var $contentdiv = $("#content-container");
    $contentdiv.width($(".ui-layout-center").width());
    $contentdiv.height($(".ui-layout-center").height() - $(".ui-layout-center #content-header").height());
    $contentdiv = null;
}

function initMenu() {
    // $('#menu li:not(.unshrink) ul').hide();
    $('#menu li ul').hide();
    $('#menu li div.title').attr('title', '展开菜单项').click(function () {
        //$('#menu li:not(.unshrink) div.title').click(function() {
        var checkElement = $(this).next();
        if ((checkElement.is('ul')) && (checkElement.is(':visible'))) {
            checkElement.slideUp(100);
            $(this).attr('title', '展开菜单项');
            return false;
        }
        if ((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
            // $('#menu li:not(.unshrink) ul').not(checkElement).slideUp(100);
            checkElement.slideDown(100);
            $(this).attr('title', '收起菜单项');
            return false;
        }
    });
    // $('#menu li:not(.unshrink) div.title').first().click();
    $('#menu li:first div.title').click();
    // $('#menu li div.title').click();
};

/**
 * 在主页的tab中打开一个网页
 */
function openPageInContent(id, title, url) {
    var tabId = "#ui-tab-";
    if (contentContainerTabs) {
        if (id) {
            tabId += id;
            if ($(tabId).length > 0) {
                // exist
                //contentContainerTabs.tabs("remove", tabId);
                //openPageInContent(id,title,url);
                contentContainerTabs.tabs("select", "" + id);
                //$('#\\'+tabId).get(0).contentWindow.location.reload();
                $('#\\' + tabId).attr('src', url.indexOf('?') != -1 ? (url + "&c=" + Math.random()) : (url + "?c=" + Math.random()));
                return;
            }
        }
        var maxLen = 8;
        var tab_content = "<iframe frameborder='0' id='" + tabId + "' scrolling='auto'  width='100%' height='100%' ></iframe>";
        contentContainerTabs.curr_add_content = tab_content;
        contentContainerTabs.tabs("add", tabId, title.length <= maxLen ? title : ("<span title=\"" + title + "\">" + title.substring(0, maxLen) + "...</span>"));
        $('#\\' + tabId).attr('src', url);

    }
}

//tips
artDialog.notice = function (options) {
    var opt = options || {},
        api, aConfig, hide, wrap, top,
        duration = 600;
    var config = {
        id: 'Notice',
        left: '100%',
        top: '100%',
        fixed: true,
        drag: false,
        resize: false,
        follow: null,
        lock: false,
        init: function (here) {
            api = this;
            aConfig = api.config;
            wrap = api.DOM.wrap;
            top = parseInt(wrap[0].style.top);
            hide = top + wrap[0].offsetHeight;

            wrap.css('top', hide + 'px')
                .animate({top: top + 'px'}, duration, function () {
                    opt.init && opt.init.call(api, here);
                });
        },
        close: function (here) {
            wrap.animate({top: hide + 'px'}, duration, function () {
                opt.close && opt.close.call(this, here);
                aConfig.close = $.noop;
                api.close();
            });
            return false;
        }
    };

    for (var i in opt) {
        if (config[i] === undefined) config[i] = opt[i];
    }
    ;
    return artDialog(config);
};


/* 用户退出 */
function logout() {
    var layer = layui.layer;
    layer.confirm('您确定要退出吗？', {
        btn: ['确认', '返回'] //按钮
    }, function () {
        location.href = getRootPath() + "/login/quit";
        // $.get(getRootPath() + "/login/quit", function (data) {
        //     if (data.code == 0) {
        //         location.href = getRootPath() + "/login/toLogin";
        //     } else {
        //         layer.alert(data, function () {
        //             layer.closeAll();
        //         });
        //     }
        // });
    }, function () {
        layer.closeAll();
    });
}

/* 修改密码 */
function openModifyPwd() {
    var layer = layui.layer;
    layer.open({
        type: 1,
        title: '修改密码',
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['400px'],
        content: $('#releasePassword'),
        end: function () {
            $('#releasePassword').css("display", "none");
            $('#releasePassword input').val("");
        }
    });
}

/* 修改密码 */
function modifyPassword() {
    var layer = layui.layer
        , form = layui.form;
    form.on('submit(userSubmit)', function (data) {
        $.post(getRootPath() + '/index/modifyPassword', data.field).then(function (res) {
            if (res.code == 0) {
                top.location.href = getRootPath() + '/login/toLogin'
            } else {
                //弹出错误提示
                layer.msg(res.message, {
                    time: 2000, // 2s后自动关闭
                });
            }
        });
        return false;
    });
}


function openUserDetails() {
    getUserDetails();
    var layer = layui.layer;
    layer.open({
        type: 1,
        title: '用户详情',
        fixed: false,
        resize: false,
        shadeClose: true,
        area: ['600px'],
        content: $('#userDetails'),
        end: function () {
            $('#userDetails').css("display", "none");
        }
    });
}

function getUserDetails() {
    var layer = layui.layer;
    $.get(getRootPath() + '/user/getUserSerf').then(function (res) {
        if (res.code == 0) {
            var user = res.data;
            $('.userId').val(user.userId).addClass('input-disabled');
            $('.nickName').val(user.nickName);
            $('.telephone').val(user.telephone);
            $('.email').val(user.email);
            $('.state').val(user.state == 0 ? '禁用' : '可用').addClass('input-disabled');
            $('.createTime').val(user.createTimeStr).addClass('input-disabled');
            $('.updateTime').val(user.updateTimeStr).addClass('input-disabled');
            $('.input-disabled').attr('disabled', true).removeAttr('name');
        } else {
            layer.alert(data, function () {
                layer.closeAll();
            });
        }
    });
}

function editUserDetails() {
    var layer = layui.layer
        , form = layui.form;
    form.on('submit(editUser)', function (data) {
        $.post(getRootPath() + '/user/editUserSerf', data.field).then(function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg(res.message, {
                    time: 2000, // 2s后自动关闭
                });
                return false;
            } else {
                layer.alert(res.message, function () {
                    layer.closeAll();
                });
            }
        });
        return false;
    });
}


function downLoadText() {
    var layer = layui.layer;
    layer.confirm('您确定下载操作指南吗？', {
        btn: ['确认', '返回'] //按钮
    }, function () {
        location.href = getRootPath() + '/file/移动警务服务总线技术参考指南(试行).zip';
        layer.closeAll();
    }, function () {
        layer.closeAll();
    });
}

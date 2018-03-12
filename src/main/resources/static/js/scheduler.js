/**
 * 作业调度处理js
 *
 * @author Song Lea
 */

$(function () {

    // 是否为修改作业
    var EDIT = false;
    var urlPath = $('#urlPath').val();
    var $table = $('#schedulerTableBody');
    var $addBtn = $('#addBtn');
    var $editBtn = $('#editBtn');
    var $operateBtn = $('#operateBtn');
    var $removeBtn = $('#removeBtn');
    var $runOnce = $('#runOnce');

    // 加载数据表格
    bootstrapTableConfig($table, $editBtn, $operateBtn, $removeBtn, $runOnce, urlPath);

    // 新增作业按钮
    var $jobOperateModal = $('#jobOperateModal');
    var $configForm = $('#schedulerConfigForm');
    var $tipName = $('#tipName');
    var $jobName = $('#jobName');
    var $jobGroup = $('#jobGroup');
    $addBtn.click(function () {
        $configForm[0].reset();
        $tipName.text('新增作业');
        $jobOperateModal.modal('show');
        $jobName.prop('disabled', false);
        $jobGroup.prop('disabled', false);
        EDIT = false;
    });

    // 修改作业按钮
    $editBtn.click(function () {
        var _select = $table.bootstrapTable('getSelections');
        if (_select.length === 0) {
            art.dialog({content: '请选择一条作业！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        $configForm[0].reset();
        $tipName.text('修改作业');
        $jobOperateModal.modal('show');
        $jobName.prop('disabled', true);
        $jobGroup.prop('disabled', true);
        $jobName.val(_select[0]['jobName']);
        $jobGroup.val(_select[0]['jobGroup']);
        $('#cron').val(_select[0]['cron']);
        $('#targetClass').val(_select[0]['targetClass']);
        $('#jobDescription').val(_select[0]['jobDescription']);
        $('#contactEmails').val(_select[0]['contactEmails']);
        $('#successEmails').val(_select[0]['successEmails']);
        EDIT = true;
    });

    // 执行或暂停作业按钮
    $operateBtn.click(function () {
        var _select = $table.bootstrapTable('getSelections');
        if (_select.length === 0) {
            art.dialog({content: '请选择一条作业！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        art.dialog({
            title: '提示',
            content: '您确定要执行/暂停所选作业吗？',
            icon: 'question',
            lock: true,
            opacity: 0.6,
            ok: function () {
                $.ajax({
                    type: 'POST',
                    url: urlPath + '/api/tms/schedule/handler/operateJob',
                    data: {
                        jobName: _select[0]['jobName'],
                        jobGroup: _select[0]['jobGroup']
                    },
                    dataType: "text",
                    success: function (resp) {
                        art.dialog({content: resp, title: '提示', icon: 'face-smile', time: 2});
                        if (resp === '操作成功！') {
                            $table.bootstrapTable('refresh');
                        }
                    },
                    error: function () {
                        art.dialog({content: '作业执行/暂停失败！', title: '提示', icon: 'face-sad', time: 2});
                    }
                });
            },
            cancel: true
        });
    });

    // 只执行一次时的统计日期选择框
    var $statisticsDate = $('#statisticsDate');
    $statisticsDate.datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        autoclose: true,
        endDate: getYesterday(),
        language: 'zh-CN'
    });
    $statisticsDate.datepicker('setDate', getYesterday());

    // 只执行一次按钮
    $('#runOnceBtn').click(function () {
        var _select = $table.bootstrapTable('getSelections');
        if (_select.length === 0) {
            art.dialog({content: '请选择一条作业！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        var _statisticsDate = $statisticsDate.val();
        if (_statisticsDate === '') {
            art.dialog({content: '请选择统计日期！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        $.ajax({
            type: 'POST',
            url: urlPath + '/api/tms/schedule/handler/runJobOnce',
            data: {
                jobName: _select[0]['jobName'],
                jobGroup: _select[0]['jobGroup'],
                targetClass: _select[0]['targetClass'],
                jobDescription: _select[0]['jobDescription'],
                contactEmails: _select[0]['contactEmails'],
                successEmails: _select[0]['successEmails'],
                statisticsDate: _statisticsDate
            },
            dataType: "text",
            success: function (resp) {
                art.dialog({content: resp, title: '提示', icon: 'face-smile', time: 2});
                if (resp === '操作成功！') {
                    $('#runOnceModal').modal('hide');
                    $table.bootstrapTable('refresh');
                }
            },
            error: function () {
                art.dialog({content: '作业执行一次失败！', title: '提示', icon: 'face-sad', time: 2});
            }
        });
    });

    // 删除作业按钮
    $removeBtn.click(function () {
        var _select = $table.bootstrapTable('getSelections');
        if (_select.length === 0) {
            art.dialog({content: '请选择一条作业！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        art.dialog({
            title: '提示',
            content: '您确定要删除所选作业吗？',
            icon: 'question',
            lock: true,
            opacity: 0.6,
            ok: function () {
                $.ajax({
                    type: 'POST',
                    url: urlPath + '/api/tms/schedule/handler/removeJob',
                    data: {
                        jobName: _select[0]['jobName'],
                        jobGroup: _select[0]['jobGroup']
                    },
                    dataType: "text",
                    success: function (resp) {
                        art.dialog({content: resp, title: '提示', icon: 'face-smile', time: 2});
                        if (resp === '操作成功！') {
                            $table.bootstrapTable('refresh');
                        }
                    },
                    error: function () {
                        art.dialog({content: '作业删除失败！', title: '提示', icon: 'face-sad', time: 2});
                    }
                });
            },
            cancel: true
        });
    });

    // 表格的确认提交
    $('#submitBtn').click(function () {
        var jobName = $.trim($jobName.val());
        if (jobName === '') {
            art.dialog({content: '作业名不能为空！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        var cron = $.trim($('#cron').val());
        if (cron === '') {
            art.dialog({content: '调度表达式不能为空！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        var targetClass = $.trim($('#targetClass').val());
        if (targetClass === '') {
            art.dialog({content: '作业类不能为空！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        var suffix = EDIT ? 'editJob' : 'addJob';
        $.ajax({
            type: 'POST',
            url: urlPath + '/api/tms/schedule/handler/' + suffix,
            data: {
                jobName: jobName,
                jobGroup: $jobGroup.val(),
                cron: cron,
                targetClass: targetClass,
                jobDescription: $('#jobDescription').val(),
                contactEmails: $('#contactEmails').val(),
                successEmails: $('#successEmails').val()
            },
            dataType: "text",
            success: function (resp) {
                art.dialog({content: resp, title: '提示', icon: 'face-smile', time: 2});
                if (resp === '操作成功！') {
                    $jobOperateModal.modal('hide');
                    $table.bootstrapTable('refresh');
                }
            },
            error: function () {
                art.dialog({content: '作业添加/修改失败！', title: '提示', icon: 'face-sad', time: 2});
            }
        });
    });

    // 退出系统按钮
    $('#logoutBtn').click(function () {
        art.dialog({
            title: '提示',
            content: '您确定要退出吗？',
            icon: 'question',
            lock: true,
            opacity: 0.6,
            ok: function () {
                window.location.href = urlPath + '/api/tms/schedule/sign/out';
            },
            cancel: true
        });
    });

    // 请求完成时根据返回来判断是否要重新登录
    $(document).ajaxComplete(function (event, xhr) {
        if (xhr.responseText && xhr.responseText.indexOf("No Login") !== -1) {
            art.dialog({content: '登录过期，请重新登录！', title: '提示', icon: 'face-sad', time: 2});
            setTimeout(function () {
                window.location.href = urlPath + '/api/tms/schedule/sign/index';
            }, 3000);
        }
    });

    // 开始日期选择框
    var $startDate = $('#startDate');
    $startDate.datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        autoclose: true,
        endDate: getYesterday(),
        language: 'zh-CN'
    });

    // 结束日期选择框
    var $endDate = $('#endDate');
    $endDate.datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        autoclose: true,
        endDate: getYesterday(),
        language: 'zh-CN'
    });

    // 时间段作业的实现
    function runZoneImpl(_startDate, _endDate, type) {
        $.ajax({
            type: 'GET',
            url: urlPath + '/api/tms/schedule/handler/runZone',
            data: {
                type: type,
                startDate: _startDate,
                endDate: _endDate
            },
            dataType: "text",
            success: function (resp) {
                $('#jobZoneModal').modal('hide');
                art.dialog({content: resp, title: '提示', icon: 'face-smile', ok: true, lock: true, opacity: 0.6});
            },
            error: function () {
                art.dialog({content: '执行时间段作业失败！', title: '提示', icon: 'face-sad', time: 2});
            }
        });
    }

    // 执行时间段确认--发车与运输报表
    $('#runZoneBtn').click(function () {
        var _startDate = $.trim($startDate.val());
        if (_startDate === '') {
            art.dialog({content: '请选择开始日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        var _endDate = $.trim($endDate.val());
        if (_endDate === '') {
            art.dialog({content: '请选择结束日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        if (_endDate < _startDate) {
            art.dialog({content: '结束日期不能早于开始日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        runZoneImpl(_startDate, _endDate, 1);
    });

    // 执行时间段确认--利润报表
    $('#runZoneBtnProfit').click(function () {
        var _startDate = $.trim($startDate.val());
        if (_startDate === '') {
            art.dialog({content: '请选择开始日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        var _endDate = $.trim($endDate.val());
        if (_endDate === '') {
            art.dialog({content: '请选择结束日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        if (_endDate < _startDate) {
            art.dialog({content: '结束日期不能早于开始日期！', title: '提示', icon: 'face-sad', time: 2});
            return;
        }
        runZoneImpl(_startDate, _endDate, 2);
    });

    // 订阅后台作业完成通知
    var sock = new SockJS(urlPath + "/endpointJob");
    var stomp = Stomp.over(sock);
    stomp.connect({}, function () {
        stomp.subscribe("/topic/finishJob", function (message) {
            art.dialog.notice({
                title: '作业完成通知',
                content: '您好：<br>' + message.body,
                icon: 'succeed',
                ok: function () {
                    $table.bootstrapTable('refresh');
                }
            });
        });
    });

    // 模态框打开时加载日志文件(只加载最后2000行)
    $('#logModal').on('show.bs.modal', function () {
        $.ajax({
            type: 'GET',
            url: urlPath + '/api/tms/schedule/handler/scrollLogInfo',
            dataType: "json",
            success: function (resp) {
                if (resp) {
                    var $logContent = $('#logContent');
                    $('#logDate').text(resp.date); // 日期显示
                    $logContent.html(resp.content); // 采集的日志内容
                    $logContent.scrollTop($logContent[0].scrollHeight);
                }
            },
            error: function () {
                art.dialog({content: '加载日志详情失败！', title: '提示', icon: 'face-sad', time: 2});
            }
        });
    });

    // 下载日志文件的日期选择
    var $logDownloadDate = $('#logDownloadDate');
    $logDownloadDate.datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        autoclose: true,
        endDate: new Date(),
        language: 'zh-CN'
    });
    $logDownloadDate.datepicker('setDate', getToday());

    // 下载日志文件
    $('#logDownloadBtn').click(function () {
        var _fileDate = $logDownloadDate.val();
        if (_fileDate === '') {
            art.dialog({content: '请选择日志日期！', title: '提示', icon: 'face-smile', time: 2});
            return;
        }
        var dFrame = $("<iframe>");
        dFrame.attr("style", "display: none");
        dFrame.attr("name", "downloadFrame");
        dFrame.attr("src", urlPath + '/api/tms/schedule/handler/downloadLogFile?fileDate=' + _fileDate);
        $("body").append(dFrame);
        $('#logDownloadModal').modal('hide');
    });

    // 构建echart图形
    var myChart = echarts.init(document.getElementById('chartDiv'), 'shine');
    myChart.showLoading({
        text: '正在努力的读取数据中...',
        effect: 'whirling'
    });
    $.ajax({
        type: 'GET',
        url: urlPath + '/api/tms/schedule/handler/getChartData',
        dataType: "json",
        success: function (resp) {
            if (resp) {
                myChart.setOption(buildChartOption(resp['xAxisData'], resp['sendCarData'], resp['profitData']));
            }
            myChart.hideLoading();
        }
    });

    // 图表大小自动改变,函数节流
    window.onresize = function () {
        var timer = null;
        clearTimeout(timer);
        timer = setTimeout(function () {
            myChart.resize();
        }, 100);
    };
});

function getYesterday() {
    var date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate() - 1);
}

function getToday() {
    var date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

// 构造表格数据
function bootstrapTableConfig($table, $editBtn, $operateBtn, $removeBtn, $runOnce, urlPath) {
    var stickyHeaderOffsetY = 0;
    var $navbarFixedTop = $('.navbar-fixed-top');
    if ($navbarFixedTop.css('height')) {
        stickyHeaderOffsetY = +$navbarFixedTop.css('height').replace('px', '');
    }
    if ($navbarFixedTop.css('margin-bottom')) {
        stickyHeaderOffsetY += +$navbarFixedTop.css('margin-bottom').replace('px', '');
    }
    $table.bootstrapTable({
        striped: true,
        url: urlPath + '/api/tms/schedule/handler/getJobs',
        pagination: false,
        showRefresh: true,
        showColumns: false,
        showToggle: false,
        idField: 'jobName',
        toolbar: '#toolbar',
        clickToSelect: true,
        singleSelect: true,
        checkboxHeader: false,
        maintainSelected: false,
        stickyHeader: true,
        searchTimeOut: 500,
        searchText: '',
        sortable: true,
        formatSearch: function () {
            return '请输入作业名搜索';
        },
        search: true,
        stickyHeaderOffsetY: stickyHeaderOffsetY + 'px',
        columns: [
            {checkbox: true, align: 'center'},
            {field: 'jobName', title: '作业名', align: 'center', searchable: true, sortable: true},
            {field: 'jobGroup', title: '作业组', align: 'center', searchable: false, sortable: true},
            {field: 'jobType', title: '作业类型', align: 'center', searchable: false, visible: false},
            {field: 'jobStatus', title: '作业状态', align: 'center', searchable: false, formatter: transformJobStatus},
            {field: 'cron', title: '调度表达式', align: 'center', searchable: false},
            {field: 'previousFireTime', title: '上次执行时间', align: 'center', searchable: false, sortable: true},
            {field: 'nextFireTime', title: '下次触发时间', align: 'center', searchable: false, sortable: true},
            {field: 'targetClass', title: '作业类路径', align: 'center', searchable: false},
            {field: 'calculateDate', title: '统计日期', align: 'center', searchable: false},
            {field: 'jobDescription', title: '作业描述', align: 'center', searchable: false},
            {field: 'contactEmails', title: '作业异常时通知邮箱', align: 'center', searchable: false},
            {field: 'successEmails', title: '作业执行后通知邮箱', align: 'center', searchable: false, visible: false}
        ],
        onLoadError: function () {
            $table.bootstrapTable('load', {total: 0, rows: []});
            art.dialog({content: '数据加载失败！', title: '提示', icon: 'face-sad', time: 2});
        }
    });
    $table.on('check.bs.table uncheck.bs.table', function () {
        var _len = !$table.bootstrapTable('getSelections').length;
        $editBtn.prop('disabled', _len);
        $operateBtn.prop('disabled', _len);
        $removeBtn.prop('disabled', _len);
        $runOnce.prop('disabled', _len);
    });
    $table.on('refresh.bs.table page-change.bs.table', function () {
        $editBtn.prop('disabled', true);
        $operateBtn.prop('disabled', true);
        $removeBtn.prop('disabled', true);
        $runOnce.prop('disabled', true);
    });
}

// 构造echarts的option
function buildChartOption(xAxisData, sendCarData, profitData) {
    return {
        title: {
            text: '发车与运输报表和利润报表作业统计的数据趋势图(近31天)',
            textStyle: {
                fontSize: 18,
                fontWeight: 'bolder',
                color: '#333' // 主标题文字颜色
            }
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['发车与运输报表', '利润报表'],
            orient: 'horizontal',
            x: 'center',
            y: 'top'
        },
        grid: {
            borderWidth: 0,
            x: 40,
            y: 70,
            x2: 50
        },
        toolbox: {
            show: true,
            feature: {
                mark: {show: false},
                dataView: {show: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [{
            type: 'category',
            name: "日期",
            scale: false,
            splitLine: {show: false},
            splitArea: {show: false},
            axisLabel: {interval: 0, rotate: 45},
            boundaryGap: true,
            data: xAxisData
        }],
        yAxis: [{
            type: 'value',
            name: '入库数',
            splitLine: {show: false},
            axisTick: {show: false},
            splitArea: {show: false}
        }],
        series: [{
            name: '发车与运输报表',
            type: 'line',
            data: sendCarData,
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: "top"
                    }
                }
            },
            markPoint: {
                data: [
                    {type: 'max', name: '最大值'},
                    {type: 'min', name: '最小值'}
                ]
            }
        }, {
            name: '利润报表',
            type: 'line',
            data: profitData,
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: "top"
                    }
                }
            },
            markPoint: {
                data: [
                    {type: 'max', name: '最大值'},
                    {type: 'min', name: '最小值'}
                ]
            }
        }]
    };
}

// 转换作业状态函数
function transformJobStatus(status) {
    var result = "";
    if (status === 'NORMAL') {
        result = "正常";
    } else if (status === 'PAUSED') {
        result = "暂停";
    } else if (status === 'ERROR') {
        result = "异常";
    } else if (status === 'BLOCKED') {
        result = "执行中";
    } else if (status === 'COMPLETE') {
        result = "已完成";
    } else {
        result = "未知";
    }
    return result;
}

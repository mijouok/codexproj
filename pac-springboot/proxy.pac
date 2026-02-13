function FindProxyForURL(url, host) {
  host = host.toLowerCase();

  // ======== 你的 HTTP 代理端口 ========
  var PROXY = "PROXY 127.0.0.1:1082";
  var DIRECT = "DIRECT";

  function inList(domain) {
    return dnsDomainIs(host, domain) || shExpMatch(host, "*." + domain);
  }

  // ======== 白名单：直连 ========
  var whitelist = [
    // --- 11对战平台 ---
    "5211game.com",
    "go.5211game.com",
    "olpassport.5211game.com",
    "war3.5211game.com",

    // --- KK对战平台 ---
    "kkdzpt.com",
    "create.kkdzpt.com",
    "reckfeng.com",
    "passport.reckfeng.com",

    // --- Search / Portal ---
    "baidu.com","bdimg.com","bdstatic.com",
    "so.com","360.cn","sm.cn",
    "sina.com.cn","sinaimg.cn","sohu.com",
    "163.com","126.net","netease.com","163yun.com",

    // --- Ecommerce / Local Life ---
    "taobao.com","tmall.com","alipay.com",
    "alicdn.com","aliyun.com","aliyuncs.com",
    "jd.com","360buyimg.com","jcloudcs.com",
    "pinduoduo.com","yangkeduo.com",
    "meituan.com","dianping.com","ele.me","suning.com",

    // --- Social / Content ---
    "qq.com","wechat.com","weixin.qq.com","qpic.cn","gtimg.com","tenpay.com",
    "weibo.com","weibocdn.com","weibo.cn",
    "xiaohongshu.com","xhslink.com",
    "zhihu.com","zhimg.com","douban.com",
    "bilibili.com","biliapi.net","hdslb.com",
    "douyin.com","bytedance.com","toutiao.com","ixigua.com",
    "kuaishou.com","hupu.com",

    // --- Maps / Travel ---
    "amap.com","map.baidu.com","api.map.baidu.com","map.qq.com",
    "ctrip.com","trip.com","ly.com","fliggy.com",

    // --- Payment ---
    "unionpay.com",

    // --- Cloud / Dev Common ---
    "qcloud.com","myqcloud.com","tencentcloud.com",
    "huaweicloud.com","baidubce.com",
    "qiniu.com","qiniucdn.com","upyun.com","leancloud.cn"
  ];

  for (var i = 0; i < whitelist.length; i++) {
    if (inList(whitelist[i])) return DIRECT;
  }

  // 其它默认走代理（兜底再给 DIRECT，避免代理挂了完全断网）
  return PROXY + "; " + DIRECT;
}

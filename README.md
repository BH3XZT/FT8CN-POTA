# FT8CN（POTA 特化版）

Developed by BG7YOZ and hosted by N0BOY · POTA edition maintained by BH3XZT

本仓库是 [N0BOY/FT8CN](https://github.com/N0BOY/FT8CN) 的 GitHub fork（派生仓库），基于上游 0.93 进行 POTA 特化改造。

Run FT8 natively on Android。上游原版下载见 [N0BOY/FT8CN Releases](https://github.com/N0BOY/FT8CN/releases)；
本仓库（POTA 特化）的发行版见 [BH3XZT/FT8CN-POTA Releases](https://github.com/BH3XZT/FT8CN-POTA/releases)。

## 致辞

首先，衷心感谢 **FT8CN 原作者 BG7YOZ** 的辛勤工作：FT8CN 把 FT8 的编解码、频谱瀑布、多品牌电台控制、日志管理等完整能力带到了 Android 手机上，并多年持续更新、开源共享；
也感谢 **N0BOY** 对项目的托管与维护，以及上游所有贡献者、翻译者和测试者。没有他们的工作，就不会有这个 POTA 特化版本。

本版本（v0.93.3）主要解决以下问题：

1. **POTA 户外通联缺少公园信息记录**：公园编号无法随 QSO 一起保存，也无法按公园编号和运行时间段导出 ADIF；
2. **日志网页陈旧且不便打开**：界面老旧、没有主题/配色切换，手机上打开或把网址传到其他设备不方便；
3. **上一版本遗留问题**：「合并界面：两个列表上下同屏」在横屏模式下闪退；切换界面颜色后 CQ、分享等功能键颜色不跟随；
4. **缺少回到原版的入口**：新增功能较多，需要一个“一键恢复原版”的入口。

## APP 端变化

- **界面颜色切换**：一键切换日间 / 夜间模式，夜间固定深蓝色；日间提供紫、蓝、青、绿、橙、红 6 种主题色，CQ、发送/暂停、分享、导出等功能键会一起变色；
- **界面布局可选**：合并界面支持「选项卡切换」「两个列表上下同屏」「呼叫控制条常驻底部」三种方式，并保留「原版界面（5 个底部标签）」；
- **解码与呼叫合并**：底部导航由 5 项精简为 4 项，单击解码消息即可呼叫并自动切换到呼叫面板；
- **列表交互**：单条消息由左右滑动改为「单击 = 呼叫、双击 = 删除」，长按菜单保留；
- **POTA 公园编号**：设置界面新增 POTA 公园编号栏，解码/呼叫页与通联记录页同步显示当前公园编号，每条 QSO 自动记录所属公园；
- **通联记录**：列表备注处显示该条 QSO 的 POTA 编号；
- **日志导出入口**：点击导出按钮后弹出窗口，显示日志网页网址、二维码，并提供「复制网址 / 打开」按钮；
- **一键恢复原版**：界面设置中一键复位布局、配色与 POTA 编号；
- **启动页说明**：版本日期下方分两行显示「界面调整|POTA特化」「调整人：BH3XZT」。

## 网页端变化

- **现代化界面**：本地日志网页改为深色科技风卡片布局，表格/表单响应式适配手机与桌面浏览器；
- **昼夜模式 + 颜色模式**：右上角可切换日间/夜间配色，并可在 6 种主题色间切换，选择保存在浏览器本地；
- **POTA 日志导出页**：按「公园编号 + 运行日期/时间」筛选并下载 ADIF，每条记录写入 `MY_POTA_REF`；自动按运行日期列出每日窗口，可逐日下载，并提供按公园编号统计；
- **首页**：POTA 卡片 + 功能入口卡片化；未设置公园编号时不显示 POTA 卡片；
- **二维码与复制网址**：APP 端弹窗提供二维码和复制网址按钮，同一局域网设备扫码即可打开日志网页。

## 下载与版本

- 最新发行版：**v0.93.3（POTA 特化）**，见 [Releases](https://github.com/BH3XZT/FT8CN-POTA/releases)
- 版本历史与改动明细：[CHANGELOG.md](CHANGELOG.md)
- 上游来源、许可证与同步说明：[UPSTREAM.md](UPSTREAM.md)
- 许可证：**MIT**（见 [LICENSE](LICENSE)）；第三方组件许可证见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)


```
免责声明：
   FT8CN旨在研究的目的，学习如何对FT8信号进行解码、发射等操作，不对使用者操作本APP所产生的后果负责。
   在中华人民共和国境内，使用FT8CN请遵守《中华人民共和国无线电管理条例》等相关规定。
   考虑到手机的性能和续航的限制，对信号的处理采用轻量化的运算，未做深度解码等处理。
   如有好的建议或问题可以提交到到”有问题要吐槽“。

Disclaimer
FT8CN aims to learn how to decode, transmit FT8 signal for research purposes, which is not responsible for the consequences caused by the user's operation.
Please comply with local laws and regulations when using FT8CN.
Considering the performance and endurance limitations of the mobile phone, the processing of the signal adopts lightweight operations instead of deep decoding and other processing.
Please click "FAQ" if you have good suggestions or questions .

BG7YOZ
2022-07-01

致敬：
   Steve Franke(K9AN)、Bill Somerville(G4WJS)、Joe Taylor(K1JT)，提出FT8和FT4协议（FT是Franke和Taylor的首字母），并在论文《The FT4 and FT8 Communication Protocols》详细介绍了FT4和FT8的设计初衷和在WSJT-X中的具体实现细节，成为完成本APP的根本指南。
   Karlis Goba(YL3JG)在代码的具体实现上提供了参考。
鸣谢：
   BG7YOY，在FT8CN开发阶段为我在无线电基本理论上作出指导，并为FT8CN设计了图标
   BG4IGX，在我刚刚入门业余无线电时为我在具体实践上作出指导。抖音上您可以搜到很多他的教学视频
   BD7MXN，帮助我对部分电台的连接控制做了一些测试，并提出改进建议
   BH2RSJ，帮助我建立了一个FT8CN测试群，为测试和后续改进提出了很多宝贵意见
   BH7ACO，帮助解决了某电台的驱动和相关的配置参数
   BG7IKK，帮助解决了只支持通过RTS控制PTT发射的电台的测试
   BI1NIZ，帮助注册账号，用于收集问题反馈和FAQ的功能
   BD3OOX以及石家庄业余无线电俱乐部，FT8CN的呼号地区归属数据提取至JTDX石家庄版，使呼号定位可以精确到中国的省级
   VR2UPU(BD7MJO)，在FT8的开发和使用经验上提供指导，并在多语言方面给予帮助
   BA2BI，在业余无线电的基础知识和通联的日志处理方面上给予帮助和指导
   BI3QXJ，在对某品牌系列电台的指令集上给予专业性的指导
   BG6TQD，在对某型号电台的指令集测试上给予帮助
   BG5CSS，提供某型号电台用于测试
   BG7YXN，提供某型号电台用于测试
   BG7YRB，对呼号规则运算提供帮助
   BG8KAH，提供设备用于测试
   BA7LVG、JE6WUD，完成日文的翻译校对工作
   BG6RI，帮助解决日志的信号报告问题
   SV1EEX，完成希腊文、西班牙文UI的翻译工作
   VR2VRC，帮助修正历史呼号读取规则
   BA7NQ，提供设备用于测试
   BD7MYM，对某型号的电台测试给予指导
   NØBOY，帮助提供Github源，以及翻译工作
   BG5JNT，帮助修正非标准呼号的识别问题
   BH3NEK，协助对某型号电台进行测试
   BG2ALB，协助对某型号电台进行测试
   BG6DRU，协助对某型号电台进行测试
   BG7NQF，提供某型号电台的隐藏指令，对一些设备做兼容性测试
   BH2VSQ，协助对某型号电台进行测试
   BG7YBW，协助对部分功能进行测试
   BH1RNN，协助对部分功能进行测试
   BG7BSM，协助对一些BUG进行调试
   BH4FTI，发现并协助对一些BUG进行调试
   BG8BXM（M哥），为FT8CN的使用做推广，抖音和B站上有很多他的教学视频
   BA7MFQ，为FT8CN的使用做推广，帮助测试
   BG2EFX，提供大数据量的日志用于测试
   DS1UFX，贡献(tr)uSDX audio over CAT代码
   BG8HT，提供某型号电台进行测试
   UB6LUM，帮助解决某型号电台的操作模式设置
   BG5VLI，贡献向Cloudlog和QRZ自动上传日志的代码
```

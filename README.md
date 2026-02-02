🧪 JAGO — Java AI GO!

我的 Java + AI 学习试验田
写代码、踩坑、调模型、顺便造点小轮子 🛠️

名字是 JAGO = Java AI GO! —— 没啥深意，就是喊一句：“Java 也能玩 AI，冲！”

这个仓库是我自己学 AI 的“试验田”：
- 有些是随手写的 Java 小工具（比如文件处理、JSON 转换、时间格式化……反正重复劳动我都想封装）
- 有些是 Spring AI Alibaba + 阿里云通义千问（Qwen） 的练习 demo（能跑就行，别笑我配置乱）
- 未来还想试试 AI Agent —— 让程序自己思考、调工具、干点活（目前还在看文档阶段 😅）

不是教程，不是框架，就是我的学习笔记 + 可运行代码。

📂 目前有啥？

jago/
├── java-utils/                 # 日常写代码顺手攒的小工具（StringHelper、FileUtils...）  
├── spring-ai-playground/  # Spring AI 动手区  
│   ├── qwen-simple/       # 最简单的 Qwen 调用（就一个 controller）   
│   ├── chat-with-history/ # 带对话历史的聊天 demo  
│   └── .env.example       # 别忘了填你的 DashScope API Key！  
└── notes/                 # 学习碎碎念（比如“今天终于搞懂了 PromptTemplate”）  

🔑 怎么跑起来？

1. 去 阿里云 DashScope 申请个 API Key
2. 复制 .env.example 为 .env，填上你的 key：
   env
   DASHSCOPE_API_KEY=sk-xxxxxx
    3. 进目录，启动（我用 Maven）：
       bash
       cd spring-ai-playground/qwen-simple
       ./mvnw spring-boot:run
    4. 浏览器打开：  
       http://localhost:8080/chat?input=今天天气怎么样？

💡 提示：第一次跑不通太正常了！我经常漏配环境变量、拼错 key、或者网络抽风……别慌，日志里找线索。

🧠 我在学什么？

- ✅ 用 Spring AI 调大模型（现在主攻阿里云 Qwen）
- ✅ Prompt 怎么写才不被模型“糊弄”
- ✅ 怎么把本地文档喂给 AI（RAG，还在啃）
- 🔜 让 AI 自己决定“下一步该干啥”（Agent，目标！）

所有代码都尽量 简单、可读、能跑 —— 不追求完美架构，先跑通再说。

📝 为什么公开？

- 方便自己在不同电脑同步
- 万一有人和我一样“从零开始摸 AI”，或许能省他 10 分钟
- 也欢迎路过的朋友提个 issue 说“你这写错了” or “可以这样改更好” 👀

🚧 免责声明

- 代码可能很糙，别当生产参考
- 配置可能过时（AI 工具更新太快了！）
- 但我保证：每一行都是我自己敲的，能跑（至少在我机器上）

❤️ 最后

JAGO 不是产品，是我的学习脚印。  
如果你也在用 Java 探索 AI ——  
咱们一起 GO！
“先跑起来，再优化；先学会，再创造。”  
—— @Sheven 一个普通的 Java 程序员
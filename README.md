# fix-env
如果你的 IDEA 无法正常调用 GPG as SSH agent 连接到 GitHub or somewhere else, 那么你可能需要这个 Javaagent.

具体原因如下：在 `~/.zshrc` 下配置的环境变量（也就是 GPG-as-SSH Agent）并不能被以 **.desktop** 文件形式启动的 IDEA 加载到，因此这个修复工具会在 IDEA 启动前调用/加载你的 zshrc 并且把它们填充回 IDEA 的环境变量中。

# Usage
在 vmoption 中加入一行即可: `-javaagent:/path/to/fix-env.jar`
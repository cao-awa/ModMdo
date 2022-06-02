## 连接相关
### Modmdo 连接
__服务器和服务器通信__
```sh
#连接服务器
/modmdo connection connect <IP> <Port>
#断开连接
/modmdo connection connections <name> disconnect
#显示流量传输
/modmdo connection connections <name> traffic
```
### 白名单
__永久白名单__
```sh
#添加 
使用 /temporary 后在五分钟内进入服务器即转为
#删除
/modmdo whitelist remove <name>
#列出已添加白名单的玩家
/modmdo whitelist list
```
__临时白名单__
```sh
#添加
/temporary whitelist add <name>
#删除
/temporary whitelist remove <name>
#列出已添加临时白名单的玩家
/temporary whitelist list
```


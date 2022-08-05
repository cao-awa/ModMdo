### 白名单
__永久白名单__
```sh
#添加 
使用临时白名单后在五分钟内进入服务器即转为永久
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
__封禁玩家__
```sh
#临时封禁 > 以分钟为单位
/temporary ban add <name> <time>
#永久封禁
/temporary ban add <name> -1
#显示被封禁的玩家
/temporary ban list
```

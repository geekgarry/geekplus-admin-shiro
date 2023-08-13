# geekplus-admin-shiro
Springboot后台管理系统,使用SpringBoot+mybatis+reids，权限框架采用shiro，逆向工程生成模版
项目中只有最基本的系统管理的部分，需要增加功能利用generator模版生成代码
权限管理部分采用shiro，用redis存储shiro的session，后期可以改造为jwt验证实现无状态的token认证
项目采取前后端分离的开发，前端使用vue框架开发
前端代码 https://github.com/geekgarry/geekplus-admin-vue
主要模块有用户管理，角色管理，菜单权限管理，系统通知，日志管理，部门管理

用户登录密码默认为123456

后端shiro+jwt实现认证授权版本 https://github.com/geekgarry/geekplus-admin-springboot-api

# SRecyclerView
有刷新和加载功能的RecyclerView</br>
博客地址：http://blog.csdn.net/hzwailll/article/details/75285924

主要功能有：
1. 下拉刷新，滑到底部加载（也支持GridGridLayoutManager）
2. 支持添加多个头部和尾部（也支持GridGridLayoutManager）
3. 支持代码设置一个刷新头部和加载尾部（满足局部特殊的刷新头部和加载尾部）
4. 支持自定义刷新头部和加载尾部，手势等逻辑已处理，你只需写刷新界面逻辑即可
5. 可以全局配置刷新头部和加载尾部，以及一些其他配置
6. 支持设置LinearLayoutManager的分割线，以及纵向时分割线的左右距离
7. 支持设置一个EmptyView
8. 支持Item的点击事件
9. 附带一个简易的适配器，大大减少适配器的代码
10. 默认设置为纵向的LinearLayoutManager

![image](https://github.com/HzwSunshine/SRecyclerView/blob/master/srecyclerview.gif)


# Download
use Gradle:&nbsp;&nbsp;&nbsp;&nbsp;
    [compile 'com.github.hzw:srecyclerview:1.1.4'](https://github.com/HzwSunshine/SRecyclerView)
  

# ProGuard
-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule


# Update History

> * 2017.9.6     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.4 </br>
修改添加头部和尾部的逻辑，可以在setAdapter之前或之后添加了

> * 2017.8.30    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.3 </br>
完善自动刷新时，手势操作可能引起的刷新异常

> * 2017.8.24    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.2 </br>
解决多手势下拉时的刷新问题，完善刷新加载逻辑，完善代码

> * 2017.7.20    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.1 </br>
完善下拉手势和加载更多的逻辑，完善测试类及注释

> * 2017.7.19    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.0 </br>
完善刷新逻辑，版本更新为1.1.0

> * 2017.7.18    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.0.0 </br>
完成SRecyclerView，测试并使用了一段时间。2017.7.18首次提交SRecyclerView到GitHub，同时提交到JCenter




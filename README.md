# 2048
作者：lzg</br>
qq：535903063</br>
上传时间：2017.9.19</br>
制作时间：8.4 - 8.29</br>
</br>
<h4>简介：</h4>
安卓View游戏框架制作的2048小游戏。使用自定义View实现，通过用户触摸事件来重绘view实现交互。</br>
动画效果根据计算动画剩余时长来重绘View，达到动画效果。</br>
![Image text](https://github.com/Greglin535903063/2048/raw/master/image/截图_游戏中.png)
![Image text](https://github.com/Greglin535903063/2048/blob/master/image/截图_关于游戏.png)
更多截图请看项目的image文件夹
</br>
<h4>功能：</h4>
游戏全部场景只有一个Activity和一个View。</br>
基本的游戏逻辑、算法。</br>
流畅的动画效果，自定义动画类实现。</br>
得分变化与动画、逻辑的同步。</br>
游戏结束与达到2048均有提示效果。</br>
在游戏退出或被销毁时通过SharedPreferences来保存游戏数据(得分、最高分、卡片布局等等数据)。</br>
有撤销、重新游戏、关于游戏三个按键。</br>
由于只是用于学习交流，并没有设置单局游戏可撤销次数，但不可连续撤销。</br>
撤销不可用时图标会变灰。</br>
当只能点击某些按钮时，该些按钮会变成橙色，便于提示。</br>
关于游戏介绍游戏的简单玩法和作者信息。</br>
调试无bug。</br>
</br>
<h4>运用到的知识点：</h4>
知识点比较少，重点是整体的框架、类的封装与逻辑、动画算法。</br>
View的游戏框架，被动式刷新。</br>
充分运用了面向对象、继承和封装的思想。</br>
安卓基础知识(Activity、自定义View)</br>
SharedPreferences</br>
IO流保存图片(注释掉了)</br>
事件监听：因为只有一个view，所以要处理的内容比较多。</br>
</br>
<h4>项目结构：</h4>
GameActivity ：游戏Activity</br>
Game2048View  ：游戏View，绘制游戏的内容</br>
GameListener  ：游戏触摸监听，处理各种触摸操作</br>
GameLogic ：游戏逻辑类，游戏算法</br>
Position  ：4*4布局的位置类</br>
Card  ：卡片类</br>
Animation ：动画单元类</br>
AnimationManager  ：动画管理类，对所有的动画单元进行管理</br>
Info ：关于游戏类，封装了游戏的介绍和动画效果

</br>
<h2>欢迎下载学习交流</h2>

